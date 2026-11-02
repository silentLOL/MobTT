package at.stefanirndorfer.mobtt.feature.club.impl.presentation.fixturedetail.models

sealed interface FixtureDetailEvent {
    data class OpenTeam(val teamId: String) : FixtureDetailEvent
}
