package at.stefanirndorfer.batmate.core.network

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respondOk
import io.ktor.client.request.get
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class HttpClientFactoryTest {
    @Test
    fun everyRequestIdentifiesTheApp() = runTest {
        var userAgent: String? = null
        val client = createHttpClient(
            userAgent = "BatMate/1.2 (contact@example.test)",
            engine = MockEngine { request ->
                userAgent = request.headers[HttpHeaders.UserAgent]
                respondOk()
            },
        )

        client.get("https://example.test/clubs")

        assertEquals("BatMate/1.2 (contact@example.test)", userAgent)
    }

    @Test
    fun noMoreRequestsRunInParallelThanTheCap() = runTest {
        val lock = Mutex()
        var running = 0
        var peak = 0
        val client = createHttpClient(
            userAgent = "BatMate/1.2",
            engine = MockEngine {
                lock.withLock {
                    running++
                    peak = maxOf(peak, running)
                }
                delay(30)
                lock.withLock { running-- }
                respondOk()
            },
        )

        List(6) { async { client.get("https://example.test/clubs?page=$it") } }.awaitAll()

        assertEquals(2, peak)
    }
}
