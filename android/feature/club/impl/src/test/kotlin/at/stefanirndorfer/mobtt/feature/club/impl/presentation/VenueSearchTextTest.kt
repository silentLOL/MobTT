package at.stefanirndorfer.mobtt.feature.club.impl.presentation

import at.stefanirndorfer.mobtt.domain.club.Fixture
import at.stefanirndorfer.mobtt.domain.club.FixtureDetail
import at.stefanirndorfer.mobtt.domain.club.FixtureStatus
import at.stefanirndorfer.mobtt.domain.club.VenueAddress
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.fixturedetail.formatting.venueSearchText
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Instant

class VenueSearchTextTest {
    private fun detail(venue: String?, address: VenueAddress?) = FixtureDetail(
        fixture = Fixture(
            id = "f1",
            homeTeamId = "t1",
            homeTeamName = "Hagenberg 1",
            awayTeamId = "t2",
            awayTeamName = "Olympic 1",
            startsAt = Instant.fromEpochSeconds(1_800_000_000),
            venue = venue,
            status = FixtureStatus.Scheduled,
        ),
        address = address,
        matches = emptyList(),
    )

    @Test
    fun namesTheVenueThenEveryAddressPart() {
        val address = VenueAddress(street = "Softwarepark 101", postalCode = "4232", city = "Hagenberg")

        assertEquals("Sporthalle Hagenberg, Softwarepark 101, 4232 Hagenberg", detail("Sporthalle Hagenberg", address).venueSearchText())
    }

    @Test
    fun aNameAloneIsSearchedForOnItsOwn() {
        assertEquals("Sporthalle Olympic", detail("Sporthalle Olympic", address = null).venueSearchText())
    }

    @Test
    fun aCityAloneFollowsTheName() {
        val address = VenueAddress(street = null, postalCode = null, city = "Wien")

        assertEquals("Sporthalle Olympic, Wien", detail("Sporthalle Olympic", address).venueSearchText())
    }

    @Test
    fun anAddressWithoutANameIsSearchedForOnItsOwn() {
        val address = VenueAddress(street = "Softwarepark 101", postalCode = "4232", city = "Hagenberg")

        assertEquals("Softwarepark 101, 4232 Hagenberg", detail(venue = null, address).venueSearchText())
    }

    @Test
    fun withoutANameOrAddressThereIsNothingToSearchFor() {
        assertNull(detail(venue = null, address = null).venueSearchText())
    }
}
