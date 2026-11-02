package at.stefanirndorfer.mobtt.core.xttv

import at.stefanirndorfer.mobtt.domain.player.PickedPlayerStore
import at.stefanirndorfer.mobtt.domain.player.Player
import at.stefanirndorfer.mobtt.domain.player.PlayerRepository
import kotlin.concurrent.Volatile
import kotlin.time.Duration.Companion.minutes

/** Standings are held in memory only (never on disk), so reopening a League is instant while XTTV is slow. */
private val STANDINGS_TTL = 5.minutes

/**
 * XTTV has no Player search, and `getPlayerDetails` is closed to API access. Players are therefore read per League
 * from its single standings. Those Players are indexed nowhere and only held in memory for [STANDINGS_TTL]; no Team is
 * cached. Only the Players the user picks are kept on disk (in [pickedPlayers]), so they resolve again
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
        return api.get(XttvEndpoints.singleStandings(host, leagueId), STANDINGS_TTL, SingleStandingsXml.serializer())
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
