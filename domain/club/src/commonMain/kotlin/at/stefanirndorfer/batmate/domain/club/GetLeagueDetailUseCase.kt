package at.stefanirndorfer.batmate.domain.club

class GetLeagueDetailUseCase(
    private val leagueRepository: LeagueRepository,
    private val fixtureRepository: FixtureRepository,
) {
    /** Null when the League cannot be resolved. */
    suspend operator fun invoke(leagueId: String): LeagueDetail? {
        val league = leagueRepository.getLeague(leagueId) ?: return null
        return LeagueDetail(
            league = league,
            standings = leagueRepository.getStandings(leagueId).sortedBy { it.position },
            fixtures = fixtureRepository.getLeagueFixtures(leagueId).sortedBy { it.startsAt },
        )
    }
}
