package at.stefanirndorfer.batmate.domain.club

/**
 * PROVISIONAL: XTTV documents no lineup yet, so [lineup] is a guess served by the fake directory. Revisit with
 * the real API (ticket 13).
 *
 * [league] and [standing] are null when the Team has no League or no table row. The full table and the League's
 * Fixtures live in [LeagueDetail]. [lineup] is empty when XTTV lists no Players for the Team.
 */
data class TeamDetail(
    val team: Team,
    val clubName: String,
    val league: League?,
    val standing: Standing?,
    val nextFixture: NextFixture,
    val lineup: List<LineupPlayer>,
)

/**
 * PROVISIONAL, see [TeamDetail]. [playerId] matches `Player.id`, so a Player profile can open from it.
 * [rcPoints] is the Player's RC value and [ageGroup] XTTV's age class code (e.g. `U171`, `S50`); each is null when
 * XTTV gives none.
 */
data class LineupPlayer(val playerId: String, val name: String, val rcPoints: Int? = null, val ageGroup: String? = null)
