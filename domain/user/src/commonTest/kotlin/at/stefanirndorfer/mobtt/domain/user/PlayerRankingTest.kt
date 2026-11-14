package at.stefanirndorfer.mobtt.domain.user

import at.stefanirndorfer.mobtt.domain.club.League
import at.stefanirndorfer.mobtt.domain.club.LeagueRepository
import at.stefanirndorfer.mobtt.domain.club.Standing
import at.stefanirndorfer.mobtt.domain.player.Player
import at.stefanirndorfer.mobtt.domain.player.PlayerRepository
import at.stefanirndorfer.mobtt.domain.player.RankedPlayer
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

private class RankingDirectory(
    val ranking: List<RankedPlayer>,
    val standings: List<Standing>,
) : PlayerRepository,
    LeagueRepository {
    override suspend fun getPlayer(id: String): Player? = null

    override suspend fun getLeaguePlayers(leagueId: String) = ranking.map { it.player }

    override suspend fun getPlayerRanking(leagueId: String) = ranking

    override suspend fun getLeague(id: String) = League(id, "Liga")

    override suspend fun getLeagues(associationId: String) = emptyList<League>()

    override suspend fun getStandings(leagueId: String) = standings
}

class PlayerRankingTest {
    private val one = Standing(position = 1, teamId = "t1", teamName = "TTC Olympic 1", played = 6, points = 10)
    private val two = Standing(position = 2, teamId = "t2", teamName = "Nussdorf 1", played = 6, points = 8)

    private fun ranked(id: String, name: String, rank: Int?, teamId: String = "t1") = RankedPlayer(Player(id, name, clubId = "c1"), rank = rank, teamId = teamId, gamesPlayed = 4, matchesWon = 6, matchesLost = 2)

    private fun getRanking(ranking: List<RankedPlayer>, standings: List<Standing> = listOf(one, two)) = RankingDirectory(ranking, standings).let { GetPlayerRankingUseCase(it, it) }

    @Test
    fun rankedPlayersComeByRankWithTheirTeamName() = runTest {
        val ranking = getRanking(listOf(ranked("p2", "Bea", 2, "t2"), ranked("p1", "Anna", 1)))("l1")

        assertEquals(
            listOf(
                PlayerRankingRow(ranked("p1", "Anna", 1), teamName = "TTC Olympic 1"),
                PlayerRankingRow(ranked("p2", "Bea", 2, "t2"), teamName = "Nussdorf 1"),
            ),
            ranking,
        )
    }

    @Test
    fun unrankedPlayersComeLastByName() = runTest {
        val ranking = getRanking(listOf(ranked("p3", "Carl", null), ranked("p1", "Anna", 1), ranked("p2", "Bea", null)))("l1")

        assertEquals(listOf("Anna", "Bea", "Carl"), ranking.map { it.ranked.player.name })
    }

    @Test
    fun aPlayerListedForTwoTeamsKeepsBothRows() = runTest {
        val ranking = getRanking(listOf(ranked("p1", "Anna", 1), ranked("p1", "Anna", 3, "t2")))("l1")

        assertEquals(listOf("TTC Olympic 1", "Nussdorf 1"), ranking.map { it.teamName })
    }

    @Test
    fun aTeamMissingFromTheStandingsLeavesTheNameEmptyButKeepsThePlayer() = runTest {
        val ranking = getRanking(listOf(ranked("p1", "Anna", 1, "gone")))("l1")

        assertEquals(listOf(PlayerRankingRow(ranked("p1", "Anna", 1, "gone"), teamName = null)), ranking)
    }
}
