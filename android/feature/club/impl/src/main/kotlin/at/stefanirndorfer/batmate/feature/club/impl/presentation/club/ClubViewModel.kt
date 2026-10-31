package at.stefanirndorfer.batmate.feature.club.impl.presentation.club

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.stefanirndorfer.batmate.core.ui.RetryTrigger
import at.stefanirndorfer.batmate.core.ui.catchUnavailable
import at.stefanirndorfer.batmate.core.ui.directory.AssociationClubPicker
import at.stefanirndorfer.batmate.domain.club.GetClubOverviewUseCase
import at.stefanirndorfer.batmate.domain.club.LineupPlayer
import at.stefanirndorfer.batmate.domain.club.ListAssociationClubsUseCase
import at.stefanirndorfer.batmate.domain.club.ListAssociationsUseCase
import at.stefanirndorfer.batmate.domain.player.Player
import at.stefanirndorfer.batmate.domain.user.AddFavoriteUseCase
import at.stefanirndorfer.batmate.domain.user.Favorite
import at.stefanirndorfer.batmate.domain.user.ObserveFavoritesUseCase
import at.stefanirndorfer.batmate.domain.user.ObserveUserContextUseCase
import at.stefanirndorfer.batmate.domain.user.RememberPlayerUseCase
import at.stefanirndorfer.batmate.domain.user.RemoveFavoriteUseCase
import at.stefanirndorfer.batmate.feature.club.impl.presentation.club.models.ClubAction
import at.stefanirndorfer.batmate.feature.club.impl.presentation.club.models.ClubEvent
import at.stefanirndorfer.batmate.feature.club.impl.presentation.club.models.ClubSource
import at.stefanirndorfer.batmate.feature.club.impl.presentation.club.models.ClubUiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.launch

class ClubViewModel(
    source: ClubSource,
    observeUserContext: ObserveUserContextUseCase,
    private val getClubOverview: GetClubOverviewUseCase,
    private val observeFavorites: ObserveFavoritesUseCase,
    private val addFavorite: AddFavoriteUseCase,
    private val removeFavorite: RemoveFavoriteUseCase,
    private val rememberPlayer: RememberPlayerUseCase,
    listAssociations: ListAssociationsUseCase,
    listAssociationClubs: ListAssociationClubsUseCase,
) : ViewModel() {
    private val retry = RetryTrigger()

    /** The Club found through the picker while the user has no My Club; it is shown but never saved as My Club. */
    private val pickedClubId = MutableStateFlow<String?>(null)
    private val picker = AssociationClubPicker(
        scope = viewModelScope,
        listAssociations = listAssociations,
        listAssociationClubs = listAssociationClubs,
        onClubChosen = { pickedClubId.value = it.id },
    )

    private val clubId: Flow<String?> = when (source) {
        ClubSource.Mine -> combine(observeUserContext().map { it.club?.id }, pickedClubId) { mine, picked -> mine ?: picked }
        is ClubSource.Specific -> flowOf(source.clubId)
    }

    val uiState: StateFlow<ClubUiState> = retry
        .restarting { states() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), ClubUiState.Loading)

    // My Club resolves through XTTV too, so the stream can end at a DataUnavailable; a retry restarts it.
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun states(): Flow<ClubUiState> = clubId
        .transformLatest { id ->
            if (id == null) {
                picker.state.collect { emit(ClubUiState.NoClub(it)) }
            } else {
                emit(ClubUiState.Loading)
                val state = load(id)
                if (state is ClubUiState.Content) {
                    observeFavorites().collect { favorites ->
                        emit(state.copy(isFavorite = Favorite.Club(id) in favorites))
                    }
                } else {
                    emit(state)
                }
            }
        }
        .catchUnavailable(ClubUiState.Error)

    private val eventChannel = Channel<ClubEvent>()
    val events: Flow<ClubEvent> = eventChannel.receiveAsFlow()

    fun onAction(action: ClubAction) {
        when (action) {
            ClubAction.AssociationRowClicked -> picker.onAssociationRowClicked()
            ClubAction.ClubRowClicked -> picker.onClubRowClicked()
            is ClubAction.PickerQueryChanged -> picker.onQueryChanged(action.query)
            is ClubAction.PickerEntryClicked -> picker.onEntryClicked(action.id)
            ClubAction.PickerDismissed -> picker.onDismissed()
            is ClubAction.TeamClicked -> viewModelScope.launch { eventChannel.send(ClubEvent.OpenTeam(action.teamId)) }
            is ClubAction.PlayerClicked -> openPlayer(action.player)
            ClubAction.RetryClicked -> retry.retry()
            ClubAction.FavoriteClicked -> toggleFavorite()
        }
    }

    private fun openPlayer(player: LineupPlayer) {
        val content = uiState.value as? ClubUiState.Content ?: return
        viewModelScope.launch {
            rememberPlayer(Player(player.playerId, player.name, content.overview.club.id))
            eventChannel.send(ClubEvent.OpenPlayer(player.playerId))
        }
    }

    private fun toggleFavorite() {
        val content = uiState.value as? ClubUiState.Content ?: return
        val favorite = Favorite.Club(content.overview.club.id)
        viewModelScope.launch {
            if (content.isFavorite) {
                removeFavorite(favorite)
            } else {
                addFavorite(favorite)
            }
        }
    }

    private suspend fun load(clubId: String): ClubUiState = try {
        getClubOverview(clubId)?.let(ClubUiState::Content) ?: ClubUiState.Error
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        ClubUiState.Error
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
