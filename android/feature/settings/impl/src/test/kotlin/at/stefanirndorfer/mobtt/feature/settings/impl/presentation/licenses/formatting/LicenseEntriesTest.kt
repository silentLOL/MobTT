package at.stefanirndorfer.mobtt.feature.settings.impl.presentation.licenses.formatting

import at.stefanirndorfer.mobtt.feature.settings.impl.presentation.licenses.models.LicenseKind
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals
import kotlin.test.assertNull

private val LICENSES = """
    "licenses": {
        "Apache-2.0": {"hash": "Apache-2.0", "internalHash": "Apache-2.0", "name": "Apache License 2.0", "url": "https://www.apache.org/licenses/LICENSE-2.0", "content": "Apache License\n<br />\nVersion 2.0"},
        "ASDKL": {"hash": "ASDKL", "internalHash": "ASDKL", "name": "Android Software Development Kit License", "url": "https://developer.android.com/studio/terms.html"},
        "OFL-1.1": {"hash": "OFL-1.1", "internalHash": "OFL-1.1", "name": "SIL Open Font License 1.1", "url": "https://openfontlicense.org", "content": "Copyright (c) 2016 The Inter Project Authors"}
    }
""".trimIndent()

private fun library(id: String, name: String, license: String, version: String? = "1.0", author: String? = null): String {
    val developers = author?.let { """[{"name": "$it"}]""" } ?: "[]"
    val versionField = version?.let { """"$it"""" } ?: "null"
    return """{"uniqueId": "$id", "artifactVersion": $versionField, "name": "$name", "description": null, "website": null, """ +
        """"developers": $developers, "organization": null, "scm": null, "licenses": ["$license"]}"""
}

private fun json(vararg libraries: String) = """{"libraries": [${libraries.joinToString(",")}], $LICENSES}"""

/** Runs under Robolectric because AboutLibraries parses the list with `org.json`, which a plain unit test only stubs. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class LicenseEntriesTest {
    @Test
    fun librariesFontsAndAssetsAreKeptApartAndEachSortedByName() {
        val entries = parseLicenseEntries(
            json(
                library("org.zeta:zeta", "Zeta", "Apache-2.0"),
                library("font:inter", "Inter", "OFL-1.1"),
                library("asset:icons", "Icons", "Apache-2.0"),
                library("org.alpha:alpha", "alpha", "Apache-2.0"),
            ),
        )

        assertEquals(
            listOf("alpha" to LicenseKind.LIBRARY, "Zeta" to LicenseKind.LIBRARY, "Inter" to LicenseKind.FONT, "Icons" to LicenseKind.ASSET),
            entries.map { it.name to it.kind },
        )
    }

    @Test
    fun anEntryCarriesItsVersionAuthorAndLicense() {
        val entry = parseLicenseEntries(json(library("org.a:a", "A", "Apache-2.0", version = "2.3", author = "Jane Doe"))).single()

        assertEquals("2.3", entry.version)
        assertEquals("Jane Doe", entry.authors)
        assertEquals("Apache License 2.0", entry.licenseName)
        assertEquals("https://www.apache.org/licenses/LICENSE-2.0", entry.licenseUrl)
    }

    @Test
    fun lineBreaksInTheLicenseTextBecomeNewlines() {
        val entry = parseLicenseEntries(json(library("org.a:a", "A", "Apache-2.0"))).single()

        assertEquals("Apache License\nVersion 2.0", entry.licenseText)
    }

    @Test
    fun aLicenseWithoutTextKeepsItsNameAndAddress() {
        val entry = parseLicenseEntries(json(library("com.google:play", "Play Services", "ASDKL"))).single()

        assertNull(entry.licenseText)
        assertEquals("Android Software Development Kit License", entry.licenseName)
        assertEquals("https://developer.android.com/studio/terms.html", entry.licenseUrl)
    }

    @Test
    fun aMissingVersionAndAuthorStayEmpty() {
        val entry = parseLicenseEntries(json(library("font:inter", "Inter", "OFL-1.1", version = null))).single()

        assertNull(entry.version)
        assertNull(entry.authors)
    }

    @Test
    fun noLibrariesGiveNoEntries() {
        assertEquals(emptyList(), parseLicenseEntries(json()))
    }
}
