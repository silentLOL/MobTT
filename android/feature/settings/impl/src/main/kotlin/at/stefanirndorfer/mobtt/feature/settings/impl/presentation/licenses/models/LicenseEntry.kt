package at.stefanirndorfer.mobtt.feature.settings.impl.presentation.licenses.models

enum class LicenseKind {
    LIBRARY,
    FONT,
    ASSET,
}

/** [licenseText] is null when the license ships no text; [licenseName] and [licenseUrl] still say which license it is. */
data class LicenseEntry(
    val id: String,
    val name: String,
    val version: String?,
    val authors: String?,
    val licenseName: String,
    val licenseText: String?,
    val licenseUrl: String?,
    val kind: LicenseKind,
)
