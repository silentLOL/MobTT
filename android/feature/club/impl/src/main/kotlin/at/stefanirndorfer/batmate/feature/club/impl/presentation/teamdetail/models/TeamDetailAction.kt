package at.stefanirndorfer.batmate.feature.club.impl.presentation.teamdetail.models

sealed interface TeamDetailAction {
    data object RetryClicked : TeamDetailAction
}
