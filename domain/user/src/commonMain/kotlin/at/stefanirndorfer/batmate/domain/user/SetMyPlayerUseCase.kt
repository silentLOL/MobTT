package at.stefanirndorfer.batmate.domain.user

class SetMyPlayerUseCase(private val repository: UserSelectionRepository) {
    suspend operator fun invoke(playerId: String) {
        repository.saveSelection(UserSelection.MyPlayer(playerId))
    }
}
