package at.stefanirndorfer.mobtt.feature.players.impl.presentation.players.models

sealed interface PlayersAction {
    data object AssociationRowClicked : PlayersAction

    data object ClubRowClicked : PlayersAction

    data class PickerQueryChanged(val query: String) : PlayersAction

    data class PickerEntryClicked(val id: String) : PlayersAction

    data object PickerDismissed : PlayersAction

    data class QueryChanged(val query: String) : PlayersAction

    data class PlayerClicked(val playerId: String) : PlayersAction

    data object RetryClicked : PlayersAction
}
