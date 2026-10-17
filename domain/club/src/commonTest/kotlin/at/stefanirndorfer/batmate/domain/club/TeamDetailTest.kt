package at.stefanirndorfer.batmate.domain.club

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class TeamDetailTest {
    private val directory = FakeClubData()
    private val getTeamDetail = GetTeamDetailUseCase(directory, directory, directory)

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
    fun standingsAreOrderedByPositionWhateverTheRepositoryReturns() = runTest {
        givenOlympic()
        directory.standings["l1"] = listOf(
            Standing(3, "r2", "Rival 2", played = 5, points = 4),
            Standing(1, "r1", "Rival 1", played = 5, points = 9),
            Standing(2, "o1", "TTC Olympic 1", played = 5, points = 7),
        )

        val detail = getTeamDetail("o1")!!

        assertEquals(listOf(1, 2, 3), detail.standings.map { it.position })
        assertEquals("1. Landesliga Herren", detail.league?.name)
        assertEquals("TTC Olympic", detail.clubName)
    }

    @Test
    fun lineupKeepsTheRepositoryOrder() = runTest {
        givenOlympic()
        directory.lineups["o1"] = listOf(LineupPlayer("b", "Bea"), LineupPlayer("a", "Ana"))

        assertEquals(listOf("b", "a"), getTeamDetail("o1")!!.lineup.map { it.playerId })
    }

    @Test
    fun emptyLineupIsNotAnError() = runTest {
        givenOlympic()

        assertEquals(emptyList(), getTeamDetail("o1")!!.lineup)
    }

    @Test
    fun teamWithoutLeagueHasNoStandings() = runTest {
        directory.clubs += Club("olympic", "TTC Olympic", "wttv")
        directory.teams += olympic1

        val detail = getTeamDetail("o1")!!

        assertNull(detail.league)
        assertEquals(emptyList(), detail.standings)
    }
}
