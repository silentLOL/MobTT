package at.stefanirndorfer.batmate.domain.club

/** [standings] is ordered by [Standing.position], [fixtures] by [Fixture.startsAt]. */
data class LeagueDetail(
    val league: League,
    val standings: List<Standing>,
    val fixtures: List<Fixture>,
)
