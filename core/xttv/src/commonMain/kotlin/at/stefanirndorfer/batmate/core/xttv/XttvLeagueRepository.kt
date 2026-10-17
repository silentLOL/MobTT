package at.stefanirndorfer.batmate.core.xttv

import at.stefanirndorfer.batmate.domain.club.League
import at.stefanirndorfer.batmate.domain.club.LeagueRepository
import at.stefanirndorfer.batmate.domain.club.Standing
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

private val LEAGUE_TTL = 1.hours
private val STANDINGS_TTL = 5.minutes

/** Both calls are asked at the default host, since a League id carries no Association (ADR-0008). */
class XttvLeagueRepository(
    private val api: XttvApi,
    private val hosts: XttvHostResolver,
) : LeagueRepository {
    override suspend fun getLeague(id: String): League? {
        val url = XttvEndpoints.league(hosts.defaultHost, id)
        return api.get(url, LEAGUE_TTL, LeaguesXml.serializer()).league.firstOrNull { it.id == id }?.toDomain()
    }

    override suspend fun getStandings(leagueId: String): List<Standing> {
        val url = XttvEndpoints.leagueStandings(hosts.defaultHost, leagueId)
        return api.get(url, STANDINGS_TTL, StandingsXml.serializer()).standing
            .map { it.toDomain() }
            .sortedBy { it.position }
    }
}
