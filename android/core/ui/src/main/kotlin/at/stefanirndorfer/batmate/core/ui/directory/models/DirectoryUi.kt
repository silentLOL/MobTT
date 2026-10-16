package at.stefanirndorfer.batmate.core.ui.directory.models

data class DirectoryEntryUi(
    val id: String,
    val title: String,
    val subtitle: String?,
    val selected: Boolean,
)

/** A null [title] shows the entries without a section header. */
data class DirectorySectionUi(
    val title: String?,
    val entries: List<DirectoryEntryUi>,
)
