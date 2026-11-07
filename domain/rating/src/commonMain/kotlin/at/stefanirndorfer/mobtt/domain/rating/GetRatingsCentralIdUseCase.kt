package at.stefanirndorfer.mobtt.domain.rating

import at.stefanirndorfer.mobtt.domain.club.DataUnavailable
import at.stefanirndorfer.mobtt.domain.player.PlayerRepository

/** Finds a Player's Ratings Central entry through the Player ranking of their Leagues, which XTTV links to it. */
class GetRatingsCentralIdUseCase(
    private val playerRepository: PlayerRepository,
) {
    /**
     * The ID is the same in every League, so the Leagues are asked in order until one ranks the Player. Null when none
     * does or XTTV gives no ID. Throws [DataUnavailable] when a ranking cannot be read.
     */
    suspend operator fun invoke(playerId: String, leagueIds: List<String>): String? {
        for (leagueId in leagueIds.distinct()) {
            val row = playerRepository.getPlayerRanking(leagueId).firstOrNull { it.player.id == playerId } ?: continue
            return row.ratingsCentralId
        }
        return null
    }
}
