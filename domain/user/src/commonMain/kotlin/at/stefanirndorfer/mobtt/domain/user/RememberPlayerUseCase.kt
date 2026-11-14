package at.stefanirndorfer.mobtt.domain.user

import at.stefanirndorfer.mobtt.domain.player.Player
import at.stefanirndorfer.mobtt.domain.player.PlayerRepository

/** XTTV cannot look a Player up by id, so a Player met in a lineup is remembered before their screen opens. */
class RememberPlayerUseCase(private val playerRepository: PlayerRepository) {
    suspend operator fun invoke(player: Player) = playerRepository.remember(player)
}
