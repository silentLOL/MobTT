package at.stefanirndorfer.batmate.feature.club.impl.presentation.teamdetail.models

import at.stefanirndorfer.batmate.domain.club.LineupPlayer

sealed interface TeamDetailAction {
    data object RetryClicked : TeamDetailAction

    data object FavoriteClicked : TeamDetailAction

    data class LeagueClicked(val leagueId: String) : TeamDetailAction

    data class PlayerClicked(val player: LineupPlayer) : TeamDetailAction
}
