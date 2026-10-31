package at.stefanirndorfer.batmate.domain.user

import at.stefanirndorfer.batmate.domain.club.Association
import at.stefanirndorfer.batmate.domain.club.AssociationRepository
import at.stefanirndorfer.batmate.domain.club.Club
import at.stefanirndorfer.batmate.domain.club.ClubRepository
import at.stefanirndorfer.batmate.domain.club.DataUnavailable
import at.stefanirndorfer.batmate.domain.club.LineupPlayer
import at.stefanirndorfer.batmate.domain.club.Team
import at.stefanirndorfer.batmate.domain.club.TeamRepository
import at.stefanirndorfer.batmate.domain.player.Player
import at.stefanirndorfer.batmate.domain.player.PlayerRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

private class PlayerDirectory(
    val players: List<Player>,
    val clubs: List<Club>,
    val associations: List<Association>,
    val teams: List<Team> = emptyList(),
    val lineups: Map<String, List<LineupPlayer>> = emptyMap(),
    val failingLineups: Set<String> = emptySet(),
) : PlayerRepository,
    ClubRepository,
    AssociationRepository,
    TeamRepository {
    override suspend fun getPlayer(id: String) = players.firstOrNull { it.id == id }

    override suspend fun getLeaguePlayers(leagueId: String) = players

    override suspend fun getClub(id: String) = clubs.firstOrNull { it.id == id }

    override suspend fun searchClubs(query: String) = clubs

    override suspend fun getAssociation(id: String) = associations.firstOrNull { it.id == id }

    override suspend fun getAssociations() = associations

    override suspend fun getTeam(id: String) = teams.firstOrNull { it.id == id }

    override suspend fun getTeams(clubId: String) = teams.filter { it.clubId == clubId }

    override suspend fun getLineup(teamId: String): List<LineupPlayer> {
        if (teamId in failingLineups) {
            throw DataUnavailable("XTTV unreachable")
        }
        return lineups[teamId].orEmpty()
    }
}

class PlayerDetailTest {
    private val anna = Player("p1", "Anna", clubId = "c1")
    private val olympic = Club("c1", "TTC Olympic", associationId = "a1")
    private val wttv = Association("a1", "WTTV")
    private val first = Team("t1", "TTC Olympic 1", clubId = "c1", leagueId = "l1")
    private val second = Team("t2", "TTC Olympic 2", clubId = "c1", leagueId = "l2")
    private val third = Team("t3", "TTC Olympic 3", clubId = "c1", leagueId = "l2")
    private val inLineup = listOf(LineupPlayer("p1", "Anna"))

    private fun getPlayerDetail(
        players: List<Player> = listOf(anna),
        clubs: List<Club> = listOf(olympic),
        associations: List<Association> = listOf(wttv),
        teams: List<Team> = emptyList(),
        lineups: Map<String, List<LineupPlayer>> = emptyMap(),
        failingLineups: Set<String> = emptySet(),
    ) = PlayerDirectory(players, clubs, associations, teams, lineups, failingLineups).let { GetPlayerDetailUseCase(it, it, it, it) }

    @Test
    fun playerShowsNameClubAndAssociation() = runTest {
        assertEquals(PlayerDetail(anna, "TTC Olympic", "WTTV"), getPlayerDetail()("p1"))
    }

    @Test
    fun unresolvablePlayerIsNull() = runTest {
        assertNull(getPlayerDetail()("gone"))
    }

    @Test
    fun playerWithoutResolvableClubKeepsTheirNameWithoutClubOrAssociation() = runTest {
        assertEquals(PlayerDetail(anna, null, null), getPlayerDetail(clubs = emptyList())("p1"))
    }

    @Test
    fun playerWhoseAssociationIsGoneKeepsTheClub() = runTest {
        assertEquals(PlayerDetail(anna, "TTC Olympic", null), getPlayerDetail(associations = emptyList())("p1"))
    }

    @Test
    fun playerListsEveryTeamOfTheirClubWhoseLineupNamesThem() = runTest {
        val detail = getPlayerDetail(
            teams = listOf(first, second, third),
            lineups = mapOf("t1" to inLineup, "t2" to inLineup, "t3" to listOf(LineupPlayer("p9", "Other"))),
        )("p1")

        assertEquals(listOf(first, second), detail?.teams)
    }

    @Test
    fun playerTakesRcPointsAndAgeGroupFromTheirLineupEntry() = runTest {
        val detail = getPlayerDetail(
            teams = listOf(first, second),
            lineups = mapOf(
                "t1" to listOf(LineupPlayer("p1", "Anna", rcPoints = 1992, ageGroup = "U171")),
                "t2" to listOf(LineupPlayer("p1", "Anna", rcPoints = 1992, ageGroup = "U171")),
            ),
        )("p1")

        assertEquals(1992, detail?.rcPoints)
        assertEquals("U171", detail?.ageGroup)
    }

    @Test
    fun teamWhoseLineupFailsIsLeftOutWithoutFailingThePlayer() = runTest {
        val detail = getPlayerDetail(
            teams = listOf(first, second),
            lineups = mapOf("t1" to inLineup, "t2" to inLineup),
            failingLineups = setOf("t1"),
        )("p1")

        assertEquals(listOf(second), detail?.teams)
        assertEquals("TTC Olympic", detail?.clubName)
    }
}
