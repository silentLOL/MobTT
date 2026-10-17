package at.stefanirndorfer.batmate.feature.club.impl.presentation.club.models

sealed interface ClubAction {
    data object ChooseClicked : ClubAction

    data object RetryClicked : ClubAction
}
