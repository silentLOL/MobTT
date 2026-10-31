package at.stefanirndorfer.mobtt.core.fakedirectory

import at.stefanirndorfer.mobtt.domain.club.Association
import at.stefanirndorfer.mobtt.domain.club.AssociationRepository
import at.stefanirndorfer.mobtt.domain.club.Club
import at.stefanirndorfer.mobtt.domain.club.ClubRepository
import at.stefanirndorfer.mobtt.domain.club.Fixture
import at.stefanirndorfer.mobtt.domain.club.FixtureRepository
import at.stefanirndorfer.mobtt.domain.club.FixtureScore
import at.stefanirndorfer.mobtt.domain.club.FixtureStatus
import at.stefanirndorfer.mobtt.domain.club.League
import at.stefanirndorfer.mobtt.domain.club.LeagueRepository
import at.stefanirndorfer.mobtt.domain.club.LineupPlayer
import at.stefanirndorfer.mobtt.domain.club.Standing
import at.stefanirndorfer.mobtt.domain.club.Team
import at.stefanirndorfer.mobtt.domain.club.TeamRepository
import at.stefanirndorfer.mobtt.domain.player.Player
import at.stefanirndorfer.mobtt.domain.player.PlayerRepository
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

// TODO: replace with the XTTV-backed Player, Club and Association repositories once the API is documented.
class FakeDirectory(private val clock: Clock = Clock.System) :
    PlayerRepository,
    ClubRepository,
    AssociationRepository,
    TeamRepository,
    LeagueRepository,
    FixtureRepository {
    private val associations = listOf(
        Association("wttv", "Wiener Tischtennisverband", "WTTV"),
        Association("noettv", "Niederösterreichischer Tischtennisverband", "NÖTTV"),
        Association("stttv", "Steirischer Tischtennisverband", "STTTV"),
    )

    private val clubs = listOf(
        Club("olympic", "TTC Olympic", "wttv"),
        Club("voest", "SK VÖEST Wien", "wttv"),
        Club("nussdorf", "Nussdorf TTC", "wttv"),
        Club("stpoelten", "Union Tischtennisclub St. Pölten Nord-West", "noettv"),
        Club("graz", "TTV Graz Liebenau", "stttv"),
    )

    private val players = listOf(
        Player("huber", "Michael Huber", "olympic"),
        Player("gruber", "Anna Gruber", "olympic"),
        Player("bauer", "Lukas Bauer", "voest"),
        Player("wagner", "Sophie Wagner", "nussdorf"),
        Player("steiner", "Jakob Steiner", "stpoelten"),
        Player("pichler", "Katharina Habsburg-Lothringen-Esterházy", "stpoelten"),
        Player("moser", "Felix Moser", "graz"),
        Player("leitner", "Laura Leitner", "graz"),
    )

    private val leagues = listOf(
        League("landesliga1", "1. Landesliga Herren"),
        League("landesliga2", "2. Landesliga Herren"),
        League("gebietsliga", "Gebietsliga Nord"),
        League("oberliga", "Oberliga Steiermark"),
    )

    private val teams = listOf(
        Team("olympic-1", "TTC Olympic 1", "olympic", "landesliga1"),
        Team("olympic-2", "TTC Olympic 2", "olympic", "landesliga2"),
        Team("olympic-3", "TTC Olympic 3", "olympic", "landesliga2"),
        Team("voest-1", "SK VÖEST Wien 1", "voest", "landesliga1"),
        Team("nussdorf-1", "Nussdorf TTC 1", "nussdorf", "landesliga1"),
        Team("nussdorf-2", "Nussdorf TTC 2", "nussdorf", "landesliga2"),
        Team("stpoelten-1", "Union St. Pölten Nord-West 1", "stpoelten", "gebietsliga"),
        Team("stpoelten-2", "Union St. Pölten Nord-West 2", "stpoelten", "gebietsliga"),
        Team("graz-1", "TTV Graz Liebenau 1", "graz", "oberliga"),
        Team("graz-2", "TTV Graz Liebenau 2", "graz", "oberliga"),
    )

    private val standings = mapOf(
        "landesliga1" to listOf(
            Standing(1, "voest-1", "SK VÖEST Wien 1", played = 6, points = 11),
            Standing(2, "olympic-1", "TTC Olympic 1", played = 6, points = 9),
            Standing(3, "nussdorf-1", "Nussdorf TTC 1", played = 6, points = 4),
        ),
        "landesliga2" to listOf(
            Standing(1, "nussdorf-2", "Nussdorf TTC 2", played = 5, points = 8),
            Standing(2, "olympic-2", "TTC Olympic 2", played = 5, points = 6),
            Standing(3, "olympic-3", "TTC Olympic 3", played = 5, points = 3),
        ),
        "gebietsliga" to listOf(
            Standing(1, "stpoelten-1", "Union St. Pölten Nord-West 1", played = 4, points = 8),
            Standing(2, "stpoelten-2", "Union St. Pölten Nord-West 2", played = 4, points = 2),
        ),
        "oberliga" to listOf(
            Standing(1, "graz-1", "TTV Graz Liebenau 1", played = 3, points = 6),
            Standing(2, "graz-2", "TTV Graz Liebenau 2", played = 3, points = 3),
        ),
    )

    // PROVISIONAL shape, see TeamDetail. Olympic 1 mixes Players with and without a profile; most Teams have none listed.
    private val lineups = mapOf(
        "olympic-1" to listOf(
            LineupPlayer("huber", "Michael Huber"),
            LineupPlayer("gruber", "Anna Gruber"),
            LineupPlayer("olympic-1-guest", "Thomas Eder"),
        ),
        "stpoelten-1" to listOf(
            LineupPlayer("steiner", "Jakob Steiner"),
            LineupPlayer("pichler", "Katharina Habsburg-Lothringen-Esterházy"),
        ),
        "graz-1" to listOf(LineupPlayer("moser", "Felix Moser"), LineupPlayer("leitner", "Laura Leitner")),
    )

    override suspend fun getPlayer(id: String): Player? = players.firstOrNull { it.id == id }

    override suspend fun getLeaguePlayers(leagueId: String): List<Player> {
        val clubIds = teams.filter { it.leagueId == leagueId }.map { it.clubId }.toSet()
        return players.filter { it.clubId in clubIds }.sortedBy { it.name }
    }

    override suspend fun getClub(id: String): Club? = clubs.firstOrNull { it.id == id }

    override suspend fun searchClubs(query: String): List<Club> = clubs.filter { it.name.contains(query, ignoreCase = true) }

    override suspend fun getAssociation(id: String): Association? = associations.firstOrNull { it.id == id }

    override suspend fun getAssociations(): List<Association> = associations

    override suspend fun getTeam(id: String): Team? = teams.firstOrNull { it.id == id }

    override suspend fun getTeams(clubId: String): List<Team> = teams.filter { it.clubId == clubId }

    override suspend fun getLineup(teamId: String): List<LineupPlayer> = lineups[teamId].orEmpty()

    override suspend fun getLeague(id: String): League? = leagues.firstOrNull { it.id == id }

    override suspend fun getLeagues(associationId: String): List<League> {
        val leagueIds = teams.filter { team -> clubs.firstOrNull { it.id == team.clubId }?.associationId == associationId }.map { it.leagueId }.toSet()
        return leagues.filter { it.id in leagueIds }
    }

    override suspend fun getStandings(leagueId: String): List<Standing> = standings[leagueId].orEmpty()

    override suspend fun getFixtures(teamId: String): List<Fixture> = fixtures().filter {
        it.homeTeamId == teamId || it.awayTeamId == teamId
    }

    override suspend fun getLeagueFixtures(leagueId: String): List<Fixture> {
        val teamIds = teams.filter { it.leagueId == leagueId }.map { it.id }.toSet()
        return fixtures().filter { it.homeTeamId in teamIds }.sortedBy { it.startsAt }
    }

    // Dates are relative to now so the demo never runs out of upcoming Fixtures. TTC Olympic 3 only has a finished one: a free week.
    private fun fixtures(): List<Fixture> = listOf(
        fixture("f1", "olympic-1", "voest-1", inDays = 3, venue = "Sporthalle Olympic"),
        fixture("f2", "olympic-1", "nussdorf-1", inDays = 10, venue = "Sporthalle Olympic"),
        fixture("f3", "nussdorf-1", "olympic-1", inDays = -7, score = FixtureScore(4, 6)),
        fixture("f4", "nussdorf-1", "voest-1", inDays = 6, venue = "Nussdorfer Halle"),
        fixture("f5", "nussdorf-2", "olympic-2", inDays = 5, venue = "Nussdorfer Halle"),
        fixture("f6", "olympic-3", "nussdorf-2", inDays = -7, score = FixtureScore(2, 6)),
        fixture("f7", "stpoelten-2", "stpoelten-1", inDays = 2, venue = null),
        fixture("f8", "graz-2", "graz-1", inDays = 4, venue = "Halle Liebenau"),
    )

    private fun fixture(
        id: String,
        homeTeamId: String,
        awayTeamId: String,
        inDays: Int,
        venue: String? = null,
        score: FixtureScore? = null,
    ): Fixture {
        val startsIn: Duration = inDays.days
        return Fixture(
            id = id,
            homeTeamId = homeTeamId,
            homeTeamName = teams.first { it.id == homeTeamId }.name,
            awayTeamId = awayTeamId,
            awayTeamName = teams.first { it.id == awayTeamId }.name,
            startsAt = clock.now() + startsIn,
            venue = venue,
            status = if (score == null) FixtureStatus.Scheduled else FixtureStatus.Finished(score),
        )
    }
}
