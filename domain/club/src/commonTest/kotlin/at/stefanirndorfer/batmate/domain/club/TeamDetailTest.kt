package at.stefanirndorfer.batmate.domain.club

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Instant

class TeamDetailTest {
    private val directory = FakeClubData()

    private val now = Instant.fromEpochSeconds(1_800_000_000)
    private val clock = object : Clock {
        override fun now(): Instant = this@TeamDetailTest.now
    }

    private val getTeamDetail = GetTeamDetailUseCase(directory, directory, directory, directory, clock)

    private fun fixture(id: String, startsAt: Instant) = Fixture(
        id = id,
        homeTeamId = "o1",
        homeTeamName = "TTC Olympic 1",
        awayTeamId = "r1",
        awayTeamName = "Rival 1",
        startsAt = startsAt,
        venue = null,
        status = FixtureStatus.Scheduled,
    )

    private val olympic1 = Team("o1", "TTC Olympic 1", "olympic", "l1")

    private fun givenOlympic() {
        directory.clubs += Club("olympic", "TTC Olympic", "wttv")
        directory.teams += olympic1
        directory.leagues += League("l1", "1. Landesliga Herren")
    }

    @Test
    fun unknownTeamIsNull() = runTest {
        assertNull(getTeamDetail("missing"))
    }

    @Test
    fun teamWithoutResolvableClubIsNull() = runTest {
        directory.teams += olympic1

        assertNull(getTeamDetail("o1"))
    }

    @Test
    fun standingIsTheTeamsOwnRowInTheLeagueTable() = runTest {
        givenOlympic()
        directory.standings["l1"] = listOf(
            Standing(3, "r2", "Rival 2", played = 5, points = 4),
            Standing(1, "r1", "Rival 1", played = 5, points = 9),
            Standing(2, "o1", "TTC Olympic 1", played = 5, points = 7),
        )

        val detail = getTeamDetail("o1")!!

        assertEquals(2, detail.standing?.position)
        assertEquals("1. Landesliga Herren", detail.league?.name)
        assertEquals("TTC Olympic", detail.clubName)
    }

    @Test
    fun nextFixtureIsTheEarliestUpcomingOneTheTeamPlays() = runTest {
        givenOlympic()
        directory.fixtures += listOf(
            fixture("later", startsAt = now + 10.days),
            fixture("sooner", startsAt = now + 3.days),
            fixture("past", startsAt = now - 3.days),
        )

        val next = assertIs<NextFixture.Scheduled>(getTeamDetail("o1")!!.nextFixture)

        assertEquals("sooner", next.fixture.id)
        assertEquals("Rival 1", next.opponentName)
        assertTrue(next.isHome)
    }

    @Test
    fun noUpcomingFixtureIsAFreeWeek() = runTest {
        givenOlympic()

        assertEquals(NextFixture.FreeWeek, getTeamDetail("o1")!!.nextFixture)
    }

    @Test
    fun lineupIsOrderedByRcPointsWithUnratedPlayersLastInRepositoryOrder() = runTest {
        givenOlympic()
        directory.lineups["o1"] = listOf(
            LineupPlayer("u1", "Uli"),
            LineupPlayer("b", "Bea", rcPoints = 1800),
            LineupPlayer("u2", "Udo"),
            LineupPlayer("a", "Ana", rcPoints = 2100),
        )

        assertEquals(listOf("a", "b", "u1", "u2"), getTeamDetail("o1")!!.lineup.map { it.playerId })
    }

    @Test
    fun emptyLineupIsNotAnError() = runTest {
        givenOlympic()

        assertEquals(emptyList(), getTeamDetail("o1")!!.lineup)
    }

    @Test
    fun teamWithoutLeagueHasNoStanding() = runTest {
        directory.clubs += Club("olympic", "TTC Olympic", "wttv")
        directory.teams += olympic1

        val detail = getTeamDetail("o1")!!

        assertNull(detail.league)
        assertNull(detail.standing)
    }
}
