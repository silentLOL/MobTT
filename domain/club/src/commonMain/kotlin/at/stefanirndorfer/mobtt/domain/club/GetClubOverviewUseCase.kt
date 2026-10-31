package at.stefanirndorfer.mobtt.domain.club

import kotlin.time.Clock

class GetClubOverviewUseCase(
    private val clubRepository: ClubRepository,
    private val associationRepository: AssociationRepository,
    private val teamRepository: TeamRepository,
    private val leagueRepository: LeagueRepository,
    private val fixtureRepository: FixtureRepository,
    private val clock: Clock = Clock.System,
) {
    /** Null when the Club or its Association cannot be resolved. */
    suspend operator fun invoke(clubId: String): ClubOverview? {
        val club = clubRepository.getClub(clubId) ?: return null
        val association = associationRepository.getAssociation(club.associationId) ?: return null
        val clubTeams = teamRepository.getTeams(clubId)
        val teams = clubTeams.map { toOverview(it) }
        val players = clubTeams
            .flatMap { teamRepository.getLineup(it.id) }
            .distinctBy { it.playerId }
            .sortedWith(compareByDescending<LineupPlayer> { it.rcPoints ?: Int.MIN_VALUE }.thenBy { it.name.lowercase() })
        return ClubOverview(club, association, teams, players)
    }

    private suspend fun toOverview(team: Team): TeamOverview = TeamOverview(
        team = team,
        league = leagueRepository.getLeague(team.leagueId),
        standing = leagueRepository.getStandings(team.leagueId).firstOrNull { it.teamId == team.id },
        nextFixture = nextFixture(team),
    )

    private suspend fun nextFixture(team: Team): NextFixture = nextFixtureOf(team, fixtureRepository.getFixtures(team.id), clock.now())
}
