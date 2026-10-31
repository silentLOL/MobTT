package at.stefanirndorfer.batmate.domain.user

import at.stefanirndorfer.batmate.domain.club.Team
import at.stefanirndorfer.batmate.domain.player.Player

/**
 * [clubName] and [associationName] are null when XTTV no longer resolves the Club or its Association. [teams] are the
 * Teams of the Player's Club whose lineup lists them, empty when none does or XTTV cannot say. [rcPoints] and
 * [ageGroup] come from that lineup entry, so they are null when no lineup lists the Player or XTTV gives none.
 */
data class PlayerDetail(
    val player: Player,
    val clubName: String?,
    val associationName: String?,
    val teams: List<Team> = emptyList(),
    val rcPoints: Int? = null,
    val ageGroup: String? = null,
)
