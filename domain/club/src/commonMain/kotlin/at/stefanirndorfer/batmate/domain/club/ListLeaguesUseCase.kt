package at.stefanirndorfer.batmate.domain.club

class ListLeaguesUseCase(private val leagueRepository: LeagueRepository) {
    suspend operator fun invoke(associationId: String): List<League> = leagueRepository.getLeagues(associationId)
}
