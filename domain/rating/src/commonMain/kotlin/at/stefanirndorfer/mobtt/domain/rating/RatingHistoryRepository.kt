package at.stefanirndorfer.mobtt.domain.rating

import at.stefanirndorfer.mobtt.domain.club.DataUnavailable

interface RatingHistoryRepository {
    /** Throws [DataUnavailable] when Ratings Central cannot answer and no history is held. */
    suspend fun getRatingHistory(ratingsCentralId: String): RatingHistory
}
