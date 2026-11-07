package at.stefanirndorfer.mobtt.domain.user

import at.stefanirndorfer.mobtt.domain.club.Team

/** The Player's Club for the Player screen. [associationCode] is null when XTTV no longer resolves the Association. */
data class PlayerClub(val clubName: String, val associationCode: String?)

/**
 * [teams] are the Teams of the Player's Club whose lineup lists them, empty when none does. [rcPoints] and [ageGroup]
 * come from that lineup entry, so they are null when no lineup lists the Player or XTTV gives none.
 */
data class PlayerTeams(
    val teams: List<Team> = emptyList(),
    val rcPoints: Int? = null,
    val ageGroup: String? = null,
)
