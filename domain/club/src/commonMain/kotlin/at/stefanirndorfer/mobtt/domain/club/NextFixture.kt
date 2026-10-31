package at.stefanirndorfer.mobtt.domain.club

import kotlin.time.Instant

/** [fixtures] may be any Fixtures; only the ones [team] plays in and that are still to come count. */
internal fun nextFixtureOf(team: Team, fixtures: List<Fixture>, now: Instant): NextFixture {
    val next = fixtures
        .filter { it.status == FixtureStatus.Scheduled && it.startsAt >= now }
        .filter { it.homeTeamId == team.id || it.awayTeamId == team.id }
        .minByOrNull { it.startsAt }
        ?: return NextFixture.FreeWeek
    val isHome = next.homeTeamId == team.id
    return NextFixture.Scheduled(
        fixture = next,
        opponentName = if (isHome) next.awayTeamName else next.homeTeamName,
        isHome = isHome,
    )
}
