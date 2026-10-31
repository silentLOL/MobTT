package at.stefanirndorfer.batmate.feature.home.impl.presentation.home.models

sealed interface HomeAction {
    data object ChooseClicked : HomeAction

    data object AddFavoriteClicked : HomeAction

    data class HeroPlayerClicked(val playerId: String) : HomeAction

    data class HeroClubClicked(val clubId: String) : HomeAction

    data class FavoriteClubClicked(val clubId: String) : HomeAction

    data class FavoritePlayerClicked(val playerId: String) : HomeAction

    data class FavoriteTeamClicked(val teamId: String) : HomeAction

    data class PickerQueryChanged(val query: String) : HomeAction

    data object PickerBrowsePlayersClicked : HomeAction

    /** An Association, League or Player entry of the drill-down; a Player entry toggles the Favorite. */
    data class PickerPlayerClicked(val id: String) : HomeAction

    data object PickerBackClicked : HomeAction

    data class PickerClubClicked(val id: String) : HomeAction

    data object PickerDismissed : HomeAction
}
