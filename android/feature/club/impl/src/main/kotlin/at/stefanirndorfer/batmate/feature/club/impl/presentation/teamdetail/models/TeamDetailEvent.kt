package at.stefanirndorfer.batmate.feature.club.impl.presentation.teamdetail.models

sealed interface TeamDetailEvent {
    data class OpenLeague(val leagueId: String) : TeamDetailEvent
}
