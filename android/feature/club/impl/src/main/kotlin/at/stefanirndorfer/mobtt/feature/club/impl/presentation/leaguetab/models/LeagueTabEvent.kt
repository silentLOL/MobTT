package at.stefanirndorfer.mobtt.feature.club.impl.presentation.leaguetab.models

sealed interface LeagueTabEvent {
    data object OpenChoosePlayer : LeagueTabEvent

    data class OpenPlayer(val playerId: String) : LeagueTabEvent

    data class OpenLeague(val leagueId: String, val teamId: String) : LeagueTabEvent
}
