package at.stefanirndorfer.batmate.feature.players.impl.presentation.players.models

sealed interface PlayersEvent {
    data class OpenPlayer(val playerId: String) : PlayersEvent

    /** The Player was made My Player, so the screen that asked for one is done. */
    data object Chosen : PlayersEvent
}
