package at.stefanirndorfer.batmate.core.xttv

import at.stefanirndorfer.batmate.domain.club.LineupPlayer
import at.stefanirndorfer.batmate.domain.club.Team
import at.stefanirndorfer.batmate.domain.club.TeamRepository
import kotlin.time.Duration.Companion.hours

private val TEAM_TTL = 1.hours

/** PROVISIONAL: [getLineup] still comes from [lineups]; `getTeamDetails` lists players, not yet mapped. */
class XttvTeamRepository(
    private val api: XttvApi,
    private val host: String,
    private val lineups: LineupSource? = null,
) : TeamRepository {
    override suspend fun getTeam(id: String): Team? = api.get(XttvEndpoints.team(host, id), TEAM_TTL, TeamXml.serializer()).toDomain()

    override suspend fun getTeams(clubId: String): List<Team> = api.get(XttvEndpoints.club(host, clubId), TEAM_TTL, ClubXml.serializer()).teamList.team
        .map { Team(id = it.id, name = it.name, clubId = clubId, leagueId = it.leagueId) }

    override suspend fun getLineup(teamId: String): List<LineupPlayer> = lineups?.lineup(teamId).orEmpty()
}
