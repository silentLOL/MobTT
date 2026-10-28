package at.stefanirndorfer.batmate.core.xttv

import at.stefanirndorfer.batmate.domain.club.Fixture
import at.stefanirndorfer.batmate.domain.club.FixtureRepository
import at.stefanirndorfer.batmate.domain.club.TeamRepository
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
        if (team.leagueId.isBlank()) {
            return emptyList()
        }
        return api.get(XttvEndpoints.leagueGames(host, team.leagueId), FIXTURE_TTL, GamesXml.serializer()).game
            .filter { it.homeTeamId == teamId || it.awayTeamId == teamId }
            .mapNotNull { it.toDomain() }
            .sortedBy { it.startsAt }
    }
}
