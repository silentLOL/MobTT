package at.stefanirndorfer.mobtt.feature.club.impl.presentation.fixturedetail.models

/** What the share sheet sends for a Fixture: [subject] for apps that use one (mail), and plain [text]. */
data class FixtureShare(val subject: String, val text: String)
