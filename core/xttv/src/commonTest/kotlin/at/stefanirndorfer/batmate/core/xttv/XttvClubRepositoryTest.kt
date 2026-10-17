package at.stefanirndorfer.batmate.core.xttv

import at.stefanirndorfer.batmate.domain.club.Club
import at.stefanirndorfer.batmate.domain.club.DataUnavailable
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

class XttvClubRepositoryTest {
    private val requests = mutableListOf<String>()
    private var failing = false
    private var list = XttvSamples.CLUBS
    private val client = HttpClient(
        MockEngine { request ->
            requests += "${request.url.host}${request.url.encodedPath}"
            val answers = mapOf(
                "https://default.example.test/api/getClubs" to list,
                "https://default.example.test/api/getClubDetails?id=8846" to XttvSamples.CLUB_8846,
                "https://noettv.example.test/api/getClubDetails?id=9001" to XttvSamples.CLUB_9001,
            )
            val body = answers[request.url.toString()].takeUnless { failing }
            if (body == null) {
                respond("", HttpStatusCode.ServiceUnavailable)
            } else {
                respond(body, HttpStatusCode.OK, headersOf(HttpHeaders.ContentType, "application/xml"))
            }
        },
    )
    private val repository = XttvClubRepository(
        XttvApi(client),
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
            listOf("default.example.test/api/getClubs", "default.example.test/api/getClubDetails", "noettv.example.test/api/getClubDetails"),
            requests,
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

        assertEquals(1, requests.size)
    }

    @Test
    fun aFailedRequestThrowsDataUnavailable() = runTest {
        failing = true

        assertFailsWith<DataUnavailable> { repository.searchClubs("") }
        assertFailsWith<DataUnavailable> { repository.getClub("8846") }
    }
}
