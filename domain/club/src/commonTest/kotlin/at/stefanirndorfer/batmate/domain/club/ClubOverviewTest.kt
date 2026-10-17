package at.stefanirndorfer.batmate.domain.club

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Instant

class ClubOverviewTest {
    private val now = Instant.fromEpochSeconds(1_800_000_000)
    private val directory = FakeClubData()
    private val getClubOverview = GetClubOverviewUseCase(
        clubRepository = directory,
        associationRepository = directory,
        teamRepository = directory,
        leagueRepository = directory,
        fixtureRepository = directory,
        clock = object : Clock {
            override fun now(): Instant = now
        },
    )

    private val olympic1 = Team("o1", "TTC Olympic 1", "olympic", "l1")
    private val rival = Team("r1", "Rival 1", "rival", "l1")

    @Test
    fun unknownClubIsNull() = runTest {
        assertNull(getClubOverview("missing"))
    }

    @Test
    fun clubWithoutTeamsHasNone() = runTest {
        directory.clubs += Club("olympic", "TTC Olympic", "wttv")
        directory.associations += Association("wttv", "WTTV")

        assertEquals(emptyList(), getClubOverview("olympic")?.teams)
    }

    @Test
    fun teamShowsItsLeagueAndStanding() = runTest {
        givenOlympic()
        directory.leagues += League("l1", "1. Landesliga Herren")
        directory.standings["l1"] = listOf(
            Standing(1, "r1", "Rival 1", played = 5, points = 9),
            Standing(2, "o1", "TTC Olympic 1", played = 5, points = 7),
        )

        val team = getClubOverview("olympic")!!.teams.single()

        assertEquals("1. Landesliga Herren", team.league?.name)
        assertEquals(2, team.standing?.position)
        assertEquals(7, team.standing?.points)
    }

    @Test
    fun leagueAndStandingAreNullWhenXttvHasNone() = runTest {
        givenOlympic()

        val team = getClubOverview("olympic")!!.teams.single()

        assertNull(team.league)
        assertNull(team.standing)
    }

    @Test
    fun nextFixtureIsTheEarliestUpcomingOne() = runTest {
        givenOlympic()
        directory.fixtures += fixture("late", home = olympic1, away = rival, startsIn = 10.days)
        directory.fixtures += fixture("soon", home = olympic1, away = rival, startsIn = 3.days)

        val next = getClubOverview("olympic")!!.teams.single().nextFixture

        assertEquals("soon", assertIs<NextFixture.Scheduled>(next).fixture.id)
    }

    @Test
    fun finishedFixturesAreSkipped() = runTest {
        givenOlympic()
        directory.fixtures += fixture("done", home = olympic1, away = rival, startsIn = (-7).days, score = FixtureScore(6, 4))
        directory.fixtures += fixture("upcoming", home = olympic1, away = rival, startsIn = 4.days)

        val next = getClubOverview("olympic")!!.teams.single().nextFixture

        assertEquals("upcoming", assertIs<NextFixture.Scheduled>(next).fixture.id)
    }

    @Test
    fun scheduledFixtureInThePastIsSkipped() = runTest {
        givenOlympic()
        directory.fixtures += fixture("stale", home = olympic1, away = rival, startsIn = (-1).days)

        assertEquals(NextFixture.FreeWeek, getClubOverview("olympic")!!.teams.single().nextFixture)
    }

    @Test
    fun teamWithNoFixtureHasAFreeWeek() = runTest {
        givenOlympic()

        assertEquals(NextFixture.FreeWeek, getClubOverview("olympic")!!.teams.single().nextFixture)
    }

    @Test
    fun teamWithOnlyFinishedFixturesHasAFreeWeek() = runTest {
        givenOlympic()
        directory.fixtures += fixture("done", home = olympic1, away = rival, startsIn = (-7).days, score = FixtureScore(2, 6))

        assertEquals(NextFixture.FreeWeek, getClubOverview("olympic")!!.teams.single().nextFixture)
    }

    @Test
    fun opponentIsTheOtherTeamWhetherHomeOrAway() = runTest {
        givenOlympic()
        directory.fixtures += fixture("home", home = olympic1, away = rival, startsIn = 2.days)
        directory.fixtures += fixture("away", home = rival, away = olympic1, startsIn = 9.days)

        val home = assertIs<NextFixture.Scheduled>(getClubOverview("olympic")!!.teams.single().nextFixture)
        assertEquals("Rival 1", home.opponentName)
        assertEquals(true, home.isHome)

        directory.fixtures.removeAll { it.id == "home" }
        val away = assertIs<NextFixture.Scheduled>(getClubOverview("olympic")!!.teams.single().nextFixture)
        assertEquals("Rival 1", away.opponentName)
        assertEquals(false, away.isHome)
    }

    @Test
    fun venueIsCarriedThroughWhenKnownAndNullWhenNot() = runTest {
        givenOlympic()
        directory.fixtures += fixture("a", home = olympic1, away = rival, startsIn = 2.days, venue = "Sporthalle")

        val known = assertIs<NextFixture.Scheduled>(getClubOverview("olympic")!!.teams.single().nextFixture)
        assertEquals("Sporthalle", known.fixture.venue)

        directory.fixtures.clear()
        directory.fixtures += fixture("b", home = olympic1, away = rival, startsIn = 2.days, venue = null)
        val unknown = assertIs<NextFixture.Scheduled>(getClubOverview("olympic")!!.teams.single().nextFixture)
        assertNull(unknown.fixture.venue)
    }

    private fun givenOlympic() {
        directory.clubs += Club("olympic", "TTC Olympic", "wttv")
        directory.associations += Association("wttv", "WTTV")
        directory.teams += olympic1
    }

    private fun fixture(
        id: String,
        home: Team,
        away: Team,
        startsIn: kotlin.time.Duration,
        score: FixtureScore? = null,
        venue: String? = "Sporthalle",
    ) = Fixture(
        id = id,
        homeTeamId = home.id,
        homeTeamName = home.name,
        awayTeamId = away.id,
        awayTeamName = away.name,
        startsAt = now + startsIn,
        venue = venue,
        status = if (score == null) FixtureStatus.Scheduled else FixtureStatus.Finished(score),
    )
}
