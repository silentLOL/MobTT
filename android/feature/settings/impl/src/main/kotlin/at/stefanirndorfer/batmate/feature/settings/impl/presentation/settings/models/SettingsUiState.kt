package at.stefanirndorfer.batmate.feature.settings.impl.presentation.settings.models

import at.stefanirndorfer.batmate.feature.settings.api.Appearance

data class SettingsUiState(
    val appearance: Appearance = Appearance.SYSTEM,
)
