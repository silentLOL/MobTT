package at.stefanirndorfer.mobtt.feature.club.impl.presentation

import at.stefanirndorfer.mobtt.domain.club.Association
import at.stefanirndorfer.mobtt.domain.club.AssociationRepository
import at.stefanirndorfer.mobtt.domain.club.Club
import at.stefanirndorfer.mobtt.domain.club.ClubRepository
import at.stefanirndorfer.mobtt.domain.club.DataUnavailable
import at.stefanirndorfer.mobtt.domain.club.Fixture
import at.stefanirndorfer.mobtt.domain.club.FixtureDetail
import at.stefanirndorfer.mobtt.domain.club.FixtureRepository
import at.stefanirndorfer.mobtt.domain.club.League
import at.stefanirndorfer.mobtt.domain.club.LeagueRepository
import at.stefanirndorfer.mobtt.domain.club.LineupPlayer
import at.stefanirndorfer.mobtt.domain.club.Standing
import at.stefanirndorfer.mobtt.domain.club.Team
import at.stefanirndorfer.mobtt.domain.club.TeamRepository
import at.stefanirndorfer.mobtt.domain.player.Player
import at.stefanirndorfer.mobtt.domain.player.PlayerRepository
import at.stefanirndorfer.mobtt.domain.player.RankedPlayer
import at.stefanirndorfer.mobtt.domain.user.Favorite
import at.stefanirndorfer.mobtt.domain.user.FavoritesRepository
import at.stefanirndorfer.mobtt.domain.user.UserSelection
import at.stefanirndorfer.mobtt.domain.user.UserSelectionRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

/** XTTV stand-in: every read throws [DataUnavailable] while [unavailable] is set. */
class FakeClubData :
    ClubRepository,
    AssociationRepository,
    TeamRepository,
    LeagueRepository,
    FixtureRepository,
    PlayerRepository,
    UserSelectionRepository,
    FavoritesRepository {
    var unavailable = false
    val saved = mutableListOf<UserSelection>()
    var leagueFixtures = emptyList<Fixture>()
    var fixtureDetail: FixtureDetail? = null
    val favorites = MutableStateFlow<Set<Favorite>>(emptySet())
    var teams = listOf(Team("t1", "Olympic 1", "c1", "l1"))
    var standings = emptyList<Standing>()
    var lineups = emptyMap<String, List<LineupPlayer>>()
    var players = emptyList<Player>()
    var ranking = emptyList<RankedPlayer>()
    var rankingUnavailable = false

    /** While set, the ranking waits for it, so a test can watch the League before the ranking arrives. */
    var rankingGate: CompletableDeferred<Unit>? = null
    val selection = MutableStateFlow<UserSelection>(UserSelection.None)

    private val club = Club("c1", "TTC Olympic", "a1")

    private fun <T> read(value: T): T {
        if (unavailable) {
            throw DataUnavailable("XTTV unreachable")
        }
        return value
    }

    override suspend fun getClub(id: String): Club? = read(club.takeIf { it.id == id })

    override suspend fun searchClubs(query: String): List<Club> = read(listOf(club))

    override suspend fun getAssociation(id: String): Association? = read(Association("a1", "WTTV", "WTTV"))

    override suspend fun getAssociations(): List<Association> = read(listOf(Association("a1", "WTTV", "WTTV")))

    override suspend fun getTeam(id: String): Team? = read(teams.firstOrNull { it.id == id })

    override suspend fun getTeams(clubId: String): List<Team> = read(teams)

    override suspend fun getLineup(teamId: String): List<LineupPlayer> = read(lineups[teamId].orEmpty())

    override suspend fun getLeague(id: String): League? = read(League(id, "Liga"))

    override suspend fun getLeagues(associationId: String): List<League> = read(listOf(League("l1", "Liga")))

    override suspend fun getStandings(leagueId: String): List<Standing> = read(standings)

    override suspend fun getFixtures(teamId: String): List<Fixture> = read(emptyList())

    override suspend fun getLeagueFixtures(leagueId: String): List<Fixture> = read(leagueFixtures)

    override suspend fun getFixtureDetail(fixtureId: String): FixtureDetail? = read(fixtureDetail)

    override suspend fun getPlayer(id: String): Player? = players.firstOrNull { it.id == id }

    override suspend fun getLeaguePlayers(leagueId: String): List<Player> = emptyList()

    override suspend fun getPlayerRanking(leagueId: String): List<RankedPlayer> {
        rankingGate?.await()
        if (rankingUnavailable) {
            throw DataUnavailable("XTTV unreachable")
        }
        return read(ranking)
    }

    override fun observeSelection(): Flow<UserSelection> = selection

    override suspend fun saveSelection(selection: UserSelection) {
        saved += selection
    }

    override fun observeFavorites(): Flow<Set<Favorite>> = favorites

    override suspend fun add(favorite: Favorite) = favorites.update { it + favorite }

    override suspend fun remove(favorite: Favorite) = favorites.update { it - favorite }
}
