package at.stefanirndorfer.mobtt.feature.club.impl.presentation.fixturedetail.models

import at.stefanirndorfer.mobtt.domain.club.FixtureDetail

sealed interface FixtureDetailEvent {
    data class OpenTeam(val teamId: String) : FixtureDetailEvent

    /** Opens a maps app searching for [searchText], the Venue's name and address. */
    data class OpenVenue(val searchText: String) : FixtureDetailEvent

    /** Opens the share sheet for [detail], ending with its Fixture [link], or without one while there is no links domain. */
    data class Share(val detail: FixtureDetail, val link: String?) : FixtureDetailEvent
}
