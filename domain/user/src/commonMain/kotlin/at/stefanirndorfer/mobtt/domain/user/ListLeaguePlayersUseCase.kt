package at.stefanirndorfer.mobtt.domain.user

import at.stefanirndorfer.mobtt.domain.club.ClubRepository
import at.stefanirndorfer.mobtt.domain.club.DataUnavailable
import at.stefanirndorfer.mobtt.domain.player.Player
import at.stefanirndorfer.mobtt.domain.player.PlayerRepository

/** [clubName] is null when the Player's Club can no longer be resolved. */
data class PlayerSearchResult(val player: Player, val clubName: String?)

class ListLeaguePlayersUseCase(
    private val playerRepository: PlayerRepository,
    private val clubRepository: ClubRepository,
) {
    suspend operator fun invoke(leagueId: String): List<PlayerSearchResult> {
        val players = playerRepository.getLeaguePlayers(leagueId)
        val clubNames = players.map { it.clubId }.distinct().associateWith { clubName(it) }
        return players.map { PlayerSearchResult(it, clubNames[it.clubId]) }
    }

    /** The name is only a subtitle, so a Club XTTV cannot answer for must not hide the League's Players. */
    private suspend fun clubName(clubId: String): String? = try {
        clubRepository.getClub(clubId)?.name
    } catch (e: DataUnavailable) {
        null
    }
}
