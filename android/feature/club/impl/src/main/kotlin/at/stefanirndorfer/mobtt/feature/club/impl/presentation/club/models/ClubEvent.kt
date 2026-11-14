package at.stefanirndorfer.mobtt.feature.club.impl.presentation.club.models

sealed interface ClubEvent {
    data class OpenTeam(val teamId: String) : ClubEvent

    data class OpenPlayer(val playerId: String) : ClubEvent
}
