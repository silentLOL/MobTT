package at.stefanirndorfer.batmate.feature.settings.impl.presentation.settings.models

sealed interface SettingsEvent {
    data object Dismiss : SettingsEvent
}
