package at.stefanirndorfer.batmate.domain.user

import at.stefanirndorfer.batmate.domain.club.ClubRepository
import at.stefanirndorfer.batmate.domain.player.Player
import at.stefanirndorfer.batmate.domain.player.PlayerRepository

/** [clubName] is null when the Player's Club can no longer be resolved. */
data class PlayerSearchResult(val player: Player, val clubName: String?)

class SearchPlayersUseCase(
    private val playerRepository: PlayerRepository,
    private val clubRepository: ClubRepository,
) {
    suspend operator fun invoke(query: String): List<PlayerSearchResult> = playerRepository.searchPlayers(query.trim()).map { player ->
        PlayerSearchResult(player, clubRepository.getClub(player.clubId)?.name)
    }
}
