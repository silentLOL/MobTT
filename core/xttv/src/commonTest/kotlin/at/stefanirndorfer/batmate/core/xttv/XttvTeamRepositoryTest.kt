package at.stefanirndorfer.batmate.core.xttv

import at.stefanirndorfer.batmate.domain.club.DataUnavailable
import at.stefanirndorfer.batmate.domain.club.LineupPlayer
import at.stefanirndorfer.batmate.domain.club.Team
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.TestTimeSource

class XttvTeamRepositoryTest {
    private val xttv = FakeXttv(
        mapOf(
            XttvSamples.CLUB_140_URL to XttvSamples.CLUB_140,
            XttvSamples.CLUB_141_URL to XttvSamples.CLUB_141_WITHOUT_TEAMS,
            XttvSamples.TEAM_501_URL to XttvSamples.TEAM_501,
            XttvSamples.UNKNOWN_TEAM_URL to XttvSamples.UNKNOWN_TEAM,
        ),
    )
    private val timeSource = TestTimeSource()
    private val api = XttvApi(xttv.client, timeSource)
    private val repository get() = XttvTeamRepository(api, XttvSamples.HOST)

    @Test
    fun aClubsTeamsMapToTheirClubAndLeague() = runTest {
        assertEquals(
            listOf(Team("501", "LINZ AG Froschberg 1", "140", "70"), Team("502", "Linz-Froschberg AG 2", "140", "71")),
            repository.getTeams("140"),
        )
    }

    @Test
    fun aClubWithNoTeamsIsAnEmptyListNotAnError() = runTest {
        assertEquals(emptyList(), repository.getTeams("141"))
    }

    @Test
    fun aTeamIsLookedUpByIdAndItsPlayersAreIgnored() = runTest {
        assertEquals(Team("501", "LINZ AG Froschberg 1", "140", "70"), repository.getTeam("501"))
    }

    @Test
    fun idsXttvCannotKnowGiveNothingAndAskXttvForNothing() = runTest {
        assertNull(repository.getTeam("olympic-1"))
        assertEquals(emptyList(), repository.getTeams("olympic"))
        assertEquals(emptyList(), xttv.requests)
    }

    @Test
    fun anUnknownTeamIsNull() = runTest {
        assertNull(repository.getTeam("999"))
    }

    @Test
    fun repeatedLookupsAreServedFromTheCache() = runTest {
        repository.getTeam("501")
        repository.getTeam("501")

        assertEquals(1, xttv.requests.size)
    }

    @Test
    fun aFailedRequestThrowsDataUnavailable() = runTest {
        xttv.failing = true

        assertFailsWith<DataUnavailable> { repository.getTeam("501") }
        assertFailsWith<DataUnavailable> { repository.getTeams("140") }
    }

    @Test
    fun theLineupIsThePlayersTheTeamDetailsList() = runTest {
        assertEquals(listOf(LineupPlayer("60473", "Phatsaraphon Wonglakhon")), repository.getLineup("501"))
    }

    @Test
    fun aFailedRefreshServesTheStaleTeams() = runTest {
        val team = repository.getTeam("501")
        val teams = repository.getTeams("140")
        xttv.failing = true
        timeSource += 1.hours + 1.minutes

        assertEquals(team, repository.getTeam("501"))
        assertEquals(teams, repository.getTeams("140"))
    }
}
