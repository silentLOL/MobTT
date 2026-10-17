package at.stefanirndorfer.batmate.core.xttv

import at.stefanirndorfer.batmate.domain.club.Association
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

class XttvAssociationRepositoryTest {
    private val requestedHosts = mutableListOf<String>()
    private var xml = XttvSamples.FEDERATIONS
    private var status = HttpStatusCode.OK
    private val client = HttpClient(
        MockEngine { request ->
            requestedHosts += request.url.host
            respond(xml, status, headersOf(HttpHeaders.ContentType, "application/xml"))
        },
    )
    private val repository = XttvAssociationRepository(
        XttvApi(client),
        XttvHostResolver(defaultHost = "https://default.example.test", hosts = mapOf("2" to "https://noettv.example.test")),
    )

    @Test
    fun federationsBecomeAssociations() = runTest {
        assertEquals(Association("1", "Wiener Tischtennisverband (WTTV)"), repository.getAssociation("1"))
        assertEquals("Niederösterreichischer Tischtennisverband (NÖTTV)", repository.getAssociation("2")?.name)
    }

    @Test
    fun anAssociationXttvDoesNotListIsNull() = runTest {
        assertNull(repository.getAssociation("99"))
    }

    @Test
    fun noFederationsAtAllIsNullNotAnError() = runTest {
        xml = XttvSamples.NO_FEDERATIONS

        assertNull(repository.getAssociation("1"))
    }

    @Test
    fun anAssociationIsAskedAtItsOwnHostAndOthersAtTheDefault() = runTest {
        repository.getAssociation("1")
        repository.getAssociation("2")

        assertEquals(listOf("default.example.test", "noettv.example.test"), requestedHosts)
    }

    @Test
    fun anUnreachableXttvThrowsDataUnavailable() = runTest {
        status = HttpStatusCode.ServiceUnavailable

        assertFailsWith<DataUnavailable> { repository.getAssociation("1") }
    }
}
