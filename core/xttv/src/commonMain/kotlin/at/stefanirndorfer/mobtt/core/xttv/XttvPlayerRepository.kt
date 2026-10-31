package at.stefanirndorfer.mobtt.core.xttv

import at.stefanirndorfer.mobtt.domain.player.PickedPlayerStore
import at.stefanirndorfer.mobtt.domain.player.Player
import at.stefanirndorfer.mobtt.domain.player.PlayerRepository
import kotlin.concurrent.Volatile
import kotlin.time.Duration

/**
 * XTTV has no Player search, and `getPlayerDetails` is closed to API access. Players are therefore read per League
 * from its single standings, straight from XTTV: neither those Players nor any Team is cached or indexed (a zero
 * TTL is never stored). Only the Players the user picks are kept (in [pickedPlayers]), so they resolve again
 * without asking XTTV, and the Players the user opens from a lineup ([remember]).
 */
class XttvPlayerRepository(
    private val api: XttvApi,
    private val host: String,
    private val pickedPlayers: PickedPlayerStore,
) : PlayerRepository {
    /** The Players of the latest League read, held only so [keepPicked] can find the one picked from it. */
    @Volatile
    private var latestResults: List<Player> = emptyList()

    override suspend fun getPlayer(id: String): Player? = pickedPlayers.get(id)

    override suspend fun getLeaguePlayers(leagueId: String): List<Player> {
        if (!XttvEndpoints.isXttvId(leagueId)) {
            return emptyList()
        }
        return api.get(XttvEndpoints.singleStandings(host, leagueId), Duration.ZERO, SingleStandingsXml.serializer())
            .rows
            .map { it.toDomain() }
            .distinctBy { it.id }
            .sortedBy { it.name.lowercase() }
            .also { latestResults = it }
    }

    override suspend fun remember(player: Player) = pickedPlayers.save(player)

    override suspend fun keepPicked(id: String) {
        latestResults.firstOrNull { it.id == id }?.let { pickedPlayers.save(it) }
    }
}
