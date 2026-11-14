package at.stefanirndorfer.mobtt.feature.settings.impl.presentation.licenses.formatting

import at.stefanirndorfer.mobtt.feature.settings.impl.presentation.licenses.models.LicenseEntry
import at.stefanirndorfer.mobtt.feature.settings.impl.presentation.licenses.models.LicenseKind
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.entity.Library

private const val FONT_PREFIX = "font:"
private const val ASSET_PREFIX = "asset:"
private val LINE_BREAK = Regex("""\s*<br\s*/?>\s*""")

/** Libraries first, then fonts, then assets; each group sorted by name. */
internal fun parseLicenseEntries(json: String): List<LicenseEntry> = Libs.Builder().withJson(json).build().libraries
    .map { it.toEntry() }
    .sortedWith(compareBy({ it.kind }, { it.name.lowercase() }))

private fun Library.toEntry(): LicenseEntry {
    val withText = licenses.firstOrNull { !it.licenseContent.isNullOrBlank() }
    val first = withText ?: licenses.firstOrNull()
    return LicenseEntry(
        id = uniqueId,
        name = name,
        version = artifactVersion?.takeIf { it.isNotBlank() },
        authors = developers.mapNotNull { it.name }.joinToString(", ").ifBlank { organization?.name },
        licenseName = licenses.joinToString(", ") { it.name },
        licenseText = withText?.licenseContent?.replace(LINE_BREAK, "\n")?.trim(),
        licenseUrl = first?.url,
        kind = when {
            uniqueId.startsWith(FONT_PREFIX) -> LicenseKind.FONT
            uniqueId.startsWith(ASSET_PREFIX) -> LicenseKind.ASSET
            else -> LicenseKind.LIBRARY
        },
    )
}
