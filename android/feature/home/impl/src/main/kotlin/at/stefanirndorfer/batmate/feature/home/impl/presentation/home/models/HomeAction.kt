package at.stefanirndorfer.batmate.feature.home.impl.presentation.home.models

import at.stefanirndorfer.batmate.domain.user.Favorite

sealed interface HomeAction {
    data object ChooseClicked : HomeAction

    data object AddFavoriteClicked : HomeAction

    data object ViewClubClicked : HomeAction

    data class FavoriteClubClicked(val clubId: String) : HomeAction

    data class RemoveFavoriteClicked(val favorite: Favorite) : HomeAction

    data class PickerQueryChanged(val query: String) : HomeAction

    data class PickerPlayerClicked(val id: String) : HomeAction

    data class PickerClubClicked(val id: String) : HomeAction

    data object PickerDismissed : HomeAction
}
