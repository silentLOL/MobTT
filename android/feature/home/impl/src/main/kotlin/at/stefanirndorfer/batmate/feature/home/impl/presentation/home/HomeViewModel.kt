package at.stefanirndorfer.batmate.feature.home.impl.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.stefanirndorfer.batmate.core.ui.directory.models.DirectoryEntryUi
import at.stefanirndorfer.batmate.domain.club.Club
import at.stefanirndorfer.batmate.domain.club.DataUnavailable
import at.stefanirndorfer.batmate.domain.club.SearchClubsUseCase
import at.stefanirndorfer.batmate.domain.user.AddFavoriteUseCase
import at.stefanirndorfer.batmate.domain.user.Favorite
import at.stefanirndorfer.batmate.domain.user.ObserveFavoritesUseCase
import at.stefanirndorfer.batmate.domain.user.ObserveResolvedFavoritesUseCase
import at.stefanirndorfer.batmate.domain.user.ObserveUpcomingFixturesUseCase
import at.stefanirndorfer.batmate.domain.user.ObserveUserContextUseCase
import at.stefanirndorfer.batmate.domain.user.PlayerSearchResult
import at.stefanirndorfer.batmate.domain.user.RemoveFavoriteUseCase
import at.stefanirndorfer.batmate.domain.user.SearchPlayersUseCase
import at.stefanirndorfer.batmate.domain.user.UpcomingFixtures
import at.stefanirndorfer.batmate.domain.user.UserContext
import at.stefanirndorfer.batmate.feature.home.impl.presentation.home.models.FavoritePickerUi
import at.stefanirndorfer.batmate.feature.home.impl.presentation.home.models.HomeAction
import at.stefanirndorfer.batmate.feature.home.impl.presentation.home.models.HomeEvent
import at.stefanirndorfer.batmate.feature.home.impl.presentation.home.models.HomeSelectionUi
import at.stefanirndorfer.batmate.feature.home.impl.presentation.home.models.HomeUiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private class PickerResult(
    val query: String,
    val players: List<PlayerSearchResult>,
    val clubs: List<Club>,
)

class HomeViewModel(
    private val observeUserContext: ObserveUserContextUseCase,
    private val observeResolvedFavorites: ObserveResolvedFavoritesUseCase,
    private val observeUpcomingFixtures: ObserveUpcomingFixturesUseCase,
    private val observeFavorites: ObserveFavoritesUseCase,
    private val addFavorite: AddFavoriteUseCase,
    private val removeFavorite: RemoveFavoriteUseCase,
    private val searchPlayers: SearchPlayersUseCase,
    private val searchClubs: SearchClubsUseCase,
) : ViewModel() {
    private val pickerQuery = MutableStateFlow<String?>(null)
    private val retryCount = MutableStateFlow(0)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val pickerResult: Flow<PickerResult?> = pickerQuery.mapLatest { query ->
        query?.let { search(it) }
    }

    // The streams below end at a DataUnavailable; a retry restarts them all.
    private fun baseState(): Flow<HomeUiState> = combine(
        observeUserContext().map { it.toSelectionUi() }.orElse(HomeSelectionUi.Unavailable),
        observeResolvedFavorites().orElse(emptyList()),
        observeFavorites(),
        pickerQuery,
        pickerResult,
    ) { selection, resolved, favorites, query, result ->
        HomeUiState(
            selection = selection,
            favorites = resolved,
            picker = query?.let { toPickerUi(it, result, favorites) },
        )
    }

    // Each retry shows no Fixtures (the loading state) until the new answer arrives.
    private fun fixtures(): Flow<UpcomingFixtures?> = observeUpcomingFixtures()
        .orElse<UpcomingFixtures?>(UpcomingFixtures.Unavailable)
        .onStart { emit(null) }

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<HomeUiState> = retryCount
        .flatMapLatest { combine(baseState(), fixtures()) { state, fixtures -> state.copy(fixtures = fixtures) } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), HomeUiState())

    private val eventChannel = Channel<HomeEvent>()
    val events: Flow<HomeEvent> = eventChannel.receiveAsFlow()

    fun onAction(action: HomeAction) {
        when (action) {
            HomeAction.ChooseClicked -> viewModelScope.launch { eventChannel.send(HomeEvent.OpenSettings) }
            HomeAction.AddFavoriteClicked -> pickerQuery.value = ""
            HomeAction.ViewClubClicked -> viewModelScope.launch { eventChannel.send(HomeEvent.OpenMyClub) }
            HomeAction.RetryFixturesClicked -> retryCount.value++
            is HomeAction.FavoriteClubClicked -> viewModelScope.launch { eventChannel.send(HomeEvent.OpenClub(action.clubId)) }
            is HomeAction.RemoveFavoriteClicked -> viewModelScope.launch { removeFavorite(action.favorite) }
            is HomeAction.PickerQueryChanged -> pickerQuery.value = action.query
            is HomeAction.PickerPlayerClicked -> toggle(Favorite.Player(action.id))
            is HomeAction.PickerClubClicked -> toggle(Favorite.Club(action.id))
            HomeAction.PickerDismissed -> pickerQuery.value = null
        }
    }

    private suspend fun search(query: String): PickerResult = try {
        PickerResult(query, searchPlayers(query), searchClubs(query))
    } catch (e: DataUnavailable) {
        PickerResult(query, emptyList(), emptyList())
    }

    /** Falls back to [fallback] and ends the stream when XTTV is unreachable and nothing is cached. */
    private fun <T> Flow<T>.orElse(fallback: T): Flow<T> = catch { e ->
        if (e is DataUnavailable) {
            emit(fallback)
        } else {
            throw e
        }
    }

    private fun toggle(favorite: Favorite) {
        viewModelScope.launch {
            if (favorite in observeFavorites().first()) {
                removeFavorite(favorite)
            } else {
                addFavorite(favorite)
            }
        }
    }

    private fun toPickerUi(query: String, result: PickerResult?, favorites: Set<Favorite>): FavoritePickerUi {
        // Results for an earlier query are kept until the new ones arrive, so the list does not flicker empty while typing.
        val players = result?.players.orEmpty().map {
            DirectoryEntryUi(it.player.id, it.player.name, it.clubName, selected = Favorite.Player(it.player.id) in favorites)
        }
        val clubs = result?.clubs.orEmpty().map {
            DirectoryEntryUi(it.id, it.name, subtitle = null, selected = Favorite.Club(it.id) in favorites)
        }
        return FavoritePickerUi(query, players, clubs)
    }

    private fun UserContext.toSelectionUi(): HomeSelectionUi = when (this) {
        UserContext.None -> HomeSelectionUi.None
        is UserContext.MyPlayer -> HomeSelectionUi.Player(player.name, club.name, association.name)
        is UserContext.MyClub -> HomeSelectionUi.Club(club.name, association.name)
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
