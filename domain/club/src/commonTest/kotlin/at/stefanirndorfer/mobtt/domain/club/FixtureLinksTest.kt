package at.stefanirndorfer.mobtt.domain.club

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class FixtureLinksTest {
    private val links = FixtureLinks(host = "links.mobtt.example", sharingEnabled = true)

    @Test
    fun aFixtureLinkPointsAtTheFixtureOnTheLinksHost() {
        assertEquals("https://links.mobtt.example/f/464821", links.linkTo("464821"))
    }

    @Test
    fun aFixtureLinkReadsBackAsItsFixture() {
        assertEquals("464821", links.fixtureIdOf("https://links.mobtt.example/f/464821"))
    }

    @Test
    fun aMessengerSuffixDoesNotHideTheFixture() {
        assertEquals("464821", links.fixtureIdOf("https://links.mobtt.example/f/464821/"))
        assertEquals("464821", links.fixtureIdOf("https://links.mobtt.example/f/464821?utm_source=whatsapp"))
        assertEquals("464821", links.fixtureIdOf("HTTPS://Links.MobTT.example/f/464821#top"))
    }

    @Test
    fun anythingElseIsNotAFixtureLink() {
        listOf(
            "https://other.example/f/464821",
            "https://links.mobtt.example.evil.example/f/464821",
            "http://links.mobtt.example/f/464821",
            "https://links.mobtt.example/t/8846",
            "https://links.mobtt.example/f/",
            "https://links.mobtt.example/f",
            "https://links.mobtt.example/f/464821/matches",
            "https://links.mobtt.example/F/464821",
            "https://links.mobtt.example/",
            "mobtt://f/464821",
            "",
        ).forEach { assertNull(links.fixtureIdOf(it), it) }
    }
}
