package at.stefanirndorfer.mobtt.domain.club

interface TeamRepository {
    suspend fun getTeam(id: String): Team?

    suspend fun getTeams(clubId: String): List<Team>

    /** PROVISIONAL: the XTTV lineup shape is undocumented. Empty when the Team has no Players listed. */
    suspend fun getLineup(teamId: String): List<LineupPlayer>
}
