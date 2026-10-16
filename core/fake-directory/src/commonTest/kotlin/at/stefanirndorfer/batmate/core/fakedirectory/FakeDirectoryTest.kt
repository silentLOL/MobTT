package at.stefanirndorfer.batmate.core.fakedirectory

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FakeDirectoryTest {
    private val directory = FakeDirectory()

    @Test
    fun blankQueryListsEveryPlayer() = runTest {
        assertTrue(directory.searchPlayers("").size > 1)
    }

    @Test
    fun playersMatchOnNameOrClubIgnoringCase() = runTest {
        assertEquals(listOf("huber"), directory.searchPlayers("michael").map { it.id })
        assertEquals(listOf("huber", "gruber"), directory.searchPlayers("olympic").map { it.id })
    }

    @Test
    fun everyPlayerAndClubResolvesItsParent() = runTest {
        directory.searchPlayers("").forEach { assertTrue(directory.getClub(it.clubId) != null, it.id) }
        directory.searchClubs("").forEach { assertTrue(directory.getAssociation(it.associationId) != null, it.id) }
    }
}
