package at.stefanirndorfer.batmate.core.xttv

import at.stefanirndorfer.batmate.domain.club.ClubRepository
import at.stefanirndorfer.batmate.domain.club.LineupPlayer
import at.stefanirndorfer.batmate.domain.club.Team
import at.stefanirndorfer.batmate.domain.club.TeamRepository
import kotlin.time.Duration.Companion.hours

private val TEAM_TTL = 1.hours

/**
 * A Club's Teams are asked at its Association's host; a single Team's details at the default host, since its
 * Association is not known before the Team is (ADR-0008). PROVISIONAL: [getLineup] still comes from [lineups].
 */
class XttvTeamRepository(
    private val api: XttvApi,
    private val hosts: XttvHostResolver,
    private val clubs: ClubRepository,
    private val lineups: LineupSource? = null,
) : TeamRepository {
    override suspend fun getTeam(id: String): Team? {
        val url = XttvEndpoints.team(hosts.defaultHost, id)
        return api.get(url, TEAM_TTL, TeamsXml.serializer()).team.firstOrNull { it.id == id }?.toDomain()
    }

    override suspend fun getTeams(clubId: String): List<Team> {
        val club = clubs.getClub(clubId) ?: return emptyList()
        val url = XttvEndpoints.clubTeams(hosts.hostFor(club.associationId), clubId)
        return api.get(url, TEAM_TTL, TeamsXml.serializer()).team.map { it.toDomain() }
    }

    override suspend fun getLineup(teamId: String): List<LineupPlayer> = lineups?.lineup(teamId).orEmpty()
}
