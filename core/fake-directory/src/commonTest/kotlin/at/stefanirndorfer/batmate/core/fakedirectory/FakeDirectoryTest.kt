package at.stefanirndorfer.batmate.core.fakedirectory

import at.stefanirndorfer.batmate.domain.club.FixtureStatus
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

    @Test
    fun everyTeamResolvesItsClubLeagueAndStanding() = runTest {
        directory.searchClubs("").forEach { club ->
            directory.getTeams(club.id).forEach { team ->
                assertTrue(directory.getLeague(team.leagueId) != null, team.id)
                assertTrue(directory.getStandings(team.leagueId).any { it.teamId == team.id }, team.id)
            }
        }
    }

    @Test
    fun everyLineupPlayerHasAName() = runTest {
        directory.searchClubs("").forEach { club ->
            directory.getTeams(club.id).forEach { team ->
                directory.getLineup(team.id).forEach { assertTrue(it.name.isNotBlank(), team.id) }
            }
        }
    }

    @Test
    fun everyClubHasATeam() = runTest {
        directory.searchClubs("").forEach { assertTrue(directory.getTeams(it.id).isNotEmpty(), it.id) }
    }

    @Test
    fun oneTeamHasOnlyFinishedFixtures() = runTest {
        val fixtures = directory.getFixtures("olympic-3")
        assertTrue(fixtures.isNotEmpty() && fixtures.all { it.status is FixtureStatus.Finished })
    }
}
