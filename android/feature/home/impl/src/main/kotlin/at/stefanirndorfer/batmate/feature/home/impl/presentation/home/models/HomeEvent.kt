package at.stefanirndorfer.batmate.feature.home.impl.presentation.home.models

sealed interface HomeEvent {
    data object OpenChoosePlayer : HomeEvent

    data object OpenChooseClub : HomeEvent

    data class OpenClub(val clubId: String) : HomeEvent

    data class OpenPlayer(val playerId: String) : HomeEvent

    data class OpenTeam(val teamId: String) : HomeEvent
}
