package at.stefanirndorfer.batmate.feature.players.impl.presentation.playerdetail.models

sealed interface PlayerDetailAction {
    data object FavoriteClicked : PlayerDetailAction
}
