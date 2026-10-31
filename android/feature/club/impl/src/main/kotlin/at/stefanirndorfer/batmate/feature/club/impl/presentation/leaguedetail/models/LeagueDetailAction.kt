package at.stefanirndorfer.batmate.feature.club.impl.presentation.leaguedetail.models

sealed interface LeagueDetailAction {
    data object RetryClicked : LeagueDetailAction
}
