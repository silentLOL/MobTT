package at.stefanirndorfer.batmate.core.xttv

import at.stefanirndorfer.batmate.domain.club.DataUnavailable
import at.stefanirndorfer.batmate.domain.club.Fixture
import at.stefanirndorfer.batmate.domain.club.FixtureScore
import at.stefanirndorfer.batmate.domain.club.FixtureStatus
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Instant

class XttvFixtureRepositoryTest {
    private val xttv = FakeXttv(
        mapOf(
            XttvSamples.TEAM_501_URL to XttvSamples.TEAM_501,
            XttvSamples.TEAM_503_URL to XttvSamples.TEAM_503_WITHOUT_LEAGUE,
            XttvSamples.UNKNOWN_TEAM_URL to XttvSamples.UNKNOWN_TEAM,
            XttvSamples.GAMES_70_URL to XttvSamples.GAMES_70,
        ),
    )
    private val api = XttvApi(xttv.client)
    private val repository = XttvFixtureRepository(api, XttvSamples.HOST, XttvTeamRepository(api, XttvSamples.HOST))

    private suspend fun fixture(id: String): Fixture = repository.getFixtures("501").first { it.id == id }

    @Test
    fun aTeamPlaysHomeAndAwayAndOtherTeamsGamesAreLeftOut() = runTest {
        assertEquals(listOf("9002", "9005", "9006", "9001"), repository.getFixtures("501").map { it.id })
    }

    @Test
    fun aLeagueListsEveryGameOfEveryTeamInDateOrder() = runTest {
        assertEquals(listOf("9002", "9005", "9006", "9001", "9003", "9007"), repository.getLeagueFixtures("70").map { it.id })
    }

    @Test
    fun aZeroToZeroGameIsNotYetPlayed() = runTest {
        assertIs<FixtureStatus.Scheduled>(repository.getLeagueFixtures("70").first { it.id == "9007" }.status)
    }

    @Test
    fun aFinishedFixtureCarriesItsMatchesWonScore() = runTest {
        val finished = fixture("9002")

        assertEquals(FixtureStatus.Finished(FixtureScore(4, 6)), finished.status)
        assertEquals("SK VÖEST 1", finished.homeTeamName)
        assertEquals("TTC Olympic 1", finished.awayTeamName)
    }

    @Test
    fun aScheduledFixtureHasNoScoreAndAnEmptyScoreElementDoesNotMakeItFinished() = runTest {
        assertIs<FixtureStatus.Scheduled>(fixture("9001").status)
    }

    @Test
    fun theVenueIsNullWhenXttvGivesNoneOrABlankOne() = runTest {
        assertEquals("Sporthalle Olympic", fixture("9001").venue)
        assertNull(fixture("9006").venue)
        assertNull(fixture("9005").venue)
    }

    @Test
    fun localTimesFollowAustrianTimeInSummerAndWinter() = runTest {
        assertEquals(Instant.parse("2026-07-01T16:00:00Z"), fixture("9002").startsAt)
        assertEquals(Instant.parse("2026-12-01T17:00:00Z"), fixture("9001").startsAt)
    }

    @Test
    fun localTimesAroundTheAutumnSwitchUseTheirOwnOffsets() = runTest {
        assertEquals(Instant.parse("2026-10-24T23:30:00Z"), fixture("9005").startsAt)
        assertEquals(Instant.parse("2026-10-25T02:30:00Z"), fixture("9006").startsAt)
    }

    @Test
    fun aGameWithAnUnreadableDateIsSkippedNotFatal() = runTest {
        assertTrue(repository.getFixtures("501").none { it.id == "9004" })
    }

    @Test
    fun anUnknownTeamHasNoFixtures() = runTest {
        assertEquals(emptyList(), repository.getFixtures("999"))
    }

    @Test
    fun aTeamWithoutALeagueHasNoFixturesAndAsksXttvForNoGames() = runTest {
        assertEquals(emptyList(), repository.getFixtures("503"))
        assertTrue(xttv.requests.none { "getLeagueGames" in it })
    }

    @Test
    fun repeatedReadsAreServedFromTheCache() = runTest {
        repository.getFixtures("501")
        val sent = xttv.requests.size
        repository.getFixtures("501")

        assertEquals(sent, xttv.requests.size)
    }

    @Test
    fun aFailedRequestThrowsDataUnavailable() = runTest {
        xttv.failing = true

        assertFailsWith<DataUnavailable> { repository.getFixtures("501") }
    }
}
