package at.stefanirndorfer.mobtt.domain.user

import at.stefanirndorfer.mobtt.domain.club.Association
import at.stefanirndorfer.mobtt.domain.club.AssociationRepository
import at.stefanirndorfer.mobtt.domain.club.Club
import at.stefanirndorfer.mobtt.domain.club.ClubRepository
import at.stefanirndorfer.mobtt.domain.club.DataUnavailable
import at.stefanirndorfer.mobtt.domain.club.Fixture
import at.stefanirndorfer.mobtt.domain.club.FixtureDetail
import at.stefanirndorfer.mobtt.domain.club.FixtureRepository
import at.stefanirndorfer.mobtt.domain.club.GetTeamDetailUseCase
import at.stefanirndorfer.mobtt.domain.club.League
import at.stefanirndorfer.mobtt.domain.club.LeagueRepository
import at.stefanirndorfer.mobtt.domain.club.LineupPlayer
import at.stefanirndorfer.mobtt.domain.club.Standing
import at.stefanirndorfer.mobtt.domain.club.Team
import at.stefanirndorfer.mobtt.domain.club.TeamRepository
import at.stefanirndorfer.mobtt.domain.player.Player
import at.stefanirndorfer.mobtt.domain.player.PlayerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

private class MyTeamsDirectory(
    val players: List<Player>,
    val teams: List<Team>,
    val lineups: Map<String, List<LineupPlayer>>,
    val leagues: List<League>,
    val failingTeams: Set<String> = emptySet(),
    val teamsUnavailable: Boolean = false,
) : PlayerRepository,
    ClubRepository,
    AssociationRepository,
    TeamRepository,
    LeagueRepository,
    FixtureRepository,
    UserSelectionRepository {
    val selection = MutableStateFlow<UserSelection>(UserSelection.None)
    private val club = Club("c1", "TTC Olympic", "a1")

    override suspend fun getPlayer(id: String) = players.firstOrNull { it.id == id }

    override suspend fun getLeaguePlayers(leagueId: String) = players

    override suspend fun getClub(id: String) = club.takeIf { it.id == id }

    override suspend fun searchClubs(query: String) = listOf(club)

    override suspend fun getAssociation(id: String) = Association("a1", "WTTV", "WTTV")

    override suspend fun getAssociations() = listOf(Association("a1", "WTTV", "WTTV"))

    override suspend fun getTeam(id: String): Team? {
        if (id in failingTeams) {
            throw DataUnavailable("XTTV unreachable")
        }
        return teams.firstOrNull { it.id == id }
    }

    override suspend fun getTeams(clubId: String): List<Team> {
        if (teamsUnavailable) {
            throw DataUnavailable("XTTV unreachable")
        }
        return teams.filter { it.clubId == clubId }
    }

    override suspend fun getLineup(teamId: String) = lineups[teamId].orEmpty()

    override suspend fun getLeague(id: String) = leagues.firstOrNull { it.id == id }

    override suspend fun getLeagues(associationId: String) = leagues

    override suspend fun getStandings(leagueId: String) = emptyList<Standing>()

    override suspend fun getFixtures(teamId: String) = emptyList<Fixture>()

    override suspend fun getLeagueFixtures(leagueId: String) = emptyList<Fixture>()

    override suspend fun getFixtureDetail(fixtureId: String): FixtureDetail? = null

    override fun observeSelection() = selection

    override suspend fun saveSelection(selection: UserSelection) {
        this.selection.value = selection
    }
}

class MyTeamsTest {
    private val anna = Player("p1", "Anna", clubId = "c1")
    private val bea = Player("p2", "Bea", clubId = "c1")
    private val first = Team("t1", "TTC Olympic 1", clubId = "c1", leagueId = "l1")
    private val second = Team("t2", "TTC Olympic 2", clubId = "c1", leagueId = "l2")
    private val third = Team("t3", "TTC Olympic 3", clubId = "c1", leagueId = "l2")
    private val noLeague = Team("t4", "TTC Olympic Cup", clubId = "c1", leagueId = "gone")
    private val leagues = listOf(League("l1", "1. Landesliga"), League("l2", "2. Landesliga"))

    private fun directory(
        lineups: Map<String, List<LineupPlayer>>,
        teams: List<Team> = listOf(first, second, third, noLeague),
        failingTeams: Set<String> = emptySet(),
        teamsUnavailable: Boolean = false,
        selection: UserSelection = UserSelection.MyPlayer("p1"),
    ) = MyTeamsDirectory(listOf(anna, bea), teams, lineups, leagues, failingTeams, teamsUnavailable).also {
        it.selection.value = selection
    }

    private fun observeMyTeams(directory: MyTeamsDirectory) = ObserveMyTeamsUseCase(
        ObserveUserContextUseCase(directory, directory, directory, directory),
        directory,
        GetTeamDetailUseCase(directory, directory, directory, directory),
    )

    private fun lineup(vararg players: Player) = players.map { LineupPlayer(it.id, it.name) }

    private fun MyTeams.teamIds() = assertIs<MyTeams.Found>(this).teams.map { it.team.id }

    @Test
    fun withoutMyPlayerThereAreNoMyTeams() = runTest {
        val directory = directory(lineups = emptyMap(), selection = UserSelection.None)

        assertEquals(MyTeams.NoMyPlayer, observeMyTeams(directory)().first())
    }

    @Test
    fun myClubAloneGivesNoMyTeams() = runTest {
        val directory = directory(lineups = mapOf("t1" to lineup(anna)), selection = UserSelection.MyClub("c1"))

        assertEquals(MyTeams.NoMyPlayer, observeMyTeams(directory)().first())
    }

    @Test
    fun myTeamsAreTheTeamsWhoseLineupNamesMyPlayer() = runTest {
        val directory = directory(lineups = mapOf("t1" to lineup(anna, bea), "t2" to lineup(bea)))

        val myTeams = observeMyTeams(directory)().first()

        assertEquals(anna, assertIs<MyTeams.Found>(myTeams).player)
        assertEquals(listOf("t1"), myTeams.teamIds())
        assertEquals("1. Landesliga", myTeams.teams.single().league?.name)
    }

    @Test
    fun twoTeamsInTheSameLeagueAreBothMyTeams() = runTest {
        val directory = directory(lineups = mapOf("t1" to lineup(anna), "t2" to lineup(anna), "t3" to lineup(anna)))

        assertEquals(listOf("t1", "t2", "t3"), observeMyTeams(directory)().first().teamIds())
    }

    @Test
    fun noLineupNamingMyPlayerMeansNoTeamFound() = runTest {
        val directory = directory(lineups = mapOf("t1" to lineup(bea)))

        assertEquals(emptyList(), observeMyTeams(directory)().first().teamIds())
    }

    @Test
    fun aTeamWithoutLeagueIsLeftOut() = runTest {
        val directory = directory(lineups = mapOf("t1" to lineup(anna), "t4" to lineup(anna)))

        assertEquals(listOf("t1"), observeMyTeams(directory)().first().teamIds())
    }

    @Test
    fun aTeamXttvCannotAnswerForIsLeftOutAlone() = runTest {
        val directory = directory(lineups = mapOf("t1" to lineup(anna), "t2" to lineup(anna)), failingTeams = setOf("t1"))

        assertEquals(listOf("t2"), observeMyTeams(directory)().first().teamIds())
    }

    @Test
    fun theClubsTeamsBeingUnavailableMakesMyTeamsUnavailable() = runTest {
        val directory = directory(lineups = mapOf("t1" to lineup(anna)), teamsUnavailable = true)

        assertEquals(MyTeams.Unavailable, observeMyTeams(directory)().first())
    }

    @Test
    fun myTeamsKeepFollowingMyPlayerAfterXttvCouldNotAnswer() = runTest {
        val directory = directory(lineups = mapOf("t1" to lineup(anna)), teamsUnavailable = true)
        val seen = mutableListOf<MyTeams>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { observeMyTeams(directory)().toList(seen) }

        directory.selection.value = UserSelection.None

        assertEquals(listOf(MyTeams.Unavailable, MyTeams.NoMyPlayer), seen)
    }

    @Test
    fun myTeamsFollowAChangeOfMyPlayer() = runTest {
        val directory = directory(lineups = mapOf("t1" to lineup(anna), "t2" to lineup(bea)))
        val seen = mutableListOf<MyTeams>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { observeMyTeams(directory)().toList(seen) }

        directory.selection.value = UserSelection.MyPlayer("p2")

        assertEquals(listOf(listOf("t1"), listOf("t2")), seen.map { it.teamIds() })
    }
}
