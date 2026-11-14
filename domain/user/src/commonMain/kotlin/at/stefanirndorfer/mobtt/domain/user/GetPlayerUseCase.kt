package at.stefanirndorfer.mobtt.domain.user

import at.stefanirndorfer.mobtt.domain.player.Player
import at.stefanirndorfer.mobtt.domain.player.PlayerRepository

/** The Player screen's first read: known from the picked Players, so the screen can draw before its sections load. */
class GetPlayerUseCase(
    private val playerRepository: PlayerRepository,
) {
    /** Null when the Player cannot be resolved, which includes a Player the user never picked. */
    suspend operator fun invoke(playerId: String): Player? = playerRepository.getPlayer(playerId)
}
