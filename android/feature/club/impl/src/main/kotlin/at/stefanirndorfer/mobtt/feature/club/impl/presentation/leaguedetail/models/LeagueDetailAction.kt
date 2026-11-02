package at.stefanirndorfer.mobtt.feature.club.impl.presentation.leaguedetail.models

sealed interface LeagueDetailAction {
    data object RetryClicked : LeagueDetailAction

    data class OnlyTeamFixturesToggled(val enabled: Boolean) : LeagueDetailAction

    data class TeamClicked(val teamId: String) : LeagueDetailAction

    data class FixtureInfoClicked(val fixtureId: String) : LeagueDetailAction
}
