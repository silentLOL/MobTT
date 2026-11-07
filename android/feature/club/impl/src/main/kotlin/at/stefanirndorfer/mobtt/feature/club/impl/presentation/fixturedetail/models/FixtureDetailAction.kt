package at.stefanirndorfer.mobtt.feature.club.impl.presentation.fixturedetail.models

sealed interface FixtureDetailAction {
    data object RetryClicked : FixtureDetailAction

    data class TeamClicked(val teamId: String) : FixtureDetailAction

    data object ShareClicked : FixtureDetailAction
}
