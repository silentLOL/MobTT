package at.stefanirndorfer.mobtt.domain.user

import at.stefanirndorfer.mobtt.domain.club.Association
import at.stefanirndorfer.mobtt.domain.club.AssociationRepository
import at.stefanirndorfer.mobtt.domain.club.Club
import at.stefanirndorfer.mobtt.domain.club.ClubRepository
import at.stefanirndorfer.mobtt.domain.club.DataUnavailable
import at.stefanirndorfer.mobtt.domain.club.LineupPlayer
import at.stefanirndorfer.mobtt.domain.club.Team
import at.stefanirndorfer.mobtt.domain.club.TeamRepository
import at.stefanirndorfer.mobtt.domain.player.Player
import at.stefanirndorfer.mobtt.domain.player.PlayerRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

private class PlayerDirectory(
    val players: List<Player>,
    val clubs: List<Club>,
    val associations: List<Association>,
    val teams: List<Team> = emptyList(),
    val lineups: Map<String, List<LineupPlayer>> = emptyMap(),
    val failingLineups: Set<String> = emptySet(),
    val clubsFail: Boolean = false,
    val teamsFail: Boolean = false,
) : PlayerRepository,
    ClubRepository,
    AssociationRepository,
    TeamRepository {
    override suspend fun getPlayer(id: String) = players.firstOrNull { it.id == id }

    override suspend fun getLeaguePlayers(leagueId: String) = players

    override suspend fun getClub(id: String): Club? {
        if (clubsFail) {
            throw DataUnavailable("XTTV unreachable")
        }
        return clubs.firstOrNull { it.id == id }
    }

    override suspend fun searchClubs(query: String) = clubs

    override suspend fun getAssociation(id: String) = associations.firstOrNull { it.id == id }

    override suspend fun getAssociations() = associations

    override suspend fun getTeam(id: String) = teams.firstOrNull { it.id == id }

    override suspend fun getTeams(clubId: String): List<Team> {
        if (teamsFail) {
            throw DataUnavailable("XTTV unreachable")
        }
        return teams.filter { it.clubId == clubId }
    }

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
    private val wttv = Association("a1", "WTTV", "WTTV")
    private val first = Team("t1", "TTC Olympic 1", clubId = "c1", leagueId = "l1")
    private val second = Team("t2", "TTC Olympic 2", clubId = "c1", leagueId = "l2")
    private val third = Team("t3", "TTC Olympic 3", clubId = "c1", leagueId = "l2")
    private val inLineup = listOf(LineupPlayer("p1", "Anna"))

    private fun directory(
        clubs: List<Club> = listOf(olympic),
        associations: List<Association> = listOf(wttv),
        teams: List<Team> = emptyList(),
        lineups: Map<String, List<LineupPlayer>> = emptyMap(),
        failingLineups: Set<String> = emptySet(),
        clubsFail: Boolean = false,
        teamsFail: Boolean = false,
    ) = PlayerDirectory(listOf(anna), clubs, associations, teams, lineups, failingLineups, clubsFail, teamsFail)

    private fun getPlayer() = GetPlayerUseCase(directory())

    private fun getPlayerClub(directory: PlayerDirectory = directory()) = GetPlayerClubUseCase(directory, directory)

    private fun getPlayerTeams(directory: PlayerDirectory) = GetPlayerTeamsUseCase(directory)

    @Test
    fun knownPlayerIsResolved() = runTest {
        assertEquals(anna, getPlayer()("p1"))
    }

    @Test
    fun unresolvablePlayerIsNull() = runTest {
        assertNull(getPlayer()("gone"))
    }

    @Test
    fun playerClubCarriesTheClubNameAndAssociationCode() = runTest {
        assertEquals(PlayerClub("TTC Olympic", "WTTV"), getPlayerClub()(anna))
    }

    @Test
    fun playerWithoutResolvableClubHasNoPlayerClub() = runTest {
        assertNull(getPlayerClub(directory(clubs = emptyList()))(anna))
    }

    @Test
    fun playerWhoseAssociationIsGoneKeepsTheClub() = runTest {
        assertEquals(PlayerClub("TTC Olympic", null), getPlayerClub(directory(associations = emptyList()))(anna))
    }

    @Test
    fun unreachableClubFailsThePlayerClub() = runTest {
        assertFailsWith<DataUnavailable> { getPlayerClub(directory(clubsFail = true))(anna) }
    }

    @Test
    fun playerListsEveryTeamOfTheirClubWhoseLineupNamesThem() = runTest {
        val teams = getPlayerTeams(
            directory(
                teams = listOf(first, second, third),
                lineups = mapOf("t1" to inLineup, "t2" to inLineup, "t3" to listOf(LineupPlayer("p9", "Other"))),
            ),
        )(anna)

        assertEquals(PlayerTeams(listOf(first, second)), teams)
    }

    @Test
    fun playerTakesRcPointsAndAgeGroupFromTheirLineupEntry() = runTest {
        val teams = getPlayerTeams(
            directory(
                teams = listOf(first, second),
                lineups = mapOf(
                    "t1" to listOf(LineupPlayer("p1", "Anna", rcPoints = 1992, ageGroup = "U171")),
                    "t2" to listOf(LineupPlayer("p1", "Anna", rcPoints = 1992, ageGroup = "U171")),
                ),
            ),
        )(anna)

        assertEquals(1992, teams.rcPoints)
        assertEquals("U171", teams.ageGroup)
    }

    @Test
    fun teamWhoseLineupFailsIsLeftOut() = runTest {
        val teams = getPlayerTeams(
            directory(
                teams = listOf(first, second),
                lineups = mapOf("t1" to inLineup, "t2" to inLineup),
                failingLineups = setOf("t1"),
            ),
        )(anna)

        assertEquals(listOf(second), teams.teams)
    }

    @Test
    fun unreachableTeamListFailsThePlayerTeams() = runTest {
        assertFailsWith<DataUnavailable> { getPlayerTeams(directory(teamsFail = true))(anna) }
    }
}
