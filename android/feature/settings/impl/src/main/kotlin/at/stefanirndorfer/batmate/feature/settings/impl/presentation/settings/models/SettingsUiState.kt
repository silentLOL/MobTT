package at.stefanirndorfer.batmate.feature.settings.impl.presentation.settings.models

import at.stefanirndorfer.batmate.core.ui.directory.models.DirectoryEntryUi
import at.stefanirndorfer.batmate.feature.settings.api.Appearance

enum class PickerKind {
    PLAYER,
    CLUB,
}

enum class DerivedFrom {
    NONE,
    PLAYER,
    CLUB,
}

data class SettingsPickerUi(
    val kind: PickerKind,
    val query: String,
    val entries: List<DirectoryEntryUi>,
)

/** [myPlayerName] and [myClubName] are the user's choice; the Club and Association are derived and never edited. */
data class SettingsUiState(
    val appearance: Appearance = Appearance.SYSTEM,
    val myPlayerName: String? = null,
    val myClubName: String? = null,
    val derivedFrom: DerivedFrom = DerivedFrom.NONE,
    val derivedClubName: String? = null,
    val derivedAssociationName: String? = null,
    val picker: SettingsPickerUi? = null,
)
