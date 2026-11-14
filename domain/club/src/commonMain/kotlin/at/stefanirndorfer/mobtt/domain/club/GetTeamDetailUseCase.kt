package at.stefanirndorfer.mobtt.domain.club

import kotlin.time.Clock

class GetTeamDetailUseCase(
    private val clubRepository: ClubRepository,
    private val teamRepository: TeamRepository,
    private val leagueRepository: LeagueRepository,
    private val fixtureRepository: FixtureRepository,
    private val clock: Clock = Clock.System,
) {
    /** Null when the Team or its Club cannot be resolved. */
    suspend operator fun invoke(teamId: String): TeamDetail? {
        val team = teamRepository.getTeam(teamId) ?: return null
        val club = clubRepository.getClub(team.clubId) ?: return null
        return TeamDetail(
            team = team,
            clubName = club.name,
            league = leagueRepository.getLeague(team.leagueId),
            standing = leagueRepository.getStandings(team.leagueId).firstOrNull { it.teamId == teamId },
            nextFixture = nextFixtureOf(team, fixtureRepository.getFixtures(teamId), clock.now()),
            lineup = teamRepository.getLineup(teamId).sortedByDescending { it.rcPoints ?: Int.MIN_VALUE },
        )
    }
}
