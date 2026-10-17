package at.stefanirndorfer.batmate.domain.club

import kotlin.time.Instant

/** [venue] is null when XTTV does not say where the Fixture is played. */
data class Fixture(
    val id: String,
    val homeTeamId: String,
    val homeTeamName: String,
    val awayTeamId: String,
    val awayTeamName: String,
    val startsAt: Instant,
    val venue: String?,
    val status: FixtureStatus,
)

sealed interface FixtureStatus {
    data object Scheduled : FixtureStatus

    data class Finished(val score: FixtureScore) : FixtureStatus
}

/** Counts Matches won, e.g. 6:4. */
data class FixtureScore(val homeMatchesWon: Int, val awayMatchesWon: Int)
