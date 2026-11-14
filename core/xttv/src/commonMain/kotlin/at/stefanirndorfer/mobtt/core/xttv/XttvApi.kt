package at.stefanirndorfer.mobtt.core.xttv

import at.stefanirndorfer.mobtt.domain.club.DataUnavailable
import co.touchlab.kermit.Logger
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
import kotlin.time.Duration.Companion.minutes
import kotlin.time.TimeMark
import kotlin.time.TimeSource

private val FAILED_REFRESH_BACKOFF = 1.minutes

/** XTTV's answer to a missing or wrong key: a leading comment, then an empty `<xml>`. Data that merely says so does not match. */
private val REJECTED_API_KEY = Regex("""^\s*(<\?xml[^>]*\?>\s*)?<!--\s*Invalid API key""")

/**
 * The one door to XTTV: fetches [get]'s `url`, parses the XML and caches the result for its `ttl`.
 * A zero `ttl` is not cached at all. A failed refresh serves the stale entry and leaves XTTV alone for a minute at most; with nothing cached
 * it throws [DataUnavailable].
 */
class XttvApi(
    private val client: HttpClient,
    private val timeSource: TimeSource.WithComparableMarks = TimeSource.Monotonic,
) {
    private class CacheEntry(val value: Any, val storedAt: TimeMark, val freshFor: Duration) {
        fun isFresh() = storedAt.elapsedNow() < freshFor
    }

    private val log = Logger.withTag("XttvApi")
    private val xml = XML.v1 { policy { ignoreUnknownChildren() } }
    private val cacheLock = Mutex()
    private val cache = mutableMapOf<String, CacheEntry>()
    private val inFlightLock = Mutex()
    private val inFlight = mutableMapOf<String, CompletableDeferred<Any>>()

    @Suppress("UNCHECKED_CAST")
    suspend fun <T : Any> get(url: String, ttl: Duration, serializer: KSerializer<T>): T {
        val cached = cachedEntry(url)
        if (cached != null && cached.isFresh()) {
            return cached.value as T
        }

        return try {
            fetchShared(url, ttl, serializer)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // The query string is left out of the log: it may carry credentials.
            val fallback = cached?.let { "serving stale entry" } ?: "nothing cached"
            log.w(e) { "Refresh failed for ${url.substringBefore('?')}; $fallback" }
            if (cached == null) {
                throw DataUnavailable("XTTV is unavailable for $url", e)
            }
            store(url, cached.value, minOf(ttl, FAILED_REFRESH_BACKOFF))
            cached.value as T
        }
    }

    private suspend fun cachedEntry(url: String) = cacheLock.withLock { cache[url] }

    private suspend fun store(url: String, value: Any, freshFor: Duration) {
        cacheLock.withLock { cache[url] = CacheEntry(value, timeSource.markNow(), freshFor) }
    }

    /**
     * Identical concurrent reads of [url] wait for one request instead of sending their own. The owner
     * caches the result before it lets go of the in-flight slot, so a reader arriving later sees one or the other.
     */
    @Suppress("UNCHECKED_CAST")
    private suspend fun <T : Any> fetchShared(url: String, ttl: Duration, serializer: KSerializer<T>): T {
        while (true) {
            var isOwner = false
            val sharedResult = inFlightLock.withLock {
                inFlight.getOrPut(url) { CompletableDeferred<Any>().also { isOwner = true } }
            }
            if (!isOwner) {
                try {
                    return sharedResult.await() as T
                } catch (e: CancellationException) {
                    // The request's owner was cancelled, not this caller: take over.
                    currentCoroutineContext().ensureActive()
                    continue
                }
            }
            try {
                // Another owner may have finished between this caller's cache check and taking the slot.
                val fresh = cachedEntry(url)?.takeIf { it.isFresh() }?.value as T?
                val value = fresh ?: download(url, serializer)
                if (fresh == null && ttl.isPositive()) {
                    store(url, value, ttl)
                }
                sharedResult.complete(value)
                return value
            } catch (e: Throwable) {
                sharedResult.completeExceptionally(e)
                throw e
            } finally {
                withContext(NonCancellable) { inFlightLock.withLock { inFlight.remove(url) } }
            }
        }
    }

    private suspend fun <T : Any> download(url: String, serializer: KSerializer<T>): T {
        val response = client.get(url)
        check(response.status.isSuccess()) { "XTTV answered ${response.status} for $url" }
        val body = response.bodyAsText()
        // XTTV answers a missing or wrong key with 200 and an empty `<xml>` document, which would read as "no data".
        check(!REJECTED_API_KEY.containsMatchIn(body)) { "XTTV rejected the API key" }
        return xml.decodeFromString(serializer, body)
    }
}
