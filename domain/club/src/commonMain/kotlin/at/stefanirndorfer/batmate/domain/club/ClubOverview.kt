package at.stefanirndorfer.batmate.domain.club

data class ClubOverview(val club: Club, val association: Association, val teams: List<TeamOverview>)

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
