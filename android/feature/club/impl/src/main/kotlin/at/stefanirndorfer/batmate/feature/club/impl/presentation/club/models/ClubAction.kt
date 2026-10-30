package at.stefanirndorfer.batmate.feature.club.impl.presentation.club.models

sealed interface ClubAction {
    data object ChooseClicked : ClubAction

    data object RetryClicked : ClubAction

    data object FavoriteClicked : ClubAction

    data class TeamClicked(val teamId: String) : ClubAction
}
