package at.stefanirndorfer.mobtt.domain.user

import at.stefanirndorfer.mobtt.domain.club.Association
import at.stefanirndorfer.mobtt.domain.club.Club
import at.stefanirndorfer.mobtt.domain.player.Player

sealed interface UserContext {
    val club: Club?
    val association: Association?

    data object None : UserContext {
        override val club: Club? = null
        override val association: Association? = null
    }

    data class MyPlayer(
        val player: Player,
        override val club: Club,
        override val association: Association,
    ) : UserContext

    data class MyClub(
        override val club: Club,
        override val association: Association,
    ) : UserContext
}
