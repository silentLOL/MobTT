package at.stefanirndorfer.mobtt.feature.club.impl.presentation

import at.stefanirndorfer.mobtt.core.ui.fixture.formatKickoff
import at.stefanirndorfer.mobtt.domain.club.Fixture
import at.stefanirndorfer.mobtt.domain.club.FixtureDetail
import at.stefanirndorfer.mobtt.domain.club.FixtureScore
import at.stefanirndorfer.mobtt.domain.club.FixtureStatus
import at.stefanirndorfer.mobtt.domain.club.Match
import at.stefanirndorfer.mobtt.domain.club.MatchKind
import at.stefanirndorfer.mobtt.domain.club.MatchScore
import at.stefanirndorfer.mobtt.domain.club.VenueAddress
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.fixturedetail.formatting.fixtureShare
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.fixturedetail.formatting.fixtureShareCaption
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.fixturedetail.models.FixtureShare
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.util.Locale
import java.util.TimeZone
import kotlin.test.assertEquals
import kotlin.time.Instant

// SDK 33 like the generated preview tests: Robolectric cannot start the compile SDK on this JDK.
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class FixtureShareTest {
    private val defaultTimeZone = TimeZone.getDefault()
    private val link = "https://links.mobtt.example/f/464821"

    // 2027-01-15 08:00 UTC, 09:00 in Vienna.
    private val startsAt = Instant.fromEpochSeconds(1_800_000_000)

    private val scheduled = FixtureDetail(
        fixture = Fixture(
            id = "464821",
            homeTeamId = "t1",
            homeTeamName = "Hagenberg 3",
            awayTeamId = "t2",
            awayTeamName = "Freistadt 6",
            startsAt = startsAt,
            venue = "Sporthalle Hagenberg",
            status = FixtureStatus.Scheduled,
        ),
        address = VenueAddress(street = "Hauptstraße 1", postalCode = "4232", city = "Hagenberg"),
        matches = emptyList(),
    )

    private val finished = scheduled.copy(
        fixture = scheduled.fixture.copy(status = FixtureStatus.Finished(FixtureScore(homeMatchesWon = 6, awayMatchesWon = 4))),
        matches = listOf(
            Match(1, MatchKind.Doubles, listOf("Huber", "Bauer"), listOf("Gruber", "Wolf"), MatchScore(3, 1)),
            Match(2, MatchKind.Singles, listOf("Huber"), listOf("Gruber"), MatchScore(2, 3)),
            Match(3, MatchKind.Singles, listOf("Bauer"), listOf("Wolf"), score = null),
        ),
    )

    @Before
    fun setUp() = TimeZone.setDefault(TimeZone.getTimeZone("Europe/Vienna"))

    @After
    fun tearDown() = TimeZone.setDefault(defaultTimeZone)

    private fun share(detail: FixtureDetail): FixtureShare = RuntimeEnvironment.getApplication().fixtureShare(detail, link)

    @Test
    @Config(qualifiers = "de")
    fun aFinishedFixtureSharesEveryMatchInGerman() {
        assertEquals(
            FixtureShare(
                subject = "Hagenberg 3 – Freistadt 6",
                text = """
                    Hagenberg 3 – Freistadt 6  6 : 4
                    15.01.2027, 09:00
                    Sporthalle Hagenberg
                    Hauptstraße 1, 4232 Hagenberg

                    Spiele
                    1 Doppel: Huber / Bauer – Gruber / Wolf  3 : 1
                    2 Einzel: Huber – Gruber  2 : 3
                    3 Einzel: Bauer – Wolf

                    https://links.mobtt.example/f/464821
                """.trimIndent(),
            ),
            share(finished),
        )
    }

    @Test
    @Config(qualifiers = "en")
    fun aScheduledFixtureSaysItIsNotPlayedYetAndHasNoMatches() {
        assertEquals(
            """
                Hagenberg 3 – Freistadt 6  Not played yet
                ${formatKickoff(startsAt, Locale.ENGLISH)}
                Sporthalle Hagenberg
                Hauptstraße 1, 4232 Hagenberg

                https://links.mobtt.example/f/464821
            """.trimIndent(),
            share(scheduled).text,
        )
    }

    @Test
    @Config(qualifiers = "en")
    fun aFinishedFixtureWithoutMatchesSaysSoAndLeavesOutAMissingPlace() {
        val noPlace = finished.copy(fixture = finished.fixture.copy(venue = null), address = null, matches = emptyList())

        assertEquals(
            """
                Hagenberg 3 – Freistadt 6  6 : 4
                ${formatKickoff(startsAt, Locale.ENGLISH)}

                Matches
                No Match results available yet.

                https://links.mobtt.example/f/464821
            """.trimIndent(),
            share(noPlace).text,
        )
    }

    @Test
    @Config(qualifiers = "de")
    fun theCaptionNextToTheImageIsThePairingTheResultTheKickoffAndTheLink() {
        assertEquals(
            FixtureShare(
                subject = "Hagenberg 3 – Freistadt 6",
                text = """
                    Hagenberg 3 – Freistadt 6  6 : 4
                    15.01.2027, 09:00

                    https://links.mobtt.example/f/464821
                """.trimIndent(),
            ),
            RuntimeEnvironment.getApplication().fixtureShareCaption(finished, link),
        )
    }
}
