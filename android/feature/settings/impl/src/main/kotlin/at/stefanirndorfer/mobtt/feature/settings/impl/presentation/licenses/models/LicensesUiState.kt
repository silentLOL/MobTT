package at.stefanirndorfer.mobtt.feature.settings.impl.presentation.licenses.models

data class LicenseGroupUi(val kind: LicenseKind, val entries: List<LicenseEntry>)

/** [groups] is empty once loaded when the list is empty or could not be read. [expandedId] is the entry showing its text. */
data class LicensesUiState(
    val isLoading: Boolean = true,
    val groups: List<LicenseGroupUi> = emptyList(),
    val expandedId: String? = null,
)
