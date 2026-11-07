package at.stefanirndorfer.mobtt.core.ratingscentral

import at.stefanirndorfer.mobtt.domain.club.DataUnavailable
import at.stefanirndorfer.mobtt.domain.rating.RatingHistory
import at.stefanirndorfer.mobtt.domain.rating.RatingHistoryRepository
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
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.TimeMark
import kotlin.time.TimeSource

/**
 * Rating histories from Ratings Central's history export (ADR-0010), asked for one Player at a time and only when the
 * user opens the section. Each history is held in memory for [freshFor]; a failed refresh serves the held one, and
 * identical concurrent requests share one call. With nothing held, a failure throws [DataUnavailable].
 */
class RatingsCentralHistories(
    private val client: HttpClient,
    private val host: String,
    private val timeSource: TimeSource.WithComparableMarks = TimeSource.Monotonic,
    private val freshFor: Duration = 24.hours,
) : RatingHistoryRepository {
    private class Held(val history: RatingHistory, val storedAt: TimeMark)

    private val log = Logger.withTag("RatingsCentralHistories")
    private val lock = Mutex()
    private val held = mutableMapOf<String, Held>()
    private val inFlight = mutableMapOf<String, CompletableDeferred<RatingHistory>>()

    override suspend fun getRatingHistory(ratingsCentralId: String): RatingHistory {
        val (cached, pending, isOwner) = lock.withLock {
            val entry = held[ratingsCentralId]
            if (entry != null && entry.storedAt.elapsedNow() < freshFor) {
                return entry.history
            }
            val existing = inFlight[ratingsCentralId]
            Triple(entry, existing ?: CompletableDeferred<RatingHistory>().also { inFlight[ratingsCentralId] = it }, existing == null)
        }
        if (!isOwner) {
            return try {
                pending.await()
            } catch (e: CancellationException) {
                // The request's owner was cancelled, not this caller: ask again.
                currentCoroutineContext().ensureActive()
                getRatingHistory(ratingsCentralId)
            }
        }
        return try {
            val history = download(ratingsCentralId)
            lock.withLock { held[ratingsCentralId] = Held(history, timeSource.markNow()) }
            pending.complete(history)
            history
        } catch (e: CancellationException) {
            pending.cancel(e)
            throw e
        } catch (e: Exception) {
            val fallback = cached?.history
            log.w(e) { "Rating history of Ratings Central ID $ratingsCentralId failed; ${fallback?.let { "serving the held one" } ?: "nothing held"}" }
            if (fallback == null) {
                val unavailable = DataUnavailable("Ratings Central is unavailable for $ratingsCentralId", e)
                pending.completeExceptionally(unavailable)
                throw unavailable
            }
            pending.complete(fallback)
            fallback
        } finally {
            withContext(NonCancellable) { lock.withLock { inFlight.remove(ratingsCentralId) } }
        }
    }

    private suspend fun download(ratingsCentralId: String): RatingHistory {
        val response = client.get("$host/PlayerHistory.php?PlayerID=$ratingsCentralId&CSV_Output=Text")
        check(response.status.isSuccess()) { "Ratings Central answered ${response.status}" }
        return parseRatingHistoryCsv(response.bodyAsText())
    }
}
