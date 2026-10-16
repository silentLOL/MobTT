package at.stefanirndorfer.batmate.feature.settings.impl.presentation.settings.models

import at.stefanirndorfer.batmate.feature.settings.api.Appearance

sealed interface SettingsAction {
    data class AppearanceSelected(val appearance: Appearance) : SettingsAction

    data object DoneClicked : SettingsAction
}
