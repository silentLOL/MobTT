package at.stefanirndorfer.mobtt.feature.settings.impl.presentation.settings.models

sealed interface SettingsEvent {
    data class OpenSupportLink(val url: String) : SettingsEvent
}
