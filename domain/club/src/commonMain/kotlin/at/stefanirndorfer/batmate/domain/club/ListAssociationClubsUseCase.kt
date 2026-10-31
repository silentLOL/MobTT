package at.stefanirndorfer.batmate.domain.club

class ListAssociationClubsUseCase(private val repository: ClubRepository) {
    suspend operator fun invoke(associationId: String): List<Club> = repository.getAssociationClubs(associationId).sortedBy { it.name.lowercase() }
}
