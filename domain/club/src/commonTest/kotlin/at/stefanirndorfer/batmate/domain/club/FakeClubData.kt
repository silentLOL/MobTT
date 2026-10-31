package at.stefanirndorfer.batmate.domain.club

class FakeClubData :
    ClubRepository,
    AssociationRepository,
    TeamRepository,
    LeagueRepository,
    FixtureRepository {
    val clubs = mutableListOf<Club>()
    val associations = mutableListOf<Association>()
    val teams = mutableListOf<Team>()
    val leagues = mutableListOf<League>()
    val standings = mutableMapOf<String, List<Standing>>()
    val lineups = mutableMapOf<String, List<LineupPlayer>>()
    val fixtures = mutableListOf<Fixture>()

    override suspend fun getClub(id: String): Club? = clubs.firstOrNull { it.id == id }

    override suspend fun searchClubs(query: String): List<Club> = clubs.filter { it.name.contains(query, ignoreCase = true) }

    override suspend fun getAssociation(id: String): Association? = associations.firstOrNull { it.id == id }

    override suspend fun getAssociations(): List<Association> = associations

    override suspend fun getTeam(id: String): Team? = teams.firstOrNull { it.id == id }

    override suspend fun getLineup(teamId: String): List<LineupPlayer> = lineups[teamId].orEmpty()

    override suspend fun getTeams(clubId: String): List<Team> = teams.filter { it.clubId == clubId }

    override suspend fun getLeague(id: String): League? = leagues.firstOrNull { it.id == id }

    override suspend fun getLeagues(associationId: String): List<League> = leagues

    override suspend fun getStandings(leagueId: String): List<Standing> = standings[leagueId].orEmpty()

    override suspend fun getLeagueFixtures(leagueId: String): List<Fixture> {
        val teamIds = teams.filter { it.leagueId == leagueId }.map { it.id }.toSet()
        return fixtures.filter { it.homeTeamId in teamIds }
    }

    override suspend fun getFixtures(teamId: String): List<Fixture> = fixtures.filter {
        it.homeTeamId == teamId || it.awayTeamId == teamId
    }
}
