package at.stefanirndorfer.batmate.feature.players.impl.presentation.players.models

sealed interface PlayersEvent {
    data class OpenPlayer(val playerId: String) : PlayersEvent
}
