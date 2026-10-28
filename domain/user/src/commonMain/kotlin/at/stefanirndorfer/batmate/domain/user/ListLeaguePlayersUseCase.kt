package at.stefanirndorfer.batmate.domain.user

import at.stefanirndorfer.batmate.domain.club.ClubRepository
import at.stefanirndorfer.batmate.domain.player.Player
import at.stefanirndorfer.batmate.domain.player.PlayerRepository

/** [clubName] is null when the Player's Club can no longer be resolved. */
data class PlayerSearchResult(val player: Player, val clubName: String?)

class ListLeaguePlayersUseCase(
    private val playerRepository: PlayerRepository,
    private val clubRepository: ClubRepository,
) {
    suspend operator fun invoke(leagueId: String): List<PlayerSearchResult> {
        val players = playerRepository.getLeaguePlayers(leagueId)
        val clubNames = players.map { it.clubId }.distinct().associateWith { clubRepository.getClub(it)?.name }
        return players.map { PlayerSearchResult(it, clubNames[it.clubId]) }
    }
}
