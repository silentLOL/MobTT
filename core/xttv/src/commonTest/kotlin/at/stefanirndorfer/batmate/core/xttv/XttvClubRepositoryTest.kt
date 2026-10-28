package at.stefanirndorfer.batmate.core.xttv

import at.stefanirndorfer.batmate.domain.club.Club
import at.stefanirndorfer.batmate.domain.club.DataUnavailable
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.TestTimeSource

class XttvClubRepositoryTest {
    private var clubs190 = XttvSamples.CLUBS_190
    private val xttv = FakeXttv {
        mapOf(
            XttvSamples.FEDERATIONS_URL to XttvSamples.FEDERATIONS,
            XttvSamples.CLUBS_188_URL to XttvSamples.CLUBS_188,
            XttvSamples.CLUBS_190_URL to clubs190,
            XttvSamples.CLUB_140_URL to XttvSamples.CLUB_140,
            XttvSamples.UNKNOWN_CLUB_URL to XttvSamples.UNKNOWN_CLUB,
        )
    }
    private val timeSource = TestTimeSource()
    private val repository = XttvClubRepository(XttvApi(xttv.client, timeSource), XttvSamples.HOST)

    @Test
    fun aBlankQueryListsEveryFederationsClubsWithTheirAssociation() = runTest {
        assertEquals(
            listOf(
                Club("1", "ATUS Gumpoldskirchen", "188"),
                Club("3", "Union Raika Allhartsberg", "188"),
                Club("77", "TTC Krumpendorf", "190"),
            ),
            repository.searchClubs(""),
        )
    }

    @Test
    fun aQueryMatchesOnClubNameIgnoringCase() = runTest {
        assertEquals(listOf("77"), repository.searchClubs("krumpen").map { it.id })
        assertEquals(emptyList(), repository.searchClubs("nobody"))
    }

    @Test
    fun aClubIsReadDirectlyAndNamesItsAssociation() = runTest {
        assertEquals(Club("140", "LINZ AG-FROSCHBERG", "191"), repository.getClub("140"))
        assertEquals(listOf(XttvSamples.CLUB_140_URL), xttv.requests)
    }

    @Test
    fun aClubXttvDoesNotKnowIsNull() = runTest {
        assertNull(repository.getClub("999"))
    }

    @Test
    fun anIdXttvCannotKnowIsNullAndAsksXttvForNothing() = runTest {
        assertNull(repository.getClub("olympic"))
        assertEquals(emptyList(), xttv.requests)
    }

    @Test
    fun noClubsAtAllIsAnEmptyListNotAnError() = runTest {
        clubs190 = XttvSamples.NO_CLUBS

        assertEquals(listOf("1", "3"), repository.searchClubs("").map { it.id })
    }

    @Test
    fun repeatedSearchesAreServedFromTheCache() = runTest {
        repository.searchClubs("")
        repository.searchClubs("krumpen")

        assertEquals(3, xttv.requests.size)
    }

    @Test
    fun aFailedRequestThrowsDataUnavailable() = runTest {
        xttv.failing = true

        assertFailsWith<DataUnavailable> { repository.searchClubs("") }
        assertFailsWith<DataUnavailable> { repository.getClub("140") }
    }

    @Test
    fun aFailedRefreshServesTheStaleClubs() = runTest {
        val clubs = repository.searchClubs("")
        val club = repository.getClub("140")
        xttv.failing = true
        timeSource += 1.hours + 1.minutes

        assertEquals(clubs, repository.searchClubs(""))
        assertEquals(club, repository.getClub("140"))
    }
}
