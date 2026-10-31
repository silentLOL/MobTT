package at.stefanirndorfer.batmate.feature.club.impl.presentation.club.models

import at.stefanirndorfer.batmate.domain.club.LineupPlayer

sealed interface ClubAction {
    data object ChooseClicked : ClubAction

    data object RetryClicked : ClubAction

    data object FavoriteClicked : ClubAction

    data class TeamClicked(val teamId: String) : ClubAction

    data class PlayerClicked(val player: LineupPlayer) : ClubAction
}
