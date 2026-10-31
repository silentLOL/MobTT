package at.stefanirndorfer.batmate.domain.club

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Instant

class LeagueDetailTest {
    private val directory = FakeClubData()
    private val getLeagueDetail = GetLeagueDetailUseCase(directory, directory)

    private fun fixture(id: String, startsAt: Long) = Fixture(
        id = id,
        homeTeamId = "o1",
        homeTeamName = "TTC Olympic 1",
        awayTeamId = "r1",
        awayTeamName = "Rival 1",
        startsAt = Instant.fromEpochSeconds(startsAt),
        venue = null,
        status = FixtureStatus.Scheduled,
    )

    @Test
    fun unknownLeagueIsNull() = runTest {
        assertNull(getLeagueDetail("missing"))
    }

    @Test
    fun standingsAndFixturesAreOrderedWhateverTheRepositoryReturns() = runTest {
        directory.leagues += League("l1", "1. Landesliga Herren")
        directory.teams += Team("o1", "TTC Olympic 1", "olympic", "l1")
        directory.standings["l1"] = listOf(
            Standing(2, "o1", "TTC Olympic 1", played = 5, points = 7),
            Standing(1, "r1", "Rival 1", played = 5, points = 9),
        )
        directory.fixtures += listOf(fixture("b", startsAt = 2_000), fixture("a", startsAt = 1_000))

        val detail = getLeagueDetail("l1")!!

        assertEquals("1. Landesliga Herren", detail.league.name)
        assertEquals(listOf(1, 2), detail.standings.map { it.position })
        assertEquals(listOf("a", "b"), detail.fixtures.map { it.id })
    }
}
