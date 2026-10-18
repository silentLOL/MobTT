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
    private var list = XttvSamples.CLUBS
    private val xttv = FakeXttv {
        mapOf(
            "https://default.example.test/api/getClubs" to list,
            "https://default.example.test/api/getClubDetails?id=8846" to XttvSamples.CLUB_8846,
            "https://noettv.example.test/api/getClubDetails?id=9001" to XttvSamples.CLUB_9001,
        )
    }
    private val timeSource = TestTimeSource()
    private val repository = XttvClubRepository(
        XttvApi(xttv.client, timeSource),
        XttvHostResolver(defaultHost = "https://default.example.test", hosts = mapOf("2" to "https://noettv.example.test")),
    )

    @Test
    fun aBlankQueryListsEveryClub() = runTest {
        assertEquals(
            listOf(Club("8846", "TTC Olympic Wien", "1"), Club("9001", "UTTC Olympia Krems", "2")),
            repository.searchClubs(""),
        )
    }

    @Test
    fun aQueryMatchesOnClubNameIgnoringCase() = runTest {
        assertEquals(listOf("9001"), repository.searchClubs("krems").map { it.id })
        assertEquals(emptyList(), repository.searchClubs("nobody"))
    }

    @Test
    fun aClubResolvesItsAssociation() = runTest {
        assertEquals(Club("9001", "UTTC Olympia Krems", "2"), repository.getClub("9001"))
    }

    @Test
    fun aClubsDetailsAreAskedAtItsAssociationsHost() = runTest {
        repository.getClub("8846")
        repository.getClub("9001")

        assertEquals(
            listOf(
                "https://default.example.test/api/getClubs",
                "https://default.example.test/api/getClubDetails?id=8846",
                "https://noettv.example.test/api/getClubDetails?id=9001",
            ),
            xttv.requests,
        )
    }

    @Test
    fun aClubXttvDoesNotKnowIsNull() = runTest {
        assertNull(repository.getClub("1"))
    }

    @Test
    fun noClubsAtAllIsAnEmptyListNotAnError() = runTest {
        list = XttvSamples.NO_CLUBS

        assertEquals(emptyList(), repository.searchClubs(""))
    }

    @Test
    fun repeatedSearchesAreServedFromTheCache() = runTest {
        repository.searchClubs("")
        repository.searchClubs("krems")

        assertEquals(1, xttv.requests.size)
    }

    @Test
    fun aFailedRequestThrowsDataUnavailable() = runTest {
        xttv.failing = true

        assertFailsWith<DataUnavailable> { repository.searchClubs("") }
        assertFailsWith<DataUnavailable> { repository.getClub("8846") }
    }

    @Test
    fun aFailedRefreshServesTheStaleClubs() = runTest {
        val clubs = repository.searchClubs("")
        val club = repository.getClub("8846")
        xttv.failing = true
        timeSource += 1.hours + 1.minutes

        assertEquals(clubs, repository.searchClubs(""))
        assertEquals(club, repository.getClub("8846"))
    }
}
