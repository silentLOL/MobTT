package at.stefanirndorfer.batmate.core.xttv

import at.stefanirndorfer.batmate.domain.club.DataUnavailable
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Duration.Companion.minutes
import kotlin.time.TestTimeSource

@Serializable
@XmlSerialName("clubs")
data class ClubsXml(val club: List<ClubXml> = emptyList())

@Serializable
@XmlSerialName("club")
data class ClubXml(@XmlElement(true) val name: String)

@OptIn(ExperimentalCoroutinesApi::class)
class XttvApiTest {
    private val url = "https://example.test/clubs"
    private val ttl = 10.minutes
    private val timeSource = TestTimeSource()
    private var requests = 0
    private var xml = "<clubs><club><name>TTC Olympic</name></club></clubs>"
    private var status = HttpStatusCode.OK
    private var gate: CompletableDeferred<Unit>? = null
    private val api = XttvApi(
        HttpClient(
            MockEngine {
                requests++
                gate?.await()
                respond(xml, status, headersOf(HttpHeaders.ContentType, "application/xml"))
            },
        ),
        timeSource,
    )

    private suspend fun clubNames() = api.get(url, ttl, ClubsXml.serializer()).club.map { it.name }

    @Test
    fun repeatReadWithinTtlMakesNoSecondRequest() = runTest {
        assertEquals(listOf("TTC Olympic"), clubNames())
        assertEquals(listOf("TTC Olympic"), clubNames())
        assertEquals(1, requests)
    }

    @Test
    fun readAfterTtlFetchesAgain() = runTest {
        clubNames()
        xml = "<clubs><club><name>UTTC Wien</name></club></clubs>"
        timeSource += ttl + 1.minutes

        assertEquals(listOf("UTTC Wien"), clubNames())
        assertEquals(2, requests)
    }

    @Test
    fun failedRefreshServesTheStaleEntry() = runTest {
        clubNames()
        xml = "<clubs><club><name>Should not be read</name></club></clubs>"
        status = HttpStatusCode.InternalServerError
        timeSource += ttl + 1.minutes

        assertEquals(listOf("TTC Olympic"), clubNames())
    }

    @Test
    fun failureWithNothingCachedThrowsDataUnavailable() = runTest {
        status = HttpStatusCode.ServiceUnavailable

        assertFailsWith<DataUnavailable> { clubNames() }
    }

    @Test
    fun unparsableAnswerWithNothingCachedThrowsDataUnavailable() = runTest {
        xml = "<html>maintenance"

        assertFailsWith<DataUnavailable> { clubNames() }
    }

    @Test
    fun unknownElementsAreIgnored() = runTest {
        xml = "<clubs><club><name>TTC Olympic</name><founded>1971</founded></club><note>new</note></clubs>"

        assertEquals(listOf("TTC Olympic"), clubNames())
    }

    @Test
    fun emptyAnswerIsAnEmptyListNotAnError() = runTest {
        xml = "<clubs/>"

        assertEquals(emptyList(), clubNames())
    }

    @Test
    fun identicalConcurrentReadsShareOneRequest() = runTest {
        val release = CompletableDeferred<Unit>().also { gate = it }

        val reads = List(3) { async { clubNames() } }
        runCurrent()
        release.complete(Unit)

        assertEquals(List(3) { listOf("TTC Olympic") }, reads.awaitAll())
        assertEquals(1, requests)
    }

    @Test
    fun readersArrivingAroundCompletionNeverSendASecondRequest() = runTest {
        val rounds = 40
        repeat(rounds) {
            timeSource += ttl + 1.minutes
            withContext(Dispatchers.Default) {
                List(100) { async { clubNames() } }.awaitAll()
            }
        }

        assertEquals(rounds, requests)
    }

    @Test
    fun afterAFailedRefreshReadsDoNotRetryUntilTheBackoffPasses() = runTest {
        clubNames()
        xml = "<clubs><club><name>UTTC Wien</name></club></clubs>"
        status = HttpStatusCode.ServiceUnavailable
        timeSource += ttl + 1.minutes

        assertEquals(listOf("TTC Olympic"), clubNames())
        assertEquals(listOf("TTC Olympic"), clubNames())
        assertEquals(2, requests)

        status = HttpStatusCode.OK
        timeSource += 2.minutes

        assertEquals(listOf("UTTC Wien"), clubNames())
        assertEquals(3, requests)
    }
}
