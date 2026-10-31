package at.stefanirndorfer.batmate.domain.user

import at.stefanirndorfer.batmate.domain.club.Team
import at.stefanirndorfer.batmate.domain.player.Player

/**
 * [clubName] and [associationName] are null when XTTV no longer resolves the Club or its Association. [teams] are the
 * Teams of the Player's Club whose lineup lists them, empty when none does or XTTV cannot say.
 */
data class PlayerDetail(
    val player: Player,
    val clubName: String?,
    val associationName: String?,
    val teams: List<Team> = emptyList(),
)
