package at.stefanirndorfer.batmate.domain.user

import at.stefanirndorfer.batmate.domain.club.Association
import at.stefanirndorfer.batmate.domain.club.AssociationRepository
import at.stefanirndorfer.batmate.domain.club.Club
import at.stefanirndorfer.batmate.domain.club.ClubRepository
import at.stefanirndorfer.batmate.domain.club.Fixture
import at.stefanirndorfer.batmate.domain.club.FixtureRepository
import at.stefanirndorfer.batmate.domain.club.FixtureScore
import at.stefanirndorfer.batmate.domain.club.FixtureStatus
import at.stefanirndorfer.batmate.domain.club.GetClubOverviewUseCase
import at.stefanirndorfer.batmate.domain.club.League
import at.stefanirndorfer.batmate.domain.club.LeagueRepository
import at.stefanirndorfer.batmate.domain.club.LineupPlayer
import at.stefanirndorfer.batmate.domain.club.NextFixture
import at.stefanirndorfer.batmate.domain.club.Standing
import at.stefanirndorfer.batmate.domain.club.Team
import at.stefanirndorfer.batmate.domain.club.TeamRepository
import at.stefanirndorfer.batmate.domain.player.Player
import at.stefanirndorfer.batmate.domain.player.PlayerRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Instant

private val now = Instant.fromEpochSeconds(1_800_000_000)

private class FakeData :
    PlayerRepository,
    ClubRepository,
    AssociationRepository,
    TeamRepository,
    LeagueRepository,
    FixtureRepository {
    val club = Club("c1", "TTC Olympic", "a1")
    val teams = listOf(
        Team("t1", "Olympic 1", "c1", "l1"),
        Team("t2", "Olympic 2", "c1", "l1"),
        Team("t3", "Olympic 3", "c1", "l1"),
    )
    val rival = Team("r1", "Rival 1", "c2", "l1")
    val fixtures = mutableListOf<Fixture>()
    var failing = false

    override suspend fun getPlayer(id: String) = Player("p1", "Anna", "c1").takeIf { it.id == id }

    override suspend fun getLeaguePlayers(leagueId: String) = emptyList<Player>()

    override suspend fun getClub(id: String): Club? = club.takeIf { it.id == id }

    override suspend fun searchClubs(query: String) = listOf(club)

    override suspend fun getAssociation(id: String) = Association("a1", "WTTV")

    override suspend fun getAssociations() = listOf(Association("a1", "WTTV"))

    override suspend fun getTeam(id: String) = teams.firstOrNull { it.id == id }

    override suspend fun getLineup(teamId: String) = emptyList<LineupPlayer>()

    override suspend fun getTeams(clubId: String) = teams.filter { it.clubId == clubId }

    override suspend fun getLeague(id: String) = League(id, "Liga")

    override suspend fun getLeagues(associationId: String) = listOf(League("l1", "Liga"))

    override suspend fun getStandings(leagueId: String) = emptyList<Standing>()

    override suspend fun getLeagueFixtures(leagueId: String): List<Fixture> = emptyList()

    override suspend fun getFixtures(teamId: String): List<Fixture> {
        check(!failing) { "XTTV unreachable" }
        return fixtures.filter { it.homeTeamId == teamId || it.awayTeamId == teamId }
    }
}

class UpcomingFixturesTest {
    private val data = FakeData()
    private val selections = FakeSelectionRepository()
    private val observe = ObserveUpcomingFixturesUseCase(
        ObserveUserContextUseCase(selections, data, data, data),
        GetClubOverviewUseCase(
            data,
            data,
            data,
            data,
            data,
            object : Clock {
                override fun now(): Instant = now
            },
        ),
    )

    @Test
    fun nothingChosenShowsNoSelection() = runTest {
        assertEquals(UpcomingFixtures.NoSelection, observe().first())
    }

    @Test
    fun fixturesAreOrderedByDate() = runTest {
        selections.saveSelection(UserSelection.MyClub("c1"))
        data.fixtures += fixture("late", data.teams[0], startsIn = 9.days)
        data.fixtures += fixture("soon", data.teams[1], startsIn = 2.days)
        data.fixtures += fixture("middle", data.teams[2], startsIn = 5.days)

        assertEquals(listOf("soon", "middle", "late"), observe().first().loaded().map { it.fixtureId() })
    }

    @Test
    fun teamsWithAFreeWeekComeAfterScheduledFixtures() = runTest {
        selections.saveSelection(UserSelection.MyClub("c1"))
        data.fixtures += fixture("only", data.teams[2], startsIn = 4.days)

        val entries = observe().first().loaded()

        assertEquals(listOf("t3", "t1", "t2"), entries.map { it.team.id })
        assertEquals(NextFixture.FreeWeek, entries[1].next)
        assertEquals(NextFixture.FreeWeek, entries[2].next)
    }

    @Test
    fun finishedFixturesAreExcluded() = runTest {
        selections.saveSelection(UserSelection.MyClub("c1"))
        data.fixtures += fixture("done", data.teams[0], startsIn = (-3).days, score = FixtureScore(6, 4))

        assertEquals(NextFixture.FreeWeek, observe().first().loaded().first { it.team.id == "t1" }.next)
    }

    @Test
    fun myPlayerShowsTheTeamsOfTheirClub() = runTest {
        selections.saveSelection(UserSelection.MyPlayer("p1"))
        data.fixtures += fixture("f", data.teams[0], startsIn = 1.days)

        assertEquals(listOf("t1", "t2", "t3"), observe().first().loaded().map { it.team.id })
    }

    @Test
    fun unreachableDataIsUnavailableNotAnError() = runTest {
        selections.saveSelection(UserSelection.MyClub("c1"))
        data.failing = true

        assertEquals(UpcomingFixtures.Unavailable, observe().first())
    }

    private fun UpcomingFixtures.loaded() = assertIs<UpcomingFixtures.Loaded>(this).entries

    private fun UpcomingFixture.fixtureId() = assertIs<NextFixture.Scheduled>(next).fixture.id

    private fun fixture(id: String, home: Team, startsIn: Duration, score: FixtureScore? = null) = Fixture(
        id = id,
        homeTeamId = home.id,
        homeTeamName = home.name,
        awayTeamId = data.rival.id,
        awayTeamName = data.rival.name,
        startsAt = now + startsIn,
        venue = null,
        status = if (score == null) FixtureStatus.Scheduled else FixtureStatus.Finished(score),
    )
}
