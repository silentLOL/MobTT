package at.stefanirndorfer.mobtt.domain.rating

import kotlinx.datetime.LocalDate

/** A Player's estimated playing strength according to Ratings Central: [points] with a [deviation] (±). */
data class Rating(val points: Int, val deviation: Int)

/** One processing of a Player's results by Ratings Central, which moves their Rating from [before] to [after]. */
data class RatingUpdate(
    val date: LocalDate,
    val name: String,
    val before: Rating,
    val after: Rating,
    val pointChange: Int,
)

/** A Player's Rating updates, oldest first. Empty for a Player Ratings Central knows but has not rated yet. */
data class RatingHistory(val updates: List<RatingUpdate>)

/** Whether the app may show Rating histories at all; off in release builds until Ratings Central agrees (ADR-0010). */
data class RatingHistoryAvailability(val enabled: Boolean)
