package at.stefanirndorfer.mobtt.domain.club

class GetLeagueDetailUseCase(
    private val leagueRepository: LeagueRepository,
) {
    /** Null when the League cannot be resolved. */
    suspend operator fun invoke(leagueId: String): LeagueDetail? {
        val league = leagueRepository.getLeague(leagueId) ?: return null
        return LeagueDetail(
            league = league,
            standings = leagueRepository.getStandings(leagueId).sortedBy { it.position },
        )
    }
}
