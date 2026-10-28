package at.stefanirndorfer.batmate.core.xttv

import at.stefanirndorfer.batmate.domain.club.DataUnavailable
import at.stefanirndorfer.batmate.domain.player.PickedPlayerStore
import at.stefanirndorfer.batmate.domain.player.Player
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

private const val STANDINGS_70_URL = "${XttvSamples.HOST}/xml/v1/getSingleStandings.php?leagueId=70"
private const val STANDINGS_8001_URL = "${XttvSamples.HOST}/xml/v1/getSingleStandings.php?leagueId=8001"

private fun row(id: String, first: String, last: String, club: String, suffix: String = "") = "<standing_row><ranked>1</ranked><rank>1</rank><player_id>$id</player_id><player_first_name>$first</player_first_name>" +
    "<player_last_name>$last</player_last_name><player_name_suffix>$suffix</player_name_suffix><club_id>$club</club_id>" +
    "<games_played>3</games_played></standing_row>"

private fun standings(vararg rows: String) = "<single_standing><league_id>1</league_id>${rows.joinToString("")}</single_standing>"

private class InMemoryPickedPlayers : PickedPlayerStore {
    val saved = mutableMapOf<String, Player>()

    override suspend fun save(player: Player) {
        saved[player.id] = player
    }

    override suspend fun get(id: String) = saved[id]
}

class XttvPlayerRepositoryTest {
    private val xttv = FakeXttv(
        mapOf(
            XttvSamples.FEDERATIONS_URL to XttvSamples.FEDERATIONS,
            XttvSamples.LEAGUES_188_URL to XttvSamples.LEAGUES_188,
            XttvSamples.LEAGUES_190_URL to XttvSamples.LEAGUES_190,
            STANDINGS_70_URL to standings(row("1", "Stefan", "Irndorfer", "718"), row("2", "Anna", "Huber", "1")),
            STANDINGS_8001_URL to standings(
                row("3", "Jakob", "Irndorfer", "718"),
                row("1", "Stefan", "Irndorfer", "718"),
                row("4", "Anton", "Irndorfer", "718", suffix = "jun."),
            ),
        ),
    )
    private val picked = InMemoryPickedPlayers()
    private val repository = XttvPlayerRepository(XttvApi(xttv.client), XttvSamples.HOST, picked)

    @Test
    fun aSearchFindsPlayersByNameAcrossEveryLeagueOnce() = runTest {
        val found = repository.searchPlayers("irndorfer")

        assertEquals(
            listOf(
                Player("4", "Anton Irndorfer jun.", "718"),
                Player("3", "Jakob Irndorfer", "718"),
                Player("1", "Stefan Irndorfer", "718"),
            ),
            found,
        )
    }

    @Test
    fun everySearchAsksXttvAgainInsteadOfServingPlayersFromMemory() = runTest {
        repository.searchPlayers("irndorfer")
        xttv.requests.clear()

        repository.searchPlayers("irndorfer")

        assertEquals(setOf(STANDINGS_70_URL, STANDINGS_8001_URL), xttv.requests.toSet())
    }

    @Test
    fun aBlankQueryAsksNothing() = runTest {
        assertEquals(emptyList(), repository.searchPlayers(" "))
        assertEquals(emptyList(), xttv.requests)
    }

    @Test
    fun onlyThePickedPlayerIsKeptAndResolvesLater() = runTest {
        repository.searchPlayers("irndorfer")
        assertNull(repository.getPlayer("1"))

        repository.keepPicked("1")

        assertEquals(Player("1", "Stefan Irndorfer", "718"), repository.getPlayer("1"))
        assertEquals(setOf("1"), picked.saved.keys)
    }

    @Test
    fun xttvBeingUnreachableThrowsDataUnavailable() = runTest {
        xttv.failing = true

        assertFailsWith<DataUnavailable> { repository.searchPlayers("irndorfer") }
    }
}
