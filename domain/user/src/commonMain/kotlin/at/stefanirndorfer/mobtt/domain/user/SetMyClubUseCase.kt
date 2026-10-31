package at.stefanirndorfer.mobtt.domain.user

class SetMyClubUseCase(private val repository: UserSelectionRepository) {
    suspend operator fun invoke(clubId: String) {
        repository.saveSelection(UserSelection.MyClub(clubId))
    }
}
