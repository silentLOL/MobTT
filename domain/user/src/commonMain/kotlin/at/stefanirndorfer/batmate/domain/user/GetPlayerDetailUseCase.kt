package at.stefanirndorfer.batmate.domain.user

import at.stefanirndorfer.batmate.domain.club.AssociationRepository
import at.stefanirndorfer.batmate.domain.club.ClubRepository
import at.stefanirndorfer.batmate.domain.player.PlayerRepository

class GetPlayerDetailUseCase(
    private val playerRepository: PlayerRepository,
    private val clubRepository: ClubRepository,
    private val associationRepository: AssociationRepository,
) {
    /** Null when the Player cannot be resolved, which includes a Player the user never picked. */
    suspend operator fun invoke(playerId: String): PlayerDetail? {
        val player = playerRepository.getPlayer(playerId) ?: return null
        val club = clubRepository.getClub(player.clubId)
        return PlayerDetail(
            player = player,
            clubName = club?.name,
            associationName = club?.let { associationRepository.getAssociation(it.associationId)?.name },
        )
    }
}
