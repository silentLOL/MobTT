package at.stefanirndorfer.mobtt.feature.club.impl.presentation.fixturelink.models

sealed interface FixtureLinkEvent {
    data class OpenFixture(val fixtureId: String) : FixtureLinkEvent
}
