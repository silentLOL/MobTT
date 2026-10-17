package at.stefanirndorfer.batmate.core.xttv

import at.stefanirndorfer.batmate.domain.club.DataUnavailable
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.KSerializer
import nl.adaptivity.xmlutil.serialization.XML
import kotlin.time.Duration
import kotlin.time.TimeMark
import kotlin.time.TimeSource

/**
 * The one door to XTTV: fetches [get]'s `url`, parses the XML and caches the result for its `ttl`.
 * A failed refresh serves the stale entry; with nothing cached it throws [DataUnavailable].
 */
class XttvApi(
    private val client: HttpClient,
    private val timeSource: TimeSource.WithComparableMarks = TimeSource.Monotonic,
) {
    private class CacheEntry(val value: Any, val storedAt: TimeMark)

    private val xml = XML { defaultPolicy { ignoreUnknownChildren() } }
    private val cacheLock = Mutex()
    private val cache = mutableMapOf<String, CacheEntry>()
    private val inFlightLock = Mutex()
    private val inFlight = mutableMapOf<String, CompletableDeferred<Any>>()

    @Suppress("UNCHECKED_CAST")
    suspend fun <T : Any> get(url: String, ttl: Duration, serializer: KSerializer<T>): T {
        val cached = cacheLock.withLock { cache[url] }
        if (cached != null && cached.storedAt.elapsedNow() < ttl) {
            return cached.value as T
        }

        return try {
            fetchShared(url, serializer).also { value ->
                cacheLock.withLock { cache[url] = CacheEntry(value, timeSource.markNow()) }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            if (cached != null) {
                return cached.value as T
            }
            throw DataUnavailable("XTTV is unavailable for $url", e)
        }
    }

    /** Identical concurrent reads of [url] wait for one request instead of sending their own. */
    @Suppress("UNCHECKED_CAST")
    private suspend fun <T : Any> fetchShared(url: String, serializer: KSerializer<T>): T {
        while (true) {
            var owner = false
            val call = inFlightLock.withLock {
                inFlight.getOrPut(url) { CompletableDeferred<Any>().also { owner = true } }
            }
            if (!owner) {
                try {
                    return call.await() as T
                } catch (e: CancellationException) {
                    // The request's owner was cancelled, not this caller: take over.
                    currentCoroutineContext().ensureActive()
                    continue
                }
            }
            try {
                return download(url, serializer).also { call.complete(it) }
            } catch (e: Throwable) {
                call.completeExceptionally(e)
                throw e
            } finally {
                withContext(NonCancellable) { inFlightLock.withLock { inFlight.remove(url) } }
            }
        }
    }

    private suspend fun <T : Any> download(url: String, serializer: KSerializer<T>): T {
        val response = client.get(url)
        check(response.status.isSuccess()) { "XTTV answered ${response.status} for $url" }
        return xml.decodeFromString(serializer, response.bodyAsText())
    }
}
