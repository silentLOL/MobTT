package at.stefanirndorfer.mobtt.feature.club.impl.presentation.leaguetab.models

sealed interface LeagueTabAction {
    data object RetryClicked : LeagueTabAction

    data object ChoosePlayerClicked : LeagueTabAction

    data class MyPlayerClicked(val playerId: String) : LeagueTabAction

    data class TeamCardClicked(val leagueId: String, val teamId: String) : LeagueTabAction
}
