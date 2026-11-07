package at.stefanirndorfer.mobtt.domain.club

/**
 * Builds and reads Fixture links, `https://<host>/f/<Fixture id>` (ADR-0011). [host] is the links domain without a
 * scheme. [sharingEnabled] says whether Fixtures may be shared yet; reading a link works either way.
 */
class FixtureLinks(private val host: String, val sharingEnabled: Boolean) {
    private val fixtureLink = Regex(
        """^https://${Regex.escape(host)}/f/([A-Za-z0-9_-]+)/?(?:[?#].*)?$""",
        RegexOption.IGNORE_CASE,
    )

    fun linkTo(fixtureId: String): String = "https://$host/f/$fixtureId"

    /** The Fixture id in [url], or null when it is not a Fixture link. A trailing slash, query or fragment is ignored. */
    fun fixtureIdOf(url: String): String? = fixtureLink.matchEntire(url)?.groupValues?.get(1)
}
