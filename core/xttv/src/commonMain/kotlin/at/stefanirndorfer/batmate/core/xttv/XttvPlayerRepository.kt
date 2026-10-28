package at.stefanirndorfer.batmate.core.xttv

import at.stefanirndorfer.batmate.domain.club.DataUnavailable
import at.stefanirndorfer.batmate.domain.player.PickedPlayerStore
import at.stefanirndorfer.batmate.domain.player.Player
import at.stefanirndorfer.batmate.domain.player.PlayerRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlin.concurrent.Volatile
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

private val LEAGUE_LIST_TTL = 1.hours

/**
 * XTTV has no Player search, and `getPlayerDetails` is closed to API access. A search therefore reads every
 * League's single standings straight from XTTV, and neither those Players nor any Team is cached or indexed:
 * a cancelled search stops asking. Only the Players the user picks are kept (in [pickedPlayers]), so they
 * resolve again without a search.
 */
class XttvPlayerRepository(
    private val api: XttvApi,
    private val host: String,
    private val pickedPlayers: PickedPlayerStore,
) : PlayerRepository {
    /** The Players of the latest search, held only so [keepPicked] can find the one picked from it. */
    @Volatile
    private var latestResults: List<Player> = emptyList()

    override suspend fun getPlayer(id: String): Player? = pickedPlayers.get(id)

    override suspend fun searchPlayers(query: String): List<Player> {
        if (query.isBlank()) {
            return emptyList()
        }
        val leagueIds = leagueIds()
        val standings = coroutineScope {
            // The HTTP client's concurrency cap spreads these out; cancelling the search cancels the rest.
            leagueIds.map { leagueId ->
                async {
                    try {
                        api.get(XttvEndpoints.singleStandings(host, leagueId), Duration.ZERO, SingleStandingsXml.serializer())
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: DataUnavailable) {
                        null
                    }
                }
            }.awaitAll()
        }
        if (leagueIds.isNotEmpty() && standings.all { it == null }) {
            throw DataUnavailable("XTTV is unavailable for Player search")
        }
        return standings.asSequence()
            .filterNotNull()
            .flatMap { it.rows }
            .map { it.toDomain() }
            .filter { it.name.contains(query, ignoreCase = true) }
            .distinctBy { it.id }
            .sortedBy { it.name.lowercase() }
            .toList()
            .also { latestResults = it }
    }

    override suspend fun keepPicked(id: String) {
        latestResults.firstOrNull { it.id == id }?.let { pickedPlayers.save(it) }
    }

    private suspend fun leagueIds(): List<String> = api.get(XttvEndpoints.federations(host), FEDERATION_TTL, FederationsXml.serializer())
        .federation
        .flatMap { api.get(XttvEndpoints.leagues(host, it.id), LEAGUE_LIST_TTL, LeaguesXml.serializer()).league }
        .map { it.id }
}
