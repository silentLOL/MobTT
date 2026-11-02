package at.stefanirndorfer.mobtt.core.xttv

import at.stefanirndorfer.mobtt.domain.club.DataUnavailable
import at.stefanirndorfer.mobtt.domain.club.FixtureScore
import at.stefanirndorfer.mobtt.domain.club.FixtureStatus
import at.stefanirndorfer.mobtt.domain.club.MatchKind
import at.stefanirndorfer.mobtt.domain.club.MatchScore
import at.stefanirndorfer.mobtt.domain.club.VenueAddress
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.time.Instant

class XttvFixtureDetailTest {
    private val xttv = FakeXttv(
        mapOf(
            XttvGameDetailsSamples.PLAYED_464821_URL to XttvGameDetailsSamples.PLAYED_464821,
            XttvGameDetailsSamples.SCHEDULED_9100_URL to XttvGameDetailsSamples.SCHEDULED_9100,
            XttvGameDetailsSamples.UNKNOWN_GAME_URL to XttvGameDetailsSamples.UNKNOWN_GAME,
        ),
    )
    private val api = XttvApi(xttv.client)
    private val repository = XttvFixtureRepository(api, XttvSamples.HOST, XttvTeamRepository(api, XttvSamples.HOST))

    @Test
    fun aPlayedFixtureCarriesItsHeaderScoreAndVenue() = runTest {
        val detail = assertNotNull(repository.getFixtureDetail("464821"))

        assertEquals("464821", detail.fixture.id)
        assertEquals("Hagenberg 3", detail.fixture.homeTeamName)
        assertEquals("Kefermarkt 4", detail.fixture.awayTeamName)
        assertEquals(FixtureStatus.Finished(FixtureScore(10, 0)), detail.fixture.status)
        assertEquals("Sporthalle Hagenberg", detail.fixture.venue)
        assertEquals(Instant.parse("2026-10-02T17:30:00Z"), detail.fixture.startsAt)
        assertEquals(VenueAddress("Softwarepark 101", "4232", "Hagenberg"), detail.address)
    }

    @Test
    fun matchesComeInOrderWithTheirPlayersResolvedFromTheirPositions() = runTest {
        val matches = repository.getFixtureDetail("464821")!!.matches

        assertEquals((1..10).toList(), matches.map { it.number })
        val first = matches.first()
        assertEquals(MatchKind.Singles, first.kind)
        assertEquals(listOf("Markus Luger"), first.homePlayers)
        assertEquals(listOf("Anton Irndorfer"), first.awayPlayers)
        assertEquals(MatchScore(3, 0), first.score)
        assertEquals(MatchScore(3, 2), matches[8].score)
    }

    @Test
    fun aDoublesMatchNamesBothPlayersOfEachPair() = runTest {
        val doubles = repository.getFixtureDetail("464821")!!.matches.filter { it.kind == MatchKind.Doubles }

        assertEquals(listOf(5, 10), doubles.map { it.number })
        assertEquals(listOf("Markus Luger", "Jonathan Himmelbauer"), doubles[0].homePlayers)
        assertEquals(listOf("Stefan Irndorfer", "Anton Irndorfer"), doubles[0].awayPlayers)
        assertEquals(MatchScore(3, 1), doubles[1].score)
    }

    @Test
    fun aFixtureNotYetPlayedHasNoMatchesAndNoScore() = runTest {
        val detail = repository.getFixtureDetail("9100")!!

        assertIs<FixtureStatus.Scheduled>(detail.fixture.status)
        assertEquals(emptyList(), detail.matches)
        assertEquals(Instant.parse("2027-03-06T17:30:00Z"), detail.fixture.startsAt)
    }

    @Test
    fun blankAddressPartsAreNullAndAnAddressWithNoPartsIsNull() = runTest {
        assertEquals(VenueAddress(street = null, postalCode = null, city = "Wien"), repository.getFixtureDetail("9100")!!.address)
    }

    @Test
    fun anUnknownFixtureIsNull() = runTest {
        assertNull(repository.getFixtureDetail("1"))
    }

    @Test
    fun aFixtureIdThatIsNotAnXttvIdIsNullWithoutAskingXttv() = runTest {
        assertNull(repository.getFixtureDetail("f3"))
        assertEquals(emptyList(), xttv.requests)
    }

    @Test
    fun aFailedRequestWithNothingCachedIsDataUnavailable() = runTest {
        xttv.failing = true

        assertFailsWith<DataUnavailable> { repository.getFixtureDetail("464821") }
    }

    @Test
    fun aFixtureIsCachedBetweenReads() = runTest {
        repository.getFixtureDetail("464821")
        repository.getFixtureDetail("464821")

        assertEquals(1, xttv.requests.size)
    }
}
