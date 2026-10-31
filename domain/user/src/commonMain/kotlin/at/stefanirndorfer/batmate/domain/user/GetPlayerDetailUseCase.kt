package at.stefanirndorfer.batmate.domain.user

import at.stefanirndorfer.batmate.domain.club.AssociationRepository
import at.stefanirndorfer.batmate.domain.club.ClubRepository
import at.stefanirndorfer.batmate.domain.club.DataUnavailable
import at.stefanirndorfer.batmate.domain.club.LineupPlayer
import at.stefanirndorfer.batmate.domain.club.Team
import at.stefanirndorfer.batmate.domain.club.TeamRepository
import at.stefanirndorfer.batmate.domain.player.Player
import at.stefanirndorfer.batmate.domain.player.PlayerRepository

class GetPlayerDetailUseCase(
    private val playerRepository: PlayerRepository,
    private val clubRepository: ClubRepository,
    private val associationRepository: AssociationRepository,
    private val teamRepository: TeamRepository,
) {
    /** Null when the Player cannot be resolved, which includes a Player the user never picked. */
    suspend operator fun invoke(playerId: String): PlayerDetail? {
        val player = playerRepository.getPlayer(playerId) ?: return null
        val club = clubRepository.getClub(player.clubId)
        val lineup = lineupEntries(player)
        return PlayerDetail(
            player = player,
            clubName = club?.name,
            associationName = club?.let { associationRepository.getAssociation(it.associationId)?.name },
            teams = lineup.map { it.first },
            rcPoints = lineup.firstNotNullOfOrNull { it.second.rcPoints },
            ageGroup = lineup.firstNotNullOfOrNull { it.second.ageGroup },
        )
    }

    // XTTV has no Player-to-Team lookup, so the Club's Teams are asked for their lineup. The Team section is a
    // bonus: a Team XTTV cannot answer for is left out instead of failing the whole Player. The Player's RC and age
    // group ride on the same lineup entries.
    private suspend fun lineupEntries(player: Player): List<Pair<Team, LineupPlayer>> {
        val teams = try {
            teamRepository.getTeams(player.clubId)
        } catch (e: DataUnavailable) {
            return emptyList()
        }
        return teams.mapNotNull { team ->
            try {
                teamRepository.getLineup(team.id).firstOrNull { it.playerId == player.id }?.let { team to it }
            } catch (e: DataUnavailable) {
                null
            }
        }
    }
}
