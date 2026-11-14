package at.stefanirndorfer.mobtt.domain.rating

import at.stefanirndorfer.mobtt.domain.club.DataUnavailable

class GetRatingHistoryUseCase(
    private val ratingHistoryRepository: RatingHistoryRepository,
    private val availability: RatingHistoryAvailability,
    private val explanation: RatingHistoryExplanation,
) {
    /** False in builds that may not show Rating histories; the screen then never asks. */
    val isEnabled: Boolean
        get() = availability.enabled

    /** Ratings Central's own explanation of the Rating, for the app's explanation to link to. */
    val explanationUrl: String
        get() = explanation.url

    /** Throws [DataUnavailable] when Ratings Central cannot answer and nothing is held. */
    suspend operator fun invoke(ratingsCentralId: String): RatingHistory = ratingHistoryRepository.getRatingHistory(ratingsCentralId)
}
