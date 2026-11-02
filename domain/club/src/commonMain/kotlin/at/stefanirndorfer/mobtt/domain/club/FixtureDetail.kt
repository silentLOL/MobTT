package at.stefanirndorfer.mobtt.domain.club

/**
 * A Fixture with the Matches it is made up of. [matches] is empty until the Fixture is played, and [address] is
 * null when XTTV does not give one.
 */
data class FixtureDetail(
    val fixture: Fixture,
    val address: VenueAddress?,
    val matches: List<Match>,
)

/** Every part is blank-free and null when XTTV leaves it out. */
data class VenueAddress(val street: String?, val postalCode: String?, val city: String?)

/** [number] is the Match's order in the Fixture. [score] is null while the Match has no result. */
data class Match(
    val number: Int,
    val kind: MatchKind,
    val homePlayers: List<String>,
    val awayPlayers: List<String>,
    val score: MatchScore?,
)

enum class MatchKind { Singles, Doubles }

/** Counts Sets won, e.g. 3:1. */
data class MatchScore(val homeSetsWon: Int, val awaySetsWon: Int)
