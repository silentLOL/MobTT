package at.stefanirndorfer.batmate.domain.user

sealed interface Favorite {
    val id: String

    data class Player(override val id: String) : Favorite

    data class Team(override val id: String) : Favorite

    data class Club(override val id: String) : Favorite
}
