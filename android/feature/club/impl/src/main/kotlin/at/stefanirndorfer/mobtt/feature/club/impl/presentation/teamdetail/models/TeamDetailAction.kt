package at.stefanirndorfer.mobtt.feature.club.impl.presentation.teamdetail.models

import at.stefanirndorfer.mobtt.domain.club.LineupPlayer

sealed interface TeamDetailAction {
    data object RetryClicked : TeamDetailAction

    data object FavoriteClicked : TeamDetailAction

    data class LeagueClicked(val leagueId: String) : TeamDetailAction

    data class PlayerClicked(val player: LineupPlayer) : TeamDetailAction
}
