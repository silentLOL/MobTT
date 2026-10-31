package at.stefanirndorfer.mobtt.domain.user

import at.stefanirndorfer.mobtt.domain.club.NextFixture
import at.stefanirndorfer.mobtt.domain.club.Team

data class UpcomingFixture(val team: Team, val next: NextFixture)

sealed interface UpcomingFixtures {
    /** The user has chosen neither My Player nor My Club, so there is nothing to show. */
    data object NoSelection : UpcomingFixtures

    data object Unavailable : UpcomingFixtures

    /** Scheduled Fixtures by date, then Teams with a free week. */
    data class Loaded(val entries: List<UpcomingFixture>) : UpcomingFixtures
}
