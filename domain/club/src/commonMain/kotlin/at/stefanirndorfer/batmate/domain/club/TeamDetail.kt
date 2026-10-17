package at.stefanirndorfer.batmate.domain.club

/**
 * PROVISIONAL: XTTV documents neither the lineup nor the full League table yet, so this shape is a guess
 * served by the fake directory. Revisit with the real API (ticket 13).
 *
 * [standings] is empty when the Team has no League. [lineup] is empty when XTTV lists no Players for the Team.
 */
data class TeamDetail(
    val team: Team,
    val clubName: String,
    val league: League?,
    val standings: List<Standing>,
    val lineup: List<LineupPlayer>,
) {
    fun isThisTeam(standing: Standing): Boolean = standing.teamId == team.id
}

/** PROVISIONAL, see [TeamDetail]. [playerId] matches `Player.id`, so a Player profile can open from it. */
data class LineupPlayer(val playerId: String, val name: String)
