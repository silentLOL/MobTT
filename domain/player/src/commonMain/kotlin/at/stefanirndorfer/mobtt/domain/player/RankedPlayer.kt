package at.stefanirndorfer.mobtt.domain.player

/**
 * One row of a League's Player ranking. [rank] is null for a Player below the League's minimum number of games.
 * A Player listed for two Teams of the same League has a row for each, as XTTV publishes them.
 */
data class RankedPlayer(
    val player: Player,
    val rank: Int?,
    val teamId: String,
    val gamesPlayed: Int,
    val matchesWon: Int,
    val matchesLost: Int,
)
