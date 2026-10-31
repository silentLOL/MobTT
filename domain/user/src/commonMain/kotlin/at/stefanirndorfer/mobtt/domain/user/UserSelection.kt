package at.stefanirndorfer.mobtt.domain.user

sealed interface UserSelection {
    data object None : UserSelection

    data class MyPlayer(val playerId: String) : UserSelection

    data class MyClub(val clubId: String) : UserSelection
}
