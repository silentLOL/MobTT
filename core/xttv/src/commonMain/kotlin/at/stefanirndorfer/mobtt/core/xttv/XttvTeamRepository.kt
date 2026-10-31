package at.stefanirndorfer.mobtt.core.xttv

import at.stefanirndorfer.mobtt.domain.club.LineupPlayer
import at.stefanirndorfer.mobtt.domain.club.Team
import at.stefanirndorfer.mobtt.domain.club.TeamRepository
import kotlin.time.Duration.Companion.hours

private val TEAM_TTL = 1.hours

class XttvTeamRepository(
    private val api: XttvApi,
    private val host: String,
) : TeamRepository {
    override suspend fun getTeam(id: String): Team? {
        if (!XttvEndpoints.isXttvId(id)) {
            return null
        }
        return api.get(XttvEndpoints.team(host, id), TEAM_TTL, TeamXml.serializer()).toDomain()
    }

    override suspend fun getTeams(clubId: String): List<Team> {
        if (!XttvEndpoints.isXttvId(clubId)) {
            return emptyList()
        }
        return api.get(XttvEndpoints.club(host, clubId), TEAM_TTL, ClubXml.serializer()).teamList.team
            .distinctBy { it.id }
            .map { Team(id = it.id, name = it.name, clubId = clubId, leagueId = it.leagueId) }
    }

    override suspend fun getLineup(teamId: String): List<LineupPlayer> {
        if (!XttvEndpoints.isXttvId(teamId)) {
            return emptyList()
        }
        return api.get(XttvEndpoints.team(host, teamId), TEAM_TTL, TeamXml.serializer()).lineup()
    }
}
