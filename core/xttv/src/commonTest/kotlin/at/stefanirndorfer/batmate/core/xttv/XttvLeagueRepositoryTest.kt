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
    private val xttv = FakeXttv(
        mapOf(
            XttvSamples.FEDERATIONS_URL to XttvSamples.FEDERATIONS,
            XttvSamples.LEAGUES_188_URL to XttvSamples.LEAGUES_188,
            XttvSamples.LEAGUES_190_URL to XttvSamples.LEAGUES_190,
            XttvSamples.STANDINGS_70_URL to XttvSamples.STANDINGS_70,
            XttvSamples.STANDINGS_99_URL to XttvSamples.NO_STANDINGS,
        ),
    )
    private val repository = XttvLeagueRepository(XttvApi(xttv.client), XttvSamples.HOST)

    @Test
    fun aLeagueIsFoundInAnyFederationsList() = runTest {
        assertEquals(League("70", "Kärntner Liga"), repository.getLeague("70"))
        assertEquals(League("8001", "NÖ Landesliga"), repository.getLeague("8001"))
    }

    @Test
    fun anUnknownLeagueIsNull() = runTest {
        assertNull(repository.getLeague("99"))
    }

    @Test
    fun standingsAreOrderedByRankAndCarryPlayedAndPoints() = runTest {
        assertEquals(
            listOf(
                Standing(1, "501", "TTC Olympic 1", played = 5, points = 10),
                Standing(2, "600", "SK VÖEST 1", played = 5, points = 8),
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
