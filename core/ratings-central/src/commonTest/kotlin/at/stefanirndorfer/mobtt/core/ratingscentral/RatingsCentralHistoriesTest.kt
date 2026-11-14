package at.stefanirndorfer.mobtt.core.ratingscentral

import at.stefanirndorfer.mobtt.domain.club.DataUnavailable
import at.stefanirndorfer.mobtt.domain.rating.Rating
import at.stefanirndorfer.mobtt.domain.rating.RatingHistory
import at.stefanirndorfer.mobtt.domain.rating.RatingUpdate
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Duration.Companion.hours
import kotlin.time.TestTimeSource

private class FakeRatingsCentral(var body: String = RatingsCentralSamples.HISTORY_163468) {
    val requests = mutableListOf<String>()
    var failing = false
    val client = HttpClient(
        MockEngine { request ->
            requests += request.url.toString()
            if (failing || request.url.toString() != RatingsCentralSamples.HISTORY_URL_163468) {
                respond("", HttpStatusCode.ServiceUnavailable)
            } else {
                respond(body, HttpStatusCode.OK, headersOf(HttpHeaders.ContentType, "text/plain"))
            }
        },
    )
}

class RatingsCentralHistoriesTest {
    private val ratingsCentral = FakeRatingsCentral()
    private val timeSource = TestTimeSource()
    private val histories = RatingsCentralHistories(ratingsCentral.client, RatingsCentralSamples.HOST, timeSource)

    private val recorded = RatingHistory(
        listOf(
            RatingUpdate(LocalDate(2025, 10, 19), "OÖTTV Meisterschaft 2025/26 (Spiele KW 42/2025)", Rating(1450, 450), Rating(331, 162), -1119),
            RatingUpdate(LocalDate(2026, 10, 18), "OÖTTV Meisterschaft 2026/27 (Spiele KW 42/2026)", Rating(400, 57), Rating(447, 65), 47),
            RatingUpdate(LocalDate(2026, 10, 25), "OÖTTV Meisterschaft 2026/27 (Spiele KW 43/2026)", Rating(447, 66), Rating(519, 86), 72),
        ),
    )

    @Test
    fun aRecordedExportReadsAsTheRatingUpdatesOldestFirst() = runTest {
        assertEquals(recorded, histories.getRatingHistory("163468"))
    }

    @Test
    fun aHeaderWithoutRowsIsAnEmptyHistory() = runTest {
        ratingsCentral.body = RatingsCentralSamples.HISTORY_163468.lines().first() + "\n"

        assertEquals(RatingHistory(emptyList()), histories.getRatingHistory("163468"))
    }

    @Test
    fun extraColumnsAreIgnoredAndColumnsAreReadByName() = runTest {
        ratingsCentral.body = "FinalStDev,FinalMean,Extra,EventDate,EventName,InitialMean,InitialStDev,PointChange\n" +
            "86,519,x,2026-10-25,\"Name, with comma \"\"quoted\"\"\",447,66,72\n"

        assertEquals(
            RatingHistory(listOf(RatingUpdate(LocalDate(2026, 10, 25), "Name, with comma \"quoted\"", Rating(447, 66), Rating(519, 86), 72))),
            histories.getRatingHistory("163468"),
        )
    }

    @Test
    fun anHtmlPageInsteadOfTheExportIsUnavailable() = runTest {
        ratingsCentral.body = "<!DOCTYPE html><html><body>Player History</body></html>"

        assertFailsWith<DataUnavailable> { histories.getRatingHistory("163468") }
    }

    @Test
    fun anExportMissingAColumnIsUnavailable() = runTest {
        ratingsCentral.body = "EventID,EventDate,EventName,InitialMean,InitialStDev,PointChange,FinalMean\n1,2026-10-25,\"x\",447,66,72,519\n"

        assertFailsWith<DataUnavailable> { histories.getRatingHistory("163468") }
    }

    @Test
    fun anUnreachableRatingsCentralIsUnavailable() = runTest {
        ratingsCentral.failing = true

        assertFailsWith<DataUnavailable> { histories.getRatingHistory("163468") }
    }

    @Test
    fun aHistoryIsHeldForADay() = runTest {
        histories.getRatingHistory("163468")
        timeSource += 23.hours
        histories.getRatingHistory("163468")

        assertEquals(1, ratingsCentral.requests.size)

        timeSource += 2.hours
        histories.getRatingHistory("163468")

        assertEquals(2, ratingsCentral.requests.size)
    }

    @Test
    fun aFailedRefreshServesTheHeldHistory() = runTest {
        histories.getRatingHistory("163468")
        timeSource += 25.hours
        ratingsCentral.failing = true

        assertEquals(recorded, histories.getRatingHistory("163468"))
    }

    @Test
    fun identicalConcurrentRequestsShareOneCall() = runTest {
        List(3) { async { histories.getRatingHistory("163468") } }.awaitAll()

        assertEquals(1, ratingsCentral.requests.size)
    }

    @Test
    fun theExportIsAskedForByRatingsCentralId() = runTest {
        histories.getRatingHistory("163468")

        assertEquals(listOf(RatingsCentralSamples.HISTORY_URL_163468), ratingsCentral.requests)
    }
}
