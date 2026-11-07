package at.stefanirndorfer.mobtt.domain.rating

import at.stefanirndorfer.mobtt.domain.club.DataUnavailable
import at.stefanirndorfer.mobtt.domain.player.Player
import at.stefanirndorfer.mobtt.domain.player.PlayerRepository
import at.stefanirndorfer.mobtt.domain.player.RankedPlayer
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

private class Rankings(
    val byLeague: Map<String, List<RankedPlayer>>,
    val failing: Set<String> = emptySet(),
) : PlayerRepository {
    val asked = mutableListOf<String>()

    override suspend fun getPlayer(id: String): Player? = null

    override suspend fun getLeaguePlayers(leagueId: String): List<Player> = emptyList()

    override suspend fun getPlayerRanking(leagueId: String): List<RankedPlayer> {
        asked += leagueId
        if (leagueId in failing) {
            throw DataUnavailable("XTTV unreachable")
        }
        return byLeague[leagueId].orEmpty()
    }
}

private class Histories(val byId: Map<String, RatingHistory>) : RatingHistoryRepository {
    override suspend fun getRatingHistory(ratingsCentralId: String): RatingHistory = byId[ratingsCentralId] ?: throw DataUnavailable("Ratings Central unreachable")
}

class RatingHistoryTest {
    private val anna = Player("p1", "Anna", "c1")
    private val other = Player("p9", "Other", "c1")

    private fun row(player: Player, ratingsCentralId: String?) = RankedPlayer(player, rank = 1, teamId = "t1", gamesPlayed = 4, matchesWon = 3, matchesLost = 1, ratingsCentralId = ratingsCentralId)

    private val update = RatingUpdate(
        date = LocalDate(2026, 10, 25),
        name = "OÖTTV Meisterschaft 2026/27 (Spiele KW 43/2026)",
        before = Rating(447, 66),
        after = Rating(519, 86),
        pointChange = 72,
    )

    @Test
    fun theIdComesFromThePlayersRowInTheFirstLeague() = runTest {
        val rankings = Rankings(mapOf("l1" to listOf(row(other, "9"), row(anna, "163468"))))

        assertEquals("163468", GetRatingsCentralIdUseCase(rankings)("p1", listOf("l1", "l2")))
        assertEquals(listOf("l1"), rankings.asked)
    }

    @Test
    fun aLeagueWithoutThePlayersRowMovesOnToTheNext() = runTest {
        val rankings = Rankings(mapOf("l1" to listOf(row(other, "9")), "l2" to listOf(row(anna, "163468"))))

        assertEquals("163468", GetRatingsCentralIdUseCase(rankings)("p1", listOf("l1", "l2")))
    }

    @Test
    fun aLeagueAskedTwiceIsReadOnce() = runTest {
        val rankings = Rankings(emptyMap())

        GetRatingsCentralIdUseCase(rankings)("p1", listOf("l1", "l1"))

        assertEquals(listOf("l1"), rankings.asked)
    }

    @Test
    fun noLeagueMeansNoId() = runTest {
        assertNull(GetRatingsCentralIdUseCase(Rankings(emptyMap()))("p1", emptyList()))
    }

    @Test
    fun aPlayerInNoRankingHasNoId() = runTest {
        assertNull(GetRatingsCentralIdUseCase(Rankings(mapOf("l1" to listOf(row(other, "9")))))("p1", listOf("l1")))
    }

    @Test
    fun aRowWithoutAnIdMeansNoId() = runTest {
        assertNull(GetRatingsCentralIdUseCase(Rankings(mapOf("l1" to listOf(row(anna, null)))))("p1", listOf("l1")))
    }

    @Test
    fun anUnreachableRankingFailsTheLookup() = runTest {
        assertFailsWith<DataUnavailable> { GetRatingsCentralIdUseCase(Rankings(emptyMap(), failing = setOf("l1")))("p1", listOf("l1")) }
    }

    @Test
    fun theHistoryOfAnIdComesFromTheRepository() = runTest {
        val history = RatingHistory(listOf(update))

        assertEquals(history, GetRatingHistoryUseCase(Histories(mapOf("163468" to history)), RatingHistoryAvailability(true))("163468"))
    }

    @Test
    fun anEmptyHistoryStaysEmpty() = runTest {
        assertEquals(RatingHistory(emptyList()), GetRatingHistoryUseCase(Histories(mapOf("1" to RatingHistory(emptyList()))), RatingHistoryAvailability(true))("1"))
    }

    @Test
    fun anUnreachableHistoryFails() = runTest {
        assertFailsWith<DataUnavailable> { GetRatingHistoryUseCase(Histories(emptyMap()), RatingHistoryAvailability(true))("1") }
    }

    @Test
    fun theUseCaseSaysWhetherRatingHistoriesAreShown() {
        assertTrue(GetRatingHistoryUseCase(Histories(emptyMap()), RatingHistoryAvailability(true)).isEnabled)
        assertFalse(GetRatingHistoryUseCase(Histories(emptyMap()), RatingHistoryAvailability(false)).isEnabled)
    }
}
