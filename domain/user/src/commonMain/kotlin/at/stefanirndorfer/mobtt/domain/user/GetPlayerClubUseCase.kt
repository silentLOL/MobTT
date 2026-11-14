package at.stefanirndorfer.mobtt.domain.user

import at.stefanirndorfer.mobtt.domain.club.AssociationRepository
import at.stefanirndorfer.mobtt.domain.club.ClubRepository
import at.stefanirndorfer.mobtt.domain.club.DataUnavailable
import at.stefanirndorfer.mobtt.domain.player.Player

/** The Player screen's Club section, read apart from the Teams so neither waits for the other. */
class GetPlayerClubUseCase(
    private val clubRepository: ClubRepository,
    private val associationRepository: AssociationRepository,
) {
    /**
     * Null when XTTV no longer resolves the Club. Throws [DataUnavailable] when XTTV cannot be reached; the Association
     * failing only leaves its code out.
     */
    suspend operator fun invoke(player: Player): PlayerClub? {
        val club = clubRepository.getClub(player.clubId) ?: return null
        val associationCode = try {
            associationRepository.getAssociation(club.associationId)?.code
        } catch (e: DataUnavailable) {
            null
        }
        return PlayerClub(club.name, associationCode)
    }
}
