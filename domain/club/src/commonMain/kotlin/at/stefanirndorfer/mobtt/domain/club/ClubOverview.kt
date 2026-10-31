package at.stefanirndorfer.mobtt.domain.club

/** [players] are the Players of every Team's lineup, once each, highest RC first; Players without RC come last by name. */
data class ClubOverview(
    val club: Club,
    val association: Association,
    val teams: List<TeamOverview>,
    val players: List<LineupPlayer>,
) {
    /** [teams] with every Team of the same name merged, e.g. a League Team and its Cup entry, in XTTV's order. */
    val teamGroups: List<TeamGroup>
        get() = teams.groupBy { it.team.name.trim().lowercase() }.values.map(::TeamGroup)
}

/** One Team as the Club lists it: XTTV knows it once per competition, each with its own next Fixture. [teams] is never empty. */
data class TeamGroup(val teams: List<TeamOverview>) {
    /** The first entry, which is the League Team; it is the one that opens. */
    val primary: TeamOverview get() = teams.first()
}

/** [league] and [standing] are null when XTTV has no League or no table row for the Team. */
data class TeamOverview(
    val team: Team,
    val league: League?,
    val standing: Standing?,
    val nextFixture: NextFixture,
)

sealed interface NextFixture {
    data class Scheduled(val fixture: Fixture, val opponentName: String, val isHome: Boolean) : NextFixture

    /** No Fixture left to play; a normal state, not an error. */
    data object FreeWeek : NextFixture
}
