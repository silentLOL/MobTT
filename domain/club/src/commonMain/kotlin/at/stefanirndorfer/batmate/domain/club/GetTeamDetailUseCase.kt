package at.stefanirndorfer.batmate.domain.club

class GetTeamDetailUseCase(
    private val clubRepository: ClubRepository,
    private val teamRepository: TeamRepository,
    private val leagueRepository: LeagueRepository,
) {
    /** Null when the Team or its Club cannot be resolved. */
    suspend operator fun invoke(teamId: String): TeamDetail? {
        val team = teamRepository.getTeam(teamId) ?: return null
        val club = clubRepository.getClub(team.clubId) ?: return null
        return TeamDetail(
            team = team,
            clubName = club.name,
            league = leagueRepository.getLeague(team.leagueId),
            standings = leagueRepository.getStandings(team.leagueId).sortedBy { it.position },
            lineup = teamRepository.getLineup(teamId),
        )
    }
}
