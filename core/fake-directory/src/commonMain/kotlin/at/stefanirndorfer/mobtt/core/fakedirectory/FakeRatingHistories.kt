package at.stefanirndorfer.mobtt.core.fakedirectory

import at.stefanirndorfer.mobtt.domain.rating.Rating
import at.stefanirndorfer.mobtt.domain.rating.RatingHistory
import at.stefanirndorfer.mobtt.domain.rating.RatingHistoryRepository
import at.stefanirndorfer.mobtt.domain.rating.RatingUpdate
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.plus
import kotlin.math.abs
import kotlin.random.Random

/**
 * A made-up Rating history for any Ratings Central ID, the same every time for the same ID. One ID in seven has an empty
 * history and one in seven a single Rating update, so those states show up while browsing; the rest play weekly
 * through three seasons, starting from Ratings Central's unrated default.
 */
class FakeRatingHistories : RatingHistoryRepository {
    override suspend fun getRatingHistory(ratingsCentralId: String): RatingHistory {
        val seed = abs(ratingsCentralId.hashCode())
        return when (seed % 7) {
            0 -> RatingHistory(emptyList())
            1 -> RatingHistory(updates(Random(seed)).take(1))
            else -> RatingHistory(updates(Random(seed)))
        }
    }

    private fun updates(random: Random): List<RatingUpdate> {
        val result = mutableListOf<RatingUpdate>()
        var before = UNRATED
        var date = FIRST_UPDATE
        var after = Rating(points = random.nextInt(700, 1800), deviation = 160)
        while (date <= LAST_UPDATE) {
            if (date.month !in SUMMER_BREAK) {
                result += RatingUpdate(date, "Fake League ${date.year} (week ${result.size + 1})", before, after, after.points - before.points)
                before = after
                after = Rating(
                    points = (before.points + random.nextInt(-45, 50)).coerceAtLeast(100),
                    deviation = (before.deviation - random.nextInt(0, 8)).coerceAtLeast(55),
                )
            }
            date = date.plus(DatePeriod(days = 7))
        }
        return result
    }

    private companion object {
        val UNRATED = Rating(1450, 450)
        val FIRST_UPDATE = LocalDate(2023, 9, 17)
        val LAST_UPDATE = LocalDate(2026, 11, 1)
        val SUMMER_BREAK = setOf(Month.JUNE, Month.JULY, Month.AUGUST)
    }
}
