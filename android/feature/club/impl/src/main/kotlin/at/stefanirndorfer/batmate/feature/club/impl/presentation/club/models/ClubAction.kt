package at.stefanirndorfer.batmate.feature.club.impl.presentation.club.models

import at.stefanirndorfer.batmate.domain.club.LineupPlayer

sealed interface ClubAction {
    data object AssociationRowClicked : ClubAction

    data object ClubRowClicked : ClubAction

    data class PickerQueryChanged(val query: String) : ClubAction

    data class PickerEntryClicked(val id: String) : ClubAction

    data object PickerDismissed : ClubAction

    data object RetryClicked : ClubAction

    data object FavoriteClicked : ClubAction

    data class TeamClicked(val teamId: String) : ClubAction

    data class PlayerClicked(val player: LineupPlayer) : ClubAction
}
