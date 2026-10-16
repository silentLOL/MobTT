package at.stefanirndorfer.batmate.domain.user

import at.stefanirndorfer.batmate.domain.club.AssociationRepository
import at.stefanirndorfer.batmate.domain.club.ClubRepository
import at.stefanirndorfer.batmate.domain.player.PlayerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Emits [UserContext.None] when nothing is chosen or the chosen Player or Club
 * can no longer be resolved, so the user is asked to choose again.
 */
class ObserveUserContextUseCase(
    private val selectionRepository: UserSelectionRepository,
    private val playerRepository: PlayerRepository,
    private val clubRepository: ClubRepository,
    private val associationRepository: AssociationRepository,
) {
    operator fun invoke(): Flow<UserContext> = selectionRepository.observeSelection().map { selection ->
        when (selection) {
            UserSelection.None -> UserContext.None
            is UserSelection.MyPlayer -> resolvePlayer(selection.playerId)
            is UserSelection.MyClub -> resolveClub(selection.clubId)
        }
    }

    private suspend fun resolvePlayer(playerId: String): UserContext {
        val player = playerRepository.getPlayer(playerId) ?: return UserContext.None
        val club = clubRepository.getClub(player.clubId) ?: return UserContext.None
        val association = associationRepository.getAssociation(club.associationId) ?: return UserContext.None
        return UserContext.MyPlayer(player, club, association)
    }

    private suspend fun resolveClub(clubId: String): UserContext {
        val club = clubRepository.getClub(clubId) ?: return UserContext.None
        val association = associationRepository.getAssociation(club.associationId) ?: return UserContext.None
        return UserContext.MyClub(club, association)
    }
}
