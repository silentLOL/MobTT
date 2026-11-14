package at.stefanirndorfer.mobtt.feature.players.impl.presentation.playerdetail.models

sealed interface PlayerDetailEvent {
    data class OpenClub(val clubId: String) : PlayerDetailEvent

    data class OpenTeam(val teamId: String) : PlayerDetailEvent

    /** Opened in the in-app browser, which needs a `Context`. */
    data class OpenWebPage(val url: String) : PlayerDetailEvent
}
