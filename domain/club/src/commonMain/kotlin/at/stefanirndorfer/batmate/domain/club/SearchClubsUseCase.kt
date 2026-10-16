package at.stefanirndorfer.batmate.domain.club

class SearchClubsUseCase(private val repository: ClubRepository) {
    suspend operator fun invoke(query: String): List<Club> = repository.searchClubs(query.trim())
}
