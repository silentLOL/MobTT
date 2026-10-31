package at.stefanirndorfer.batmate.domain.user

import at.stefanirndorfer.batmate.domain.club.AssociationRepository
import at.stefanirndorfer.batmate.domain.club.ClubRepository
import at.stefanirndorfer.batmate.domain.club.DataUnavailable
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
        return PlayerDetail(
            player = player,
            clubName = club?.name,
            associationName = club?.let { associationRepository.getAssociation(it.associationId)?.name },
            teams = teamsOf(player),
        )
    }

    // XTTV has no Player-to-Team lookup, so the Club's Teams are asked for their lineup. The Team section is a
    // bonus: a Team XTTV cannot answer for is left out instead of failing the whole Player.
    private suspend fun teamsOf(player: Player): List<Team> {
        val teams = try {
            teamRepository.getTeams(player.clubId)
        } catch (e: DataUnavailable) {
            return emptyList()
        }
        return teams.filter { team ->
            try {
                teamRepository.getLineup(team.id).any { it.playerId == player.id }
            } catch (e: DataUnavailable) {
                false
            }
        }
    }
}
