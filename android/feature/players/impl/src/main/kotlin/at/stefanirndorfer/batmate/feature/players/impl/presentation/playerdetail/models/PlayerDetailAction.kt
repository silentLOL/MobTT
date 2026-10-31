package at.stefanirndorfer.batmate.feature.players.impl.presentation.playerdetail.models

sealed interface PlayerDetailAction {
    data object FavoriteClicked : PlayerDetailAction

    data object ClubClicked : PlayerDetailAction

    data class TeamClicked(val teamId: String) : PlayerDetailAction
}
