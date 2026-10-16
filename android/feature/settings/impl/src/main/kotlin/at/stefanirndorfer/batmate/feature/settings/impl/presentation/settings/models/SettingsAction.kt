package at.stefanirndorfer.batmate.feature.settings.impl.presentation.settings.models

import at.stefanirndorfer.batmate.feature.settings.api.Appearance

sealed interface SettingsAction {
    data class AppearanceSelected(val appearance: Appearance) : SettingsAction

    data object ChoosePlayerClicked : SettingsAction

    data object ChooseClubClicked : SettingsAction

    data class PickerQueryChanged(val query: String) : SettingsAction

    data class PickerEntryClicked(val id: String) : SettingsAction

    data object PickerBackClicked : SettingsAction

    data object DoneClicked : SettingsAction
}
