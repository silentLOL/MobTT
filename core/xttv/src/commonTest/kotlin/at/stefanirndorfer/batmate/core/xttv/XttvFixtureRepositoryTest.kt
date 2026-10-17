package at.stefanirndorfer.batmate.core.xttv

import at.stefanirndorfer.batmate.domain.club.DataUnavailable
import at.stefanirndorfer.batmate.domain.club.Fixture
import at.stefanirndorfer.batmate.domain.club.FixtureScore
import at.stefanirndorfer.batmate.domain.club.FixtureStatus
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.time.Instant

class XttvFixtureRepositoryTest {
    private val requests = mutableListOf<String>()
    private var failing = false
    private val answers = mapOf(
        "https://default.example.test/api/getClubs" to XttvSamples.CLUBS,
        "https://default.example.test/api/getClubDetails?id=8846" to XttvSamples.CLUB_8846,
        "https://default.example.test/api/getTeamDetails?id=501" to XttvSamples.TEAM_501,
        "https://default.example.test/api/getTeamDetails?id=999" to XttvSamples.NO_TEAMS,
        "https://default.example.test/api/getLeagueGames?leagueId=70" to XttvSamples.LEAGUE_70_GAMES,
    )
    private val client = HttpClient(
        MockEngine { request ->
            requests += request.url.toString()
            val body = answers[request.url.toString()].takeUnless { failing }
            if (body == null) {
                respond("", HttpStatusCode.ServiceUnavailable)
            } else {
                respond(body, HttpStatusCode.OK, headersOf(HttpHeaders.ContentType, "application/xml"))
            }
        },
    )
    private val api = XttvApi(client)
    private val hosts = XttvHostResolver(defaultHost = "https://default.example.test")
    private val clubs = XttvClubRepository(api, hosts)
    private val repository = XttvFixtureRepository(api, hosts, XttvTeamRepository(api, hosts, clubs), clubs)

    private suspend fun fixture(id: String): Fixture = repository.getFixtures("501").first { it.id == id }

    @Test
    fun aTeamPlaysHomeAndAwayAndOtherTeamsGamesAreLeftOut() = runTest {
        assertEquals(listOf("9002", "9005", "9006", "9001"), repository.getFixtures("501").map { it.id })
    }

    @Test
    fun aFinishedFixtureCarriesItsMatchesWonScore() = runTest {
        val finished = fixture("9002")

        assertEquals(FixtureStatus.Finished(FixtureScore(4, 6)), finished.status)
        assertEquals("SK VÖEST Wien 1", finished.homeTeamName)
        assertEquals("TTC Olympic Wien 1", finished.awayTeamName)
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
        assertEquals(false, repository.getFixtures("501").any { it.id == "9004" })
    }

    @Test
    fun anUnknownTeamHasNoFixtures() = runTest {
        assertEquals(emptyList(), repository.getFixtures("999"))
    }

    @Test
    fun repeatedReadsAreServedFromTheCache() = runTest {
        repository.getFixtures("501")
        val sent = requests.size
        repository.getFixtures("501")

        assertEquals(sent, requests.size)
    }

    @Test
    fun aFailedRequestThrowsDataUnavailable() = runTest {
        failing = true

        assertFailsWith<DataUnavailable> { repository.getFixtures("501") }
    }
}
