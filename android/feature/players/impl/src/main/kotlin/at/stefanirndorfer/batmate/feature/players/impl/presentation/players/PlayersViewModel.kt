package at.stefanirndorfer.batmate.feature.players.impl.presentation.players

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.stefanirndorfer.batmate.core.ui.directory.AssociationClubPicker
import at.stefanirndorfer.batmate.core.ui.directory.models.PickedUi
import at.stefanirndorfer.batmate.domain.club.DataUnavailable
import at.stefanirndorfer.batmate.domain.club.ListAssociationClubsUseCase
import at.stefanirndorfer.batmate.domain.club.ListAssociationsUseCase
import at.stefanirndorfer.batmate.domain.club.ListClubPlayersUseCase
import at.stefanirndorfer.batmate.domain.player.Player
import at.stefanirndorfer.batmate.domain.user.RememberPlayerUseCase
import at.stefanirndorfer.batmate.domain.user.SetMyPlayerUseCase
import at.stefanirndorfer.batmate.feature.players.impl.presentation.players.models.PlayerEntryUi
import at.stefanirndorfer.batmate.feature.players.impl.presentation.players.models.PlayersAction
import at.stefanirndorfer.batmate.feature.players.impl.presentation.players.models.PlayersEvent
import at.stefanirndorfer.batmate.feature.players.impl.presentation.players.models.PlayersListUi
import at.stefanirndorfer.batmate.feature.players.impl.presentation.players.models.PlayersMode
import at.stefanirndorfer.batmate.feature.players.impl.presentation.players.models.PlayersUiState
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
) : ViewModel() {
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
            is PlayersAction.PickerQueryChanged -> picker.onQueryChanged(action.query)
            is PlayersAction.PickerEntryClicked -> picker.onEntryClicked(action.id)
            PlayersAction.PickerDismissed -> picker.onDismissed()
            is PlayersAction.QueryChanged -> changeQuery(action.query)
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
                PlayersMode.Browse -> eventChannel.send(PlayersEvent.OpenPlayer(playerId))
                PlayersMode.ChooseMine -> {
                    setMyPlayer(playerId)
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
