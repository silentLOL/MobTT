package at.stefanirndorfer.mobtt.feature.club.impl.presentation.leaguedetail.models

sealed interface LeagueDetailEvent {
    data class OpenTeam(val teamId: String) : LeagueDetailEvent
}
