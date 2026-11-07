package at.stefanirndorfer.mobtt.feature.club.impl.presentation.fixturelink.models

sealed interface FixtureLinkAction {
    /** The app was opened with [url]; [coldStart] when the link started it rather than reaching it while running. */
    data class LinkReceived(val url: String, val coldStart: Boolean) : FixtureLinkAction
}
