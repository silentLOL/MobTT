package at.stefanirndorfer.batmate.feature.players.impl.presentation.players

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.stefanirndorfer.batmate.core.ui.directory.filteredBy
import at.stefanirndorfer.batmate.core.ui.directory.models.DirectoryEntryUi
import at.stefanirndorfer.batmate.domain.club.DataUnavailable
import at.stefanirndorfer.batmate.domain.club.ListAssociationClubsUseCase
import at.stefanirndorfer.batmate.domain.club.ListAssociationsUseCase
import at.stefanirndorfer.batmate.domain.club.ListClubPlayersUseCase
import at.stefanirndorfer.batmate.domain.player.Player
import at.stefanirndorfer.batmate.domain.user.RememberPlayerUseCase
import at.stefanirndorfer.batmate.feature.players.impl.presentation.players.models.PickedUi
import at.stefanirndorfer.batmate.feature.players.impl.presentation.players.models.PlayerEntryUi
import at.stefanirndorfer.batmate.feature.players.impl.presentation.players.models.PlayersAction
import at.stefanirndorfer.batmate.feature.players.impl.presentation.players.models.PlayersEvent
import at.stefanirndorfer.batmate.feature.players.impl.presentation.players.models.PlayersListUi
import at.stefanirndorfer.batmate.feature.players.impl.presentation.players.models.PlayersPickerKind
import at.stefanirndorfer.batmate.feature.players.impl.presentation.players.models.PlayersPickerUi
import at.stefanirndorfer.batmate.feature.players.impl.presentation.players.models.PlayersUiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Association, then Club, then a Player of that Club. XTTV cannot search Players by name, so the Club's lineup Players
 * are loaded once when the Club is chosen and the typed name filters them locally. A sheet's entries are loaded
 * when it opens and filtered locally the same way.
 */
class PlayersViewModel(
    private val listAssociations: ListAssociationsUseCase,
    private val listAssociationClubs: ListAssociationClubsUseCase,
    private val listClubPlayers: ListClubPlayersUseCase,
    private val rememberPlayer: RememberPlayerUseCase,
) : ViewModel() {
    private val state = MutableStateFlow(PlayersUiState())
    val uiState: StateFlow<PlayersUiState> = state.asStateFlow()

    private val eventChannel = Channel<PlayersEvent>()
    val events: Flow<PlayersEvent> = eventChannel.receiveAsFlow()

    private var pickerEntries: List<DirectoryEntryUi> = emptyList()
    private var players: List<PlayerEntryUi> = emptyList()
    private var pickerJob: Job? = null
    private var playersJob: Job? = null

    fun onAction(action: PlayersAction) {
        when (action) {
            PlayersAction.AssociationRowClicked -> openPicker(PlayersPickerKind.ASSOCIATION)
            PlayersAction.ClubRowClicked -> openPicker(PlayersPickerKind.CLUB)
            is PlayersAction.PickerQueryChanged -> changePickerQuery(action.query)
            is PlayersAction.PickerEntryClicked -> choose(action.id)
            PlayersAction.PickerDismissed -> closePicker()
            is PlayersAction.QueryChanged -> changeQuery(action.query)
            is PlayersAction.PlayerClicked -> openPlayer(action.playerId)
            PlayersAction.RetryClicked -> loadPlayers()
        }
    }

    private fun openPicker(kind: PlayersPickerKind) {
        val current = state.value
        if (kind == PlayersPickerKind.CLUB && current.association == null) {
            return
        }
        pickerEntries = emptyList()
        state.update { it.copy(picker = PlayersPickerUi(kind, query = "", entries = emptyList(), isLoading = true)) }
        pickerJob?.cancel()
        pickerJob = viewModelScope.launch {
            val entries = loadPickerEntries(kind, current)
            pickerEntries = entries
            state.update { s -> s.picker?.let { s.copy(picker = it.copy(entries = entries.markSelected(s, kind), isLoading = false)) } ?: s }
        }
    }

    /** An unreachable XTTV reads as an empty sheet rather than an error. */
    private suspend fun loadPickerEntries(kind: PlayersPickerKind, current: PlayersUiState): List<DirectoryEntryUi> = try {
        when (kind) {
            PlayersPickerKind.ASSOCIATION -> listAssociations().map { DirectoryEntryUi(it.id, it.name, subtitle = null, selected = false) }
            PlayersPickerKind.CLUB -> listAssociationClubs(current.association!!.id).map {
                DirectoryEntryUi(it.id, it.name, subtitle = null, selected = false)
            }
        }
    } catch (e: DataUnavailable) {
        emptyList()
    }

    private fun changePickerQuery(query: String) {
        state.update { s ->
            val picker = s.picker ?: return@update s
            s.copy(picker = picker.copy(query = query, entries = pickerEntries.markSelected(s, picker.kind).filteredBy(query)))
        }
    }

    private fun List<DirectoryEntryUi>.markSelected(s: PlayersUiState, kind: PlayersPickerKind): List<DirectoryEntryUi> {
        val chosenId = when (kind) {
            PlayersPickerKind.ASSOCIATION -> s.association?.id
            PlayersPickerKind.CLUB -> s.club?.id
        }
        return map { it.copy(selected = it.id == chosenId) }
    }

    private fun choose(id: String) {
        val current = state.value
        val picker = current.picker ?: return
        val entry = pickerEntries.firstOrNull { it.id == id } ?: return
        val picked = PickedUi(entry.id, entry.title)
        closePicker()
        when (picker.kind) {
            PlayersPickerKind.ASSOCIATION -> if (picked != current.association) {
                playersJob?.cancel()
                players = emptyList()
                state.update { PlayersUiState(association = picked) }
            }
            PlayersPickerKind.CLUB -> if (picked != current.club) {
                state.update { it.copy(club = picked, query = "", list = PlayersListUi.Loading) }
                loadPlayers()
            }
        }
    }

    private fun closePicker() {
        pickerJob?.cancel()
        state.update { it.copy(picker = null) }
    }

    private fun loadPlayers() {
        val club = state.value.club ?: return
        state.update { it.copy(list = PlayersListUi.Loading) }
        playersJob?.cancel()
        playersJob = viewModelScope.launch {
            try {
                players = listClubPlayers(club.id).map { PlayerEntryUi(it.playerId, it.name, it.rcPoints) }
                state.update { it.copy(list = PlayersListUi.Loaded(players.matching(it.query))) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: DataUnavailable) {
                state.update { it.copy(list = PlayersListUi.Unavailable) }
            }
        }
    }

    private fun changeQuery(query: String) {
        state.update { s ->
            val loaded = s.list as? PlayersListUi.Loaded ?: return@update s.copy(query = query)
            s.copy(query = query, list = loaded.copy(entries = players.matching(query)))
        }
    }

    private fun openPlayer(playerId: String) {
        val club = state.value.club ?: return
        val entry = players.firstOrNull { it.id == playerId } ?: return
        viewModelScope.launch {
            rememberPlayer(Player(entry.id, entry.name, club.id))
            eventChannel.send(PlayersEvent.OpenPlayer(playerId))
        }
    }
}

private fun List<PlayerEntryUi>.matching(query: String): List<PlayerEntryUi> {
    val trimmed = query.trim()
    return filter { it.name.contains(trimmed, ignoreCase = true) }
}
