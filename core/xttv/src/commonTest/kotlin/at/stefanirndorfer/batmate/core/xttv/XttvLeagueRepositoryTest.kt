package at.stefanirndorfer.batmate.core.xttv

import at.stefanirndorfer.batmate.domain.club.DataUnavailable
import at.stefanirndorfer.batmate.domain.club.League
import at.stefanirndorfer.batmate.domain.club.Standing
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
import kotlin.test.assertNull

class XttvLeagueRepositoryTest {
    private val requests = mutableListOf<String>()
    private var failing = false
    private val answers = mapOf(
        "https://default.example.test/api/getLeagueDetails?id=70" to XttvSamples.LEAGUE_70,
        "https://default.example.test/api/getLeagueDetails?id=99" to XttvSamples.NO_LEAGUES,
        "https://default.example.test/api/getTeamStandings?leagueId=70" to XttvSamples.LEAGUE_70_STANDINGS,
        "https://default.example.test/api/getTeamStandings?leagueId=99" to XttvSamples.NO_STANDINGS,
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
    private val repository = XttvLeagueRepository(XttvApi(client), XttvHostResolver(defaultHost = "https://default.example.test"))

    @Test
    fun aLeagueIsLookedUpById() = runTest {
        assertEquals(League("70", "1. Landesliga Herren"), repository.getLeague("70"))
    }

    @Test
    fun anUnknownLeagueIsNull() = runTest {
        assertNull(repository.getLeague("99"))
    }

    @Test
    fun standingsAreOrderedByPositionAndCarryPlayedAndPoints() = runTest {
        assertEquals(
            listOf(
                Standing(1, "501", "TTC Olympic Wien 1", played = 5, points = 10),
                Standing(2, "600", "SK VÖEST Wien 1", played = 5, points = 8),
            ),
            repository.getStandings("70"),
        )
    }

    @Test
    fun aLeagueWithNoStandingsIsAnEmptyListNotAnError() = runTest {
        assertEquals(emptyList(), repository.getStandings("99"))
    }

    @Test
    fun repeatedReadsAreServedFromTheCache() = runTest {
        repository.getStandings("70")
        repository.getStandings("70")

        assertEquals(1, requests.size)
    }

    @Test
    fun aFailedRequestThrowsDataUnavailable() = runTest {
        failing = true

        assertFailsWith<DataUnavailable> { repository.getLeague("70") }
        assertFailsWith<DataUnavailable> { repository.getStandings("70") }
    }
}
