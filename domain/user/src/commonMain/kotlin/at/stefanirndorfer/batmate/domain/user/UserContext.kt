package at.stefanirndorfer.batmate.domain.user

import at.stefanirndorfer.batmate.domain.club.Association
import at.stefanirndorfer.batmate.domain.club.Club
import at.stefanirndorfer.batmate.domain.player.Player

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
