package at.stefanirndorfer.batmate.core.xttv

import at.stefanirndorfer.batmate.domain.club.DataUnavailable
import at.stefanirndorfer.batmate.domain.club.League
import at.stefanirndorfer.batmate.domain.club.Standing
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class XttvLeagueRepositoryTest {
    private val answers = mapOf(
        "https://default.example.test/api/getLeagueDetails?id=70" to XttvSamples.LEAGUE_70,
        "https://default.example.test/api/getLeagueDetails?id=99" to XttvSamples.NO_LEAGUES,
        "https://default.example.test/api/getTeamStandings?leagueId=70" to XttvSamples.LEAGUE_70_STANDINGS,
        "https://default.example.test/api/getTeamStandings?leagueId=99" to XttvSamples.NO_STANDINGS,
    )
    private val xttv = FakeXttv(answers)
    private val repository = XttvLeagueRepository(XttvApi(xttv.client), XttvHostResolver(defaultHost = "https://default.example.test"))

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
    fun aBlankLeagueHasNoStandingsAndAsksXttvForNone() = runTest {
        assertEquals(emptyList(), repository.getStandings(""))
        assertEquals(emptyList(), xttv.requests)
    }

    @Test
    fun repeatedReadsAreServedFromTheCache() = runTest {
        repository.getStandings("70")
        repository.getStandings("70")

        assertEquals(1, xttv.requests.size)
    }

    @Test
    fun aFailedRequestThrowsDataUnavailable() = runTest {
        xttv.failing = true

        assertFailsWith<DataUnavailable> { repository.getLeague("70") }
        assertFailsWith<DataUnavailable> { repository.getStandings("70") }
    }
}
