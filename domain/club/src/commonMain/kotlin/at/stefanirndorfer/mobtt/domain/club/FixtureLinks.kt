package at.stefanirndorfer.mobtt.domain.club

/**
 * Builds and reads Fixture links, `https://<host>/f/<Fixture id>` (ADR-0011). [host] is the links domain without a
 * scheme, or null until one is registered: then there are no Fixture links, and Fixtures are shared without one.
 * [sharingEnabled] says whether Fixtures may be shared yet; reading a link works either way.
 */
class FixtureLinks(private val host: String?, val sharingEnabled: Boolean) {
    // Scheme and host ignore case, as URLs do; the path is case-sensitive, like the app's link filters.
    private val origin = host?.let { "https://$it/" }
    private val fixturePath = Regex("""^f/([A-Za-z0-9_-]+)/?(?:[?#].*)?$""")

    /** The Fixture link of [fixtureId], or null while there is no links domain. */
    fun linkTo(fixtureId: String): String? = origin?.let { "${it}f/$fixtureId" }

    /** The Fixture id in [url], or null when it is not a Fixture link. A trailing slash, query or fragment is ignored. */
    fun fixtureIdOf(url: String): String? {
        if (origin == null || !url.startsWith(origin, ignoreCase = true)) {
            return null
        }
        return fixturePath.matchEntire(url.substring(origin.length))?.groupValues?.get(1)
    }
}
