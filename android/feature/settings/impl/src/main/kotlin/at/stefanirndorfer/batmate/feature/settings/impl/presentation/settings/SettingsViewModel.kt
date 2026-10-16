package at.stefanirndorfer.batmate.feature.settings.impl.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.stefanirndorfer.batmate.core.ui.directory.DirectoryEntryUi
import at.stefanirndorfer.batmate.domain.club.SearchClubsUseCase
import at.stefanirndorfer.batmate.domain.user.ObserveUserContextUseCase
import at.stefanirndorfer.batmate.domain.user.SearchPlayersUseCase
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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private data class PickerInput(val kind: PickerKind, val query: String)

private data class PickerResult(val input: PickerInput, val entries: List<DirectoryEntryUi>)

class SettingsViewModel(
    private val appearanceRepository: AppearanceRepository,
    observeUserContext: ObserveUserContextUseCase,
    private val setMyPlayer: SetMyPlayerUseCase,
    private val setMyClub: SetMyClubUseCase,
    private val searchPlayers: SearchPlayersUseCase,
    private val searchClubs: SearchClubsUseCase,
) : ViewModel() {
    private val pickerInput = MutableStateFlow<PickerInput?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val pickerResult: Flow<PickerResult?> = pickerInput.mapLatest { input ->
        input?.let { PickerResult(it, search(it)) }
    }

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
            SettingsAction.ChoosePlayerClicked -> pickerInput.value = PickerInput(PickerKind.PLAYER, "")
            SettingsAction.ChooseClubClicked -> pickerInput.value = PickerInput(PickerKind.CLUB, "")
            is SettingsAction.PickerQueryChanged -> pickerInput.update { it?.copy(query = action.query) }
            is SettingsAction.PickerEntryClicked -> choose(action.id)
            SettingsAction.PickerBackClicked -> pickerInput.value = null
            SettingsAction.DoneClicked -> viewModelScope.launch {
                eventChannel.send(SettingsEvent.Dismiss)
            }
        }
    }

    private fun choose(id: String) {
        val kind = pickerInput.value?.kind ?: return
        viewModelScope.launch {
            when (kind) {
                PickerKind.PLAYER -> setMyPlayer(id)
                PickerKind.CLUB -> setMyClub(id)
            }
            pickerInput.value = null
        }
    }

    private suspend fun search(input: PickerInput): List<DirectoryEntryUi> = when (input.kind) {
        PickerKind.PLAYER -> searchPlayers(input.query).map { result ->
            DirectoryEntryUi(result.player.id, result.player.name, result.clubName, selected = false)
        }
        PickerKind.CLUB -> searchClubs(input.query).map { club ->
            DirectoryEntryUi(club.id, club.name, subtitle = null, selected = false)
        }
    }

    private fun PickerInput.toPicker(result: PickerResult?, userContext: UserContext): SettingsPickerUi {
        val currentId = when (kind) {
            PickerKind.PLAYER -> (userContext as? UserContext.MyPlayer)?.player?.id
            PickerKind.CLUB -> (userContext as? UserContext.MyClub)?.club?.id
        }
        // A result computed for the other kind must not be shown, or its ids would be saved as the wrong choice.
        val entries = result?.takeIf { it.input.kind == kind }?.entries.orEmpty()
        return SettingsPickerUi(kind, query, entries.map { it.copy(selected = it.id == currentId) })
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
