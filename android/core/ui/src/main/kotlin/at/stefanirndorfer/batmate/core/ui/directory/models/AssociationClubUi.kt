package at.stefanirndorfer.batmate.core.ui.directory.models

/** An Association or Club the user chose. */
data class PickedUi(val id: String, val name: String)

enum class AssociationClubSheetKind { ASSOCIATION, CLUB }

/** The open bottom sheet. [entries] are already filtered by [query]. */
data class AssociationClubSheetUi(
    val kind: AssociationClubSheetKind,
    val query: String,
    val entries: List<DirectoryEntryUi>,
    val isLoading: Boolean,
)

data class AssociationClubUi(
    val association: PickedUi? = null,
    val club: PickedUi? = null,
    val sheet: AssociationClubSheetUi? = null,
)
