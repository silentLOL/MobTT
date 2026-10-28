package at.stefanirndorfer.batmate.feature.settings.impl.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.stefanirndorfer.batmate.core.ui.directory.PlayerDrillDown
import at.stefanirndorfer.batmate.core.ui.directory.filteredBy
import at.stefanirndorfer.batmate.core.ui.directory.models.DirectoryEntryUi
import at.stefanirndorfer.batmate.core.ui.directory.models.PlayerPath
import at.stefanirndorfer.batmate.core.ui.directory.models.PlayerStep
import at.stefanirndorfer.batmate.domain.club.DataUnavailable
import at.stefanirndorfer.batmate.domain.club.ListAssociationsUseCase
import at.stefanirndorfer.batmate.domain.club.ListLeaguesUseCase
import at.stefanirndorfer.batmate.domain.club.SearchClubsUseCase
import at.stefanirndorfer.batmate.domain.user.ListLeaguePlayersUseCase
import at.stefanirndorfer.batmate.domain.user.ObserveUserContextUseCase
import at.stefanirndorfer.batmate.domain.user.SetMyClubUseCase
import at.stefanirndorfer.batmate.domain.user.SetMyPlayerUseCase
import at.stefanirndorfer.batmate.domain.user.UserContext
import at.stefanirndorfer.batmate.feature.settings.api.Appearance
import at.stefanirndorfer.batmate.feature.settings.api.AppearanceRepository
import at.stefanirndorfer.batmate.feature.settings.impl.presentation.settings.models.DerivedFrom
import at.stefanirndorfer.batmate.feature.settings.impl.presentation.settings.models.PickerKind
import at.stefanirndorfer.batmate.feature.settings.impl.presentation.settings.models.SettingsAction
import at.stefanirndorfer.batmate.feature.settings.impl.presentation.settings.models.SettingsEvent
import at.stefanirndorfer.batmate.feature.settings.impl.presentation.settings.models.SettingsPickerUi
import at.stefanirndorfer.batmate.feature.settings.impl.presentation.settings.models.SettingsUiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private data class PickerInput(val kind: PickerKind, val path: PlayerPath = PlayerPath(), val query: String = "")

/** What is fetched: only a change of Picker kind or drill-down step fetches again, never a keystroke. */
private data class PickerSource(val kind: PickerKind, val path: PlayerPath)

private class PickerResult(val source: PickerSource, val entries: List<DirectoryEntryUi>)

private fun PickerInput.source() = PickerSource(kind, path)

class SettingsViewModel(
    private val appearanceRepository: AppearanceRepository,
    observeUserContext: ObserveUserContextUseCase,
    private val setMyPlayer: SetMyPlayerUseCase,
    private val setMyClub: SetMyClubUseCase,
    private val searchClubs: SearchClubsUseCase,
    listAssociations: ListAssociationsUseCase,
    listLeagues: ListLeaguesUseCase,
    listLeaguePlayers: ListLeaguePlayersUseCase,
) : ViewModel() {
    private val drillDown = PlayerDrillDown(listAssociations, listLeagues, listLeaguePlayers)
    private val pickerInput = MutableStateFlow<PickerInput?>(null)
    private var loaded: PickerResult? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    private val pickerResult: Flow<PickerResult?> = pickerInput
        .map { it?.source() }
        .distinctUntilChanged()
        .mapLatest { source -> source?.let { PickerResult(it, load(it)).also { result -> loaded = result } } }

    val uiState: StateFlow<SettingsUiState> = combine(
        appearanceRepository.observeAppearance(),
        observeUserContext(),
        pickerInput,
        pickerResult,
    ) { appearance, userContext, input, result ->
        userContext.toUiState(appearance).copy(picker = input?.toPicker(result, userContext))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), SettingsUiState())

    private val eventChannel = Channel<SettingsEvent>()
    val events: Flow<SettingsEvent> = eventChannel.receiveAsFlow()

    fun onAction(action: SettingsAction) {
        when (action) {
            is SettingsAction.AppearanceSelected -> viewModelScope.launch {
                appearanceRepository.saveAppearance(action.appearance)
            }
            SettingsAction.ChoosePlayerClicked -> pickerInput.value = PickerInput(PickerKind.PLAYER)
            SettingsAction.ChooseClubClicked -> pickerInput.value = PickerInput(PickerKind.CLUB)
            is SettingsAction.PickerQueryChanged -> pickerInput.update { it?.copy(query = action.query) }
            is SettingsAction.PickerEntryClicked -> choose(action.id)
            SettingsAction.PickerBackClicked -> pickerInput.update { goBack(it) }
            SettingsAction.DoneClicked -> viewModelScope.launch {
                eventChannel.send(SettingsEvent.Dismiss)
            }
        }
    }

    /** One step back through the Player drill-down; from its first step, or from the Club picker, the picker closes. */
    private fun goBack(input: PickerInput?): PickerInput? {
        val previous = input?.path?.back() ?: return null
        return input.copy(path = previous, query = "")
    }

    private fun choose(id: String) {
        val input = pickerInput.value ?: return
        when {
            input.kind == PickerKind.CLUB -> save { setMyClub(id) }
            input.path.step == PlayerStep.PLAYER -> save { setMyPlayer(id) }
            else -> enter(input, id)
        }
    }

    /** An Association or League was chosen: the next step lists what is inside it. */
    private fun enter(input: PickerInput, id: String) {
        val entry = loaded?.takeIf { it.source == input.source() }?.entries?.firstOrNull { it.id == id } ?: return
        val next = input.path.enter(entry) ?: return
        pickerInput.update { current -> current?.takeIf { it.source() == input.source() }?.copy(path = next, query = "") ?: current }
    }

    private fun save(choice: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                choice()
                pickerInput.value = null
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                // The picker stays open so the choice can be retried.
            }
        }
    }

    /** An unreachable XTTV reads as no results rather than ending the picker's stream. */
    private suspend fun load(source: PickerSource): List<DirectoryEntryUi> = when (source.kind) {
        PickerKind.PLAYER -> drillDown.load(source.path)
        PickerKind.CLUB -> try {
            searchClubs("").map { DirectoryEntryUi(it.id, it.name, subtitle = null, selected = false) }
        } catch (e: DataUnavailable) {
            emptyList()
        }
    }

    private fun PickerInput.toPicker(result: PickerResult?, userContext: UserContext): SettingsPickerUi {
        val currentId = when {
            kind == PickerKind.CLUB -> (userContext as? UserContext.MyClub)?.club?.id
            path.step == PlayerStep.PLAYER -> (userContext as? UserContext.MyPlayer)?.player?.id
            else -> null
        }
        // A result fetched for another kind or step must not be shown, or its ids would be saved as the wrong choice.
        val entries = result?.takeIf { it.source == source() }?.entries.orEmpty().filteredBy(query)
        return SettingsPickerUi(
            kind = kind,
            query = query,
            entries = entries.map { it.copy(selected = it.id == currentId) },
            step = path.step.takeIf { kind == PickerKind.PLAYER },
            breadcrumb = path.breadcrumb.takeIf { kind == PickerKind.PLAYER },
        )
    }

    private fun UserContext.toUiState(appearance: Appearance) = when (this) {
        UserContext.None -> SettingsUiState(appearance = appearance)
        is UserContext.MyPlayer -> SettingsUiState(
            appearance = appearance,
            myPlayerName = player.name,
            derivedFrom = DerivedFrom.PLAYER,
            derivedClubName = club.name,
            derivedAssociationName = association.name,
        )
        is UserContext.MyClub -> SettingsUiState(
            appearance = appearance,
            myClubName = club.name,
            derivedFrom = DerivedFrom.CLUB,
            derivedClubName = club.name,
            derivedAssociationName = association.name,
        )
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
