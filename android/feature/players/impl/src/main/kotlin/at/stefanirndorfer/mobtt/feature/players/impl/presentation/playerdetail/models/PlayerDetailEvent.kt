package at.stefanirndorfer.mobtt.feature.players.impl.presentation.playerdetail.models

sealed interface PlayerDetailEvent {
    data class OpenClub(val clubId: String) : PlayerDetailEvent

    data class OpenTeam(val teamId: String) : PlayerDetailEvent
}
