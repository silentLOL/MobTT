package at.stefanirndorfer.batmate.core.xttv

import at.stefanirndorfer.batmate.domain.club.DataUnavailable
import at.stefanirndorfer.batmate.domain.club.LineupPlayer
import at.stefanirndorfer.batmate.domain.club.Team
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

class XttvTeamRepositoryTest {
    private val requests = mutableListOf<String>()
    private var failing = false
    private val answers = mutableMapOf(
        "https://default.example.test/api/getClubs" to XttvSamples.CLUBS,
        "https://default.example.test/api/getClubDetails?id=8846" to XttvSamples.CLUB_8846,
        "https://noettv.example.test/api/getClubDetails?id=9001" to XttvSamples.CLUB_9001,
        "https://default.example.test/api/getLeagueTeams?clubId=8846" to XttvSamples.CLUB_8846_TEAMS,
        "https://noettv.example.test/api/getLeagueTeams?clubId=9001" to XttvSamples.NO_TEAMS,
        "https://default.example.test/api/getTeamDetails?id=501" to XttvSamples.TEAM_501,
        "https://default.example.test/api/getTeamDetails?id=999" to XttvSamples.NO_TEAMS,
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
    private val hosts = XttvHostResolver(
        defaultHost = "https://default.example.test",
        hosts = mapOf("2" to "https://noettv.example.test"),
    )
    private var lineups: LineupSource? = null
    private val repository get() = XttvTeamRepository(api, hosts, XttvClubRepository(api, hosts), lineups)

    @Test
    fun aClubsTeamsMapToTheirClubAndLeague() = runTest {
        assertEquals(
            listOf(Team("501", "TTC Olympic Wien 1", "8846", "70"), Team("502", "TTC Olympic Wien 2", "8846", "71")),
            repository.getTeams("8846"),
        )
    }

    @Test
    fun aClubWithNoTeamsIsAnEmptyListNotAnError() = runTest {
        assertEquals(emptyList(), repository.getTeams("9001"))
    }

    @Test
    fun anUnknownClubHasNoTeams() = runTest {
        assertEquals(emptyList(), repository.getTeams("1"))
    }

    @Test
    fun aClubsTeamsAreAskedAtItsAssociationsHost() = runTest {
        repository.getTeams("9001")

        assertEquals("https://noettv.example.test/api/getLeagueTeams?clubId=9001", requests.last())
    }

    @Test
    fun aTeamIsLookedUpById() = runTest {
        assertEquals(Team("501", "TTC Olympic Wien 1", "8846", "70"), repository.getTeam("501"))
    }

    @Test
    fun anUnknownTeamIsNull() = runTest {
        assertNull(repository.getTeam("999"))
    }

    @Test
    fun repeatedLookupsAreServedFromTheCache() = runTest {
        repository.getTeam("501")
        repository.getTeam("501")

        assertEquals(1, requests.size)
    }

    @Test
    fun aFailedRequestThrowsDataUnavailable() = runTest {
        failing = true

        assertFailsWith<DataUnavailable> { repository.getTeam("501") }
        assertFailsWith<DataUnavailable> { repository.getTeams("8846") }
    }

    @Test
    fun theLineupComesFromTheLineupSourceAndIsEmptyWithoutOne() = runTest {
        assertEquals(emptyList(), repository.getLineup("501"))

        lineups = LineupSource { listOf(LineupPlayer("huber", "Michael Huber")) }

        assertEquals(listOf(LineupPlayer("huber", "Michael Huber")), repository.getLineup("501"))
    }
}
