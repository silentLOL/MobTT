package at.stefanirndorfer.mobtt.feature.club.impl.presentation.fixturedetail.models

import at.stefanirndorfer.mobtt.domain.club.FixtureDetail

sealed interface FixtureDetailEvent {
    data class OpenTeam(val teamId: String) : FixtureDetailEvent

    /** Opens the share sheet for [detail], ending with its Fixture [link]. */
    data class Share(val detail: FixtureDetail, val link: String) : FixtureDetailEvent
}
