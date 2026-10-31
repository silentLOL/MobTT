package at.stefanirndorfer.batmate.domain.club

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class PlayerSearchScopeTest {
    private val directory = FakeClubData()

    @Test
    fun associationClubsAreOnlyThoseOfThatAssociationByName() = runTest {
        directory.clubs += listOf(Club("c2", "Zebra TTC", "wttv"), Club("c1", "Alpha TTC", "wttv"), Club("c3", "Other TTC", "noettv"))

        assertEquals(listOf("c1", "c2"), ListAssociationClubsUseCase(directory)("wttv").map { it.id })
    }

    @Test
    fun clubPlayersListEveryLineupOnceByName() = runTest {
        directory.teams += listOf(Team("t1", "TTC 1", "c1", "l1"), Team("t2", "TTC 2", "c1", "l1"), Team("t3", "Rival", "c2", "l1"))
        directory.lineups["t1"] = listOf(LineupPlayer("p2", "Zoe Zeller", 1200), LineupPlayer("p1", "anna Gruber", 1500))
        directory.lineups["t2"] = listOf(LineupPlayer("p1", "anna Gruber", 1500))
        directory.lineups["t3"] = listOf(LineupPlayer("p9", "Not Mine"))

        assertEquals(listOf("p1", "p2"), ListClubPlayersUseCase(directory)("c1").map { it.playerId })
    }
}
