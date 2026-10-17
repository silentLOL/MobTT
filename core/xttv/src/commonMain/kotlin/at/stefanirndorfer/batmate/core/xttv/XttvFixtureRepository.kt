package at.stefanirndorfer.batmate.core.xttv

import at.stefanirndorfer.batmate.domain.club.ClubRepository
import at.stefanirndorfer.batmate.domain.club.Fixture
import at.stefanirndorfer.batmate.domain.club.FixtureRepository
import at.stefanirndorfer.batmate.domain.club.TeamRepository
import kotlin.time.Duration.Companion.minutes

private val FIXTURE_TTL = 5.minutes

/**
 * A Team's Fixtures are its League's Games, asked at the host of its Club's Association (ADR-0008) and filtered
 * to the Team. The League's answer is shared by every Team in it, so it is cached once.
 */
class XttvFixtureRepository(
    private val api: XttvApi,
    private val hosts: XttvHostResolver,
    private val teams: TeamRepository,
    private val clubs: ClubRepository,
) : FixtureRepository {
    override suspend fun getFixtures(teamId: String): List<Fixture> {
        val team = teams.getTeam(teamId) ?: return emptyList()
        val club = clubs.getClub(team.clubId) ?: return emptyList()
        val url = XttvEndpoints.leagueGames(hosts.hostFor(club.associationId), team.leagueId)
        return api.get(url, FIXTURE_TTL, GamesXml.serializer()).game
            .filter { it.homeTeamId == teamId || it.awayTeamId == teamId }
            .mapNotNull { it.toDomain() }
            .sortedBy { it.startsAt }
    }
}
