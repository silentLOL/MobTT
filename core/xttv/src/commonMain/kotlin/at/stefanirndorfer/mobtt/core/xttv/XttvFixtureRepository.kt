package at.stefanirndorfer.mobtt.core.xttv

import at.stefanirndorfer.mobtt.domain.club.Fixture
import at.stefanirndorfer.mobtt.domain.club.FixtureRepository
import at.stefanirndorfer.mobtt.domain.club.TeamRepository
import kotlin.time.Duration.Companion.minutes

private val FIXTURE_TTL = 5.minutes

/**
 * A Team's Fixtures are its League's Games filtered to the Team. The League's answer is shared by every Team
 * in it, so it is cached once.
 */
class XttvFixtureRepository(
    private val api: XttvApi,
    private val host: String,
    private val teams: TeamRepository,
) : FixtureRepository {
    override suspend fun getFixtures(teamId: String): List<Fixture> {
        val team = teams.getTeam(teamId) ?: return emptyList()
        return getLeagueFixtures(team.leagueId).filter { it.homeTeamId == teamId || it.awayTeamId == teamId }
    }

    override suspend fun getLeagueFixtures(leagueId: String): List<Fixture> {
        if (!XttvEndpoints.isXttvId(leagueId)) {
            return emptyList()
        }
        return api.get(XttvEndpoints.leagueGames(host, leagueId), FIXTURE_TTL, GamesXml.serializer()).game
            .mapNotNull { it.toDomain() }
            .sortedBy { it.startsAt }
    }
}
