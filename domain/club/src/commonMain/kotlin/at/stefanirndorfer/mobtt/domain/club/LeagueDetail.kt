package at.stefanirndorfer.mobtt.domain.club

/** [standings] is ordered by [Standing.position]. The League's Fixtures come from [GetLeagueFixturesUseCase]. */
data class LeagueDetail(
    val league: League,
    val standings: List<Standing>,
)
