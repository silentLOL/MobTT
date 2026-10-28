package at.stefanirndorfer.batmate.domain.user

import at.stefanirndorfer.batmate.domain.club.ClubRepository
import at.stefanirndorfer.batmate.domain.player.Player
import at.stefanirndorfer.batmate.domain.player.PlayerRepository

/** [clubName] is null when the Player's Club can no longer be resolved. */
data class PlayerSearchResult(val player: Player, val clubName: String?)

/** Player search reaches XTTV on every call, so shorter queries are not searched at all. */
const val MIN_PLAYER_QUERY_LENGTH = 3

class SearchPlayersUseCase(
    private val playerRepository: PlayerRepository,
    private val clubRepository: ClubRepository,
) {
    suspend operator fun invoke(query: String): List<PlayerSearchResult> {
        val trimmed = query.trim()
        if (trimmed.length < MIN_PLAYER_QUERY_LENGTH) {
            return emptyList()
        }
        return playerRepository.searchPlayers(trimmed).map { player ->
            PlayerSearchResult(player, clubRepository.getClub(player.clubId)?.name)
        }
    }
}
