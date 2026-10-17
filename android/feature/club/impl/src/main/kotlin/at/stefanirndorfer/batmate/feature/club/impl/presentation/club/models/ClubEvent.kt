package at.stefanirndorfer.batmate.feature.club.impl.presentation.club.models

sealed interface ClubEvent {
    data object OpenSettings : ClubEvent

    data class OpenTeam(val teamId: String) : ClubEvent
}
