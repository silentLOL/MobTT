package at.stefanirndorfer.batmate.feature.home.impl.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.stefanirndorfer.batmate.core.ui.RetryTrigger
import at.stefanirndorfer.batmate.core.ui.catchUnavailable
import at.stefanirndorfer.batmate.core.ui.directory.PlayerDrillDown
import at.stefanirndorfer.batmate.core.ui.directory.filteredBy
import at.stefanirndorfer.batmate.core.ui.directory.models.DirectoryEntryUi
import at.stefanirndorfer.batmate.core.ui.directory.models.PlayerPath
import at.stefanirndorfer.batmate.core.ui.directory.models.PlayerStep
import at.stefanirndorfer.batmate.domain.club.DataUnavailable
import at.stefanirndorfer.batmate.domain.club.ListAssociationsUseCase
import at.stefanirndorfer.batmate.domain.club.ListLeaguesUseCase
import at.stefanirndorfer.batmate.domain.club.SearchClubsUseCase
import at.stefanirndorfer.batmate.domain.user.AddFavoriteUseCase
import at.stefanirndorfer.batmate.domain.user.Favorite
import at.stefanirndorfer.batmate.domain.user.ListLeaguePlayersUseCase
import at.stefanirndorfer.batmate.domain.user.ObserveFavoritesUseCase
import at.stefanirndorfer.batmate.domain.user.ObserveResolvedFavoritesUseCase
import at.stefanirndorfer.batmate.domain.user.ObserveUpcomingFixturesUseCase
import at.stefanirndorfer.batmate.domain.user.ObserveUserContextUseCase
import at.stefanirndorfer.batmate.domain.user.RemoveFavoriteUseCase
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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** [path] is null while the picker lists Clubs, and the position in the Player drill-down once it is browsing Players. */
private data class PickerInput(val query: String = "", val path: PlayerPath? = null)

/** What is fetched: only a change between Clubs and a drill-down step fetches again, never a keystroke. */
private sealed interface PickerSource {
    data object Clubs : PickerSource

    data class Players(val path: PlayerPath) : PickerSource
}

private class PickerResult(val source: PickerSource, val entries: List<DirectoryEntryUi>)

private fun PickerInput.source(): PickerSource = path?.let { PickerSource.Players(it) } ?: PickerSource.Clubs

class HomeViewModel(
    private val observeUserContext: ObserveUserContextUseCase,
    private val observeResolvedFavorites: ObserveResolvedFavoritesUseCase,
    private val observeUpcomingFixtures: ObserveUpcomingFixturesUseCase,
    private val observeFavorites: ObserveFavoritesUseCase,
    private val addFavorite: AddFavoriteUseCase,
    private val removeFavorite: RemoveFavoriteUseCase,
    private val searchClubs: SearchClubsUseCase,
    listAssociations: ListAssociationsUseCase,
    listLeagues: ListLeaguesUseCase,
    listLeaguePlayers: ListLeaguePlayersUseCase,
) : ViewModel() {
    private val drillDown = PlayerDrillDown(listAssociations, listLeagues, listLeaguePlayers)
    private val pickerInput = MutableStateFlow<PickerInput?>(null)
    private var loaded: PickerResult? = null
    private val retry = RetryTrigger()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val pickerResult: Flow<PickerResult?> = pickerInput
        .map { it?.source() }
        .distinctUntilChanged()
        .mapLatest { source -> source?.let { PickerResult(it, load(it)).also { result -> loaded = result } } }

    // The streams below end at a DataUnavailable; a retry restarts them all.
    private fun baseState(): Flow<HomeUiState> = combine(
        observeUserContext().map { it.toSelectionUi() }.catchUnavailable(HomeSelectionUi.Unavailable),
        observeResolvedFavorites().catchUnavailable(emptyList()),
        observeFavorites(),
        pickerInput,
        pickerResult,
    ) { selection, resolved, favorites, input, result ->
        HomeUiState(
            selection = selection,
            favorites = resolved,
            picker = input?.let { toPickerUi(it, result, favorites) },
        )
    }

    // Each retry shows no Fixtures (the loading state) until the new answer arrives.
    private fun fixtures(): Flow<UpcomingFixtures?> = observeUpcomingFixtures()
        .catchUnavailable<UpcomingFixtures?>(UpcomingFixtures.Unavailable)
        .onStart { emit(null) }

    val uiState: StateFlow<HomeUiState> = retry
        .restarting { combine(baseState(), fixtures()) { state, fixtures -> state.copy(fixtures = fixtures) } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), HomeUiState())

    private val eventChannel = Channel<HomeEvent>()
    val events: Flow<HomeEvent> = eventChannel.receiveAsFlow()

    fun onAction(action: HomeAction) {
        when (action) {
            HomeAction.ChooseClicked -> viewModelScope.launch { eventChannel.send(HomeEvent.OpenSettings) }
            HomeAction.AddFavoriteClicked -> pickerInput.value = PickerInput()
            HomeAction.ViewClubClicked -> viewModelScope.launch { eventChannel.send(HomeEvent.OpenMyClub) }
            HomeAction.RetryFixturesClicked -> retry.retry()
            is HomeAction.FavoriteClubClicked -> viewModelScope.launch { eventChannel.send(HomeEvent.OpenClub(action.clubId)) }
            is HomeAction.FavoritePlayerClicked -> viewModelScope.launch { eventChannel.send(HomeEvent.OpenPlayer(action.playerId)) }
            is HomeAction.FavoriteTeamClicked -> viewModelScope.launch { eventChannel.send(HomeEvent.OpenTeam(action.teamId)) }
            is HomeAction.RemoveFavoriteClicked -> viewModelScope.launch { removeFavorite(action.favorite) }
            is HomeAction.PickerQueryChanged -> pickerInput.update { it?.copy(query = action.query) }
            HomeAction.PickerBrowsePlayersClicked -> pickerInput.update { it?.copy(query = "", path = PlayerPath()) }
            is HomeAction.PickerPlayerClicked -> choosePlayerEntry(action.id)
            is HomeAction.PickerClubClicked -> toggle(Favorite.Club(action.id))
            HomeAction.PickerBackClicked -> pickerInput.update { goBack(it) }
            HomeAction.PickerDismissed -> pickerInput.value = null
        }
    }

    /** One step back through the Player drill-down; from its first step back to the Clubs, and from the Clubs the picker closes. */
    private fun goBack(input: PickerInput?): PickerInput? {
        val path = input?.path ?: return null
        return input.copy(query = "", path = path.back())
    }

    private fun choosePlayerEntry(id: String) {
        val input = pickerInput.value ?: return
        val path = input.path ?: return
        if (path.step == PlayerStep.PLAYER) {
            toggle(Favorite.Player(id))
            return
        }
        // An Association or League was chosen: the next step lists what is inside it.
        val entry = loaded?.takeIf { it.source == input.source() }?.entries?.firstOrNull { it.id == id } ?: return
        val next = path.enter(entry) ?: return
        pickerInput.update { current -> current?.takeIf { it.source() == input.source() }?.copy(query = "", path = next) ?: current }
    }

    /** An unreachable XTTV reads as no results rather than ending the picker's stream. */
    private suspend fun load(source: PickerSource): List<DirectoryEntryUi> = when (source) {
        PickerSource.Clubs -> try {
            searchClubs("").map { DirectoryEntryUi(it.id, it.name, subtitle = null, selected = false) }
        } catch (e: DataUnavailable) {
            emptyList()
        }
        is PickerSource.Players -> drillDown.load(source.path)
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

    private fun toPickerUi(input: PickerInput, result: PickerResult?, favorites: Set<Favorite>): FavoritePickerUi {
        // A result fetched for another step must not be shown, or its ids would be toggled as the wrong kind of Favorite.
        val entries = result?.takeIf { it.source == input.source() }?.entries.orEmpty().filteredBy(input.query)
        val path = input.path ?: return FavoritePickerUi(
            query = input.query,
            clubs = entries.map { it.copy(selected = Favorite.Club(it.id) in favorites) },
        )
        return FavoritePickerUi(
            query = input.query,
            clubs = emptyList(),
            step = path.step,
            breadcrumb = path.breadcrumb,
            entries = entries.map { it.copy(selected = path.step == PlayerStep.PLAYER && Favorite.Player(it.id) in favorites) },
        )
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
