package at.stefanirndorfer.mobtt.domain.user

import at.stefanirndorfer.mobtt.domain.player.PlayerRepository

class SetMyPlayerUseCase(
    private val repository: UserSelectionRepository,
    private val playerRepository: PlayerRepository,
) {
    suspend operator fun invoke(playerId: String) {
        playerRepository.keepPicked(playerId)
        repository.saveSelection(UserSelection.MyPlayer(playerId))
    }
}
