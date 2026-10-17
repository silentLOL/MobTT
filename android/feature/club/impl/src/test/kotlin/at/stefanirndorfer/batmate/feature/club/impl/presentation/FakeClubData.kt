package at.stefanirndorfer.batmate.feature.club.impl.presentation

import at.stefanirndorfer.batmate.domain.club.Association
import at.stefanirndorfer.batmate.domain.club.AssociationRepository
import at.stefanirndorfer.batmate.domain.club.Club
import at.stefanirndorfer.batmate.domain.club.ClubRepository
import at.stefanirndorfer.batmate.domain.club.DataUnavailable
import at.stefanirndorfer.batmate.domain.club.Fixture
import at.stefanirndorfer.batmate.domain.club.FixtureRepository
import at.stefanirndorfer.batmate.domain.club.League
import at.stefanirndorfer.batmate.domain.club.LeagueRepository
import at.stefanirndorfer.batmate.domain.club.LineupPlayer
import at.stefanirndorfer.batmate.domain.club.Standing
import at.stefanirndorfer.batmate.domain.club.Team
import at.stefanirndorfer.batmate.domain.club.TeamRepository
import at.stefanirndorfer.batmate.domain.player.Player
import at.stefanirndorfer.batmate.domain.player.PlayerRepository
import at.stefanirndorfer.batmate.domain.user.UserSelection
import at.stefanirndorfer.batmate.domain.user.UserSelectionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/** XTTV stand-in: every read throws [DataUnavailable] while [unavailable] is set. */
class FakeClubData :
    ClubRepository,
    AssociationRepository,
    TeamRepository,
    LeagueRepository,
    FixtureRepository,
    PlayerRepository,
    UserSelectionRepository {
    var unavailable = false
    var teams = listOf(Team("t1", "Olympic 1", "c1", "l1"))

    private val club = Club("c1", "TTC Olympic", "a1")

    private fun <T> read(value: T): T {
        if (unavailable) {
            throw DataUnavailable("XTTV unreachable")
        }
        return value
    }

    override suspend fun getClub(id: String): Club? = read(club.takeIf { it.id == id })

    override suspend fun searchClubs(query: String): List<Club> = read(listOf(club))

    override suspend fun getAssociation(id: String): Association? = read(Association("a1", "WTTV"))

    override suspend fun getTeam(id: String): Team? = read(teams.firstOrNull { it.id == id })

    override suspend fun getTeams(clubId: String): List<Team> = read(teams)

    override suspend fun getLineup(teamId: String): List<LineupPlayer> = read(emptyList())

    override suspend fun getLeague(id: String): League? = read(League(id, "Liga"))

    override suspend fun getStandings(leagueId: String): List<Standing> = read(emptyList())

    override suspend fun getFixtures(teamId: String): List<Fixture> = read(emptyList())

    override suspend fun getPlayer(id: String): Player? = null

    override suspend fun searchPlayers(query: String): List<Player> = emptyList()

    override fun observeSelection(): Flow<UserSelection> = flowOf(UserSelection.None)

    override suspend fun saveSelection(selection: UserSelection) = Unit
}
