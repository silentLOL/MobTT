package at.stefanirndorfer.mobtt.core.xttv

import at.stefanirndorfer.mobtt.domain.club.DataUnavailable
import at.stefanirndorfer.mobtt.domain.player.PickedPlayerStore
import at.stefanirndorfer.mobtt.domain.player.Player
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

private const val STANDINGS_70_URL = "${XttvSamples.HOST}/xml/v1/getSingleStandings.php?leagueId=70"

private fun row(id: String, first: String, last: String, club: String, suffix: String = "") = "<standing_row><ranked>1</ranked><rank>1</rank><player_id>$id</player_id><player_first_name>$first</player_first_name>" +
    "<player_last_name>$last</player_last_name><player_name_suffix>$suffix</player_name_suffix><club_id>$club</club_id>" +
    "<games_played>3</games_played></standing_row>"

private fun standings(vararg rows: String) = "<single_standing><league_id>70</league_id>${rows.joinToString("")}</single_standing>"

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
            STANDINGS_70_URL to standings(
                row("3", "Jakob", "Irndorfer", "718"),
                row("1", "Stefan", "Irndorfer", "718"),
                row("4", "Anton", "Irndorfer", "718", suffix = "jun."),
                row("1", "Stefan", "Irndorfer", "718"),
            ),
        ),
    )
    private val picked = InMemoryPickedPlayers()
    private val repository = XttvPlayerRepository(XttvApi(xttv.client), XttvSamples.HOST, picked)

    @Test
    fun aLeagueListsItsPlayersByNameOnceEach() = runTest {
        assertEquals(
            listOf(
                Player("4", "Anton Irndorfer jun.", "718"),
                Player("3", "Jakob Irndorfer", "718"),
                Player("1", "Stefan Irndorfer", "718"),
            ),
            repository.getLeaguePlayers("70"),
        )
    }

    @Test
    fun everyReadAsksXttvAgainInsteadOfServingPlayersFromMemory() = runTest {
        repository.getLeaguePlayers("70")
        repository.getLeaguePlayers("70")

        assertEquals(listOf(STANDINGS_70_URL, STANDINGS_70_URL), xttv.requests)
    }

    @Test
    fun anIdXttvCannotKnowIsNotAsked() = runTest {
        assertEquals(emptyList(), repository.getLeaguePlayers("landesliga1"))
        assertEquals(emptyList(), xttv.requests)
    }

    @Test
    fun onlyThePickedPlayerIsKeptAndResolvesLater() = runTest {
        repository.getLeaguePlayers("70")
        assertNull(repository.getPlayer("1"))

        repository.keepPicked("1")

        assertEquals(Player("1", "Stefan Irndorfer", "718"), repository.getPlayer("1"))
        assertEquals(setOf("1"), picked.saved.keys)
    }

    @Test
    fun xttvBeingUnreachableThrowsDataUnavailable() = runTest {
        xttv.failing = true

        assertFailsWith<DataUnavailable> { repository.getLeaguePlayers("70") }
    }

    @Test
    fun aRecordedStandingsRowStillMapsToIdNameAndClub() = runTest {
        val recorded = XttvPlayerRepository(
            XttvApi(FakeXttv(mapOf(STANDINGS_70_URL to XttvSamples.SINGLE_STANDINGS_RECORDED)).client),
            XttvSamples.HOST,
            picked,
        )

        assertEquals(
            listOf(Player("62323", "Beispiel Spieler", "112"), Player("3680", "Reinhard Blauensteiner", "99")),
            recorded.getLeaguePlayers("70"),
        )
    }
}
