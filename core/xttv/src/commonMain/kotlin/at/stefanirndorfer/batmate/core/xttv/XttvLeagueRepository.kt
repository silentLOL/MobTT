package at.stefanirndorfer.batmate.core.xttv

import at.stefanirndorfer.batmate.domain.club.League
import at.stefanirndorfer.batmate.domain.club.LeagueRepository
import at.stefanirndorfer.batmate.domain.club.Standing
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

private val LEAGUE_TTL = 1.hours
private val STANDINGS_TTL = 5.minutes

class XttvLeagueRepository(
    private val api: XttvApi,
    private val host: String,
) : LeagueRepository {
    /** XTTV lists Leagues per Federation only, so this reads each Federation's list (cached) until it finds [id]. */
    override suspend fun getLeague(id: String): League? {
        val federations = api.get(XttvEndpoints.federations(host), FEDERATION_TTL, FederationsXml.serializer()).federation
        for (federation in federations) {
            val leagues = api.get(XttvEndpoints.leagues(host, federation.id), LEAGUE_TTL, LeaguesXml.serializer())
            leagues.league.firstOrNull { it.id == id }?.let { return it.toDomain() }
        }
        return null
    }

    override suspend fun getStandings(leagueId: String): List<Standing> {
        if (!XttvEndpoints.isXttvId(leagueId)) {
            return emptyList()
        }
        return api.get(XttvEndpoints.leagueStandings(host, leagueId), STANDINGS_TTL, StandingsXml.serializer()).standing
            .map { it.toDomain() }
            .sortedBy { it.position }
    }
}
