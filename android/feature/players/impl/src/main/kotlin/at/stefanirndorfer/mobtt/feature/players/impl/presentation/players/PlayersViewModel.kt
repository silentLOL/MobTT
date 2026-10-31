package at.stefanirndorfer.mobtt.feature.players.impl.presentation.players

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.stefanirndorfer.mobtt.core.analytics.AnalyticsKind
import at.stefanirndorfer.mobtt.core.analytics.AnalyticsScreen
import at.stefanirndorfer.mobtt.core.analytics.AnalyticsSource
import at.stefanirndorfer.mobtt.core.analytics.AnalyticsTracker
import at.stefanirndorfer.mobtt.core.analytics.NoOpAnalyticsTracker
import at.stefanirndorfer.mobtt.core.analytics.SearchEvents
import at.stefanirndorfer.mobtt.core.analytics.detailOpened
import at.stefanirndorfer.mobtt.core.analytics.myPlayerSelected
import at.stefanirndorfer.mobtt.core.analytics.screenView
import at.stefanirndorfer.mobtt.core.ui.directory.AssociationClubPicker
import at.stefanirndorfer.mobtt.core.ui.directory.models.PickedUi
import at.stefanirndorfer.mobtt.domain.club.DataUnavailable
import at.stefanirndorfer.mobtt.domain.club.ListAssociationClubsUseCase
import at.stefanirndorfer.mobtt.domain.club.ListAssociationsUseCase
import at.stefanirndorfer.mobtt.domain.club.ListClubPlayersUseCase
import at.stefanirndorfer.mobtt.domain.player.Player
import at.stefanirndorfer.mobtt.domain.user.RememberPlayerUseCase
import at.stefanirndorfer.mobtt.domain.user.SetMyPlayerUseCase
import at.stefanirndorfer.mobtt.feature.players.impl.presentation.players.models.PlayerEntryUi
import at.stefanirndorfer.mobtt.feature.players.impl.presentation.players.models.PlayersAction
import at.stefanirndorfer.mobtt.feature.players.impl.presentation.players.models.PlayersEvent
import at.stefanirndorfer.mobtt.feature.players.impl.presentation.players.models.PlayersListUi
import at.stefanirndorfer.mobtt.feature.players.impl.presentation.players.models.PlayersMode
import at.stefanirndorfer.mobtt.feature.players.impl.presentation.players.models.PlayersUiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** The Club's Players and the name typed to narrow them, below the [AssociationClubPicker]'s two rows. */
private data class PlayersListState(val query: String = "", val list: PlayersListUi = PlayersListUi.PickClub)

/**
 * Association, then Club, then a Player of that Club. XTTV cannot search Players by name, so the Club's lineup Players
 * are loaded once when the Club is chosen and the typed name filters them locally. In [PlayersMode.ChooseMine] a
 * tapped Player becomes My Player instead of opening their screen.
 */
class PlayersViewModel(
    private val mode: PlayersMode,
    listAssociations: ListAssociationsUseCase,
    listAssociationClubs: ListAssociationClubsUseCase,
    private val listClubPlayers: ListClubPlayersUseCase,
    private val rememberPlayer: RememberPlayerUseCase,
    private val setMyPlayer: SetMyPlayerUseCase,
    private val analytics: AnalyticsTracker = NoOpAnalyticsTracker,
) : ViewModel() {
    private val screen = when (mode) {
        PlayersMode.Browse -> AnalyticsScreen.PLAYERS
        PlayersMode.ChooseMine -> AnalyticsScreen.CHOOSE_PLAYER
    }
    private val searchEvents = SearchEvents(viewModelScope, analytics, screen)

    init {
        analytics.screenView(screen)
    }

    private val picker = AssociationClubPicker(
        scope = viewModelScope,
        listAssociations = listAssociations,
        listAssociationClubs = listAssociationClubs,
        onAssociationChanged = ::clearPlayers,
        onClubChosen = ::chooseClub,
    )
    private val listState = MutableStateFlow(PlayersListState())

    val uiState: StateFlow<PlayersUiState> = combine(picker.state, listState) { chosen, list ->
        PlayersUiState(
            association = chosen.association,
            club = chosen.club,
            query = list.query,
            list = list.list,
            picker = chosen.sheet,
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, PlayersUiState())

    private val eventChannel = Channel<PlayersEvent>()
    val events: Flow<PlayersEvent> = eventChannel.receiveAsFlow()

    private var players: List<PlayerEntryUi> = emptyList()
    private var playersJob: Job? = null

    fun onAction(action: PlayersAction) {
        when (action) {
            PlayersAction.AssociationRowClicked -> picker.onAssociationRowClicked()
            PlayersAction.ClubRowClicked -> picker.onClubRowClicked()
            is PlayersAction.PickerQueryChanged -> {
                picker.onQueryChanged(action.query)
                searchEvents.onQueryChanged(action.query)
            }
            is PlayersAction.PickerEntryClicked -> picker.onEntryClicked(action.id)
            PlayersAction.PickerDismissed -> picker.onDismissed()
            is PlayersAction.QueryChanged -> {
                changeQuery(action.query)
                searchEvents.onQueryChanged(action.query)
            }
            is PlayersAction.PlayerClicked -> onPlayerClicked(action.playerId)
            PlayersAction.RetryClicked -> picker.state.value.club?.let(::loadPlayers)
        }
    }

    private fun clearPlayers() {
        playersJob?.cancel()
        players = emptyList()
        listState.value = PlayersListState()
    }

    private fun chooseClub(club: PickedUi) {
        listState.value = PlayersListState(list = PlayersListUi.Loading)
        loadPlayers(club)
    }

    private fun loadPlayers(club: PickedUi) {
        listState.update { it.copy(list = PlayersListUi.Loading) }
        playersJob?.cancel()
        playersJob = viewModelScope.launch {
            try {
                players = listClubPlayers(club.id).map { PlayerEntryUi(it.playerId, it.name, it.rcPoints) }
                listState.update { it.copy(list = PlayersListUi.Loaded(players.matching(it.query))) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: DataUnavailable) {
                listState.update { it.copy(list = PlayersListUi.Unavailable) }
            }
        }
    }

    private fun changeQuery(query: String) {
        listState.update { s ->
            val loaded = s.list as? PlayersListUi.Loaded ?: return@update s.copy(query = query)
            s.copy(query = query, list = loaded.copy(entries = players.matching(query)))
        }
    }

    private fun onPlayerClicked(playerId: String) {
        val club = picker.state.value.club ?: return
        val entry = players.firstOrNull { it.id == playerId } ?: return
        viewModelScope.launch {
            // XTTV cannot look a Player up by id, so they are remembered before My Player or their screen needs them.
            rememberPlayer(Player(entry.id, entry.name, club.id))
            when (mode) {
                PlayersMode.Browse -> {
                    analytics.detailOpened(AnalyticsKind.PLAYER, playerId, from = screen)
                    eventChannel.send(PlayersEvent.OpenPlayer(playerId))
                }
                PlayersMode.ChooseMine -> {
                    setMyPlayer(playerId)
                    analytics.myPlayerSelected(AnalyticsSource.HOME)
                    eventChannel.send(PlayersEvent.Chosen)
                }
            }
        }
    }
}

private fun List<PlayerEntryUi>.matching(query: String): List<PlayerEntryUi> {
    val trimmed = query.trim()
    return filter { it.name.contains(trimmed, ignoreCase = true) }
}
