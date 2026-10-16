package at.stefanirndorfer.batmate.feature.settings.impl.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.stefanirndorfer.batmate.feature.settings.api.AppearanceRepository
import at.stefanirndorfer.batmate.feature.settings.impl.presentation.settings.models.SettingsAction
import at.stefanirndorfer.batmate.feature.settings.impl.presentation.settings.models.SettingsEvent
import at.stefanirndorfer.batmate.feature.settings.impl.presentation.settings.models.SettingsUiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val appearanceRepository: AppearanceRepository,
) : ViewModel() {
    val uiState: StateFlow<SettingsUiState> = appearanceRepository.observeAppearance()
        .map { SettingsUiState(appearance = it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), SettingsUiState())

    private val eventChannel = Channel<SettingsEvent>()
    val events: Flow<SettingsEvent> = eventChannel.receiveAsFlow()

    fun onAction(action: SettingsAction) {
        when (action) {
            is SettingsAction.AppearanceSelected -> viewModelScope.launch {
                appearanceRepository.saveAppearance(action.appearance)
            }
            SettingsAction.DoneClicked -> viewModelScope.launch {
                eventChannel.send(SettingsEvent.Dismiss)
            }
        }
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
