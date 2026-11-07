package at.stefanirndorfer.mobtt.feature.club.impl.presentation.fixturelink

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.stefanirndorfer.mobtt.core.analytics.AnalyticsStart
import at.stefanirndorfer.mobtt.core.analytics.AnalyticsTracker
import at.stefanirndorfer.mobtt.core.analytics.NoOpAnalyticsTracker
import at.stefanirndorfer.mobtt.core.analytics.fixtureLinkOpened
import at.stefanirndorfer.mobtt.domain.club.FixtureLinks
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.fixturelink.models.FixtureLinkAction
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.fixturelink.models.FixtureLinkEvent
import co.touchlab.kermit.Logger
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/** Turns a link the app was opened with into the Fixture it names; anything that is not a Fixture link is ignored. */
class FixtureLinkViewModel(
    private val fixtureLinks: FixtureLinks,
    private val analytics: AnalyticsTracker = NoOpAnalyticsTracker,
) : ViewModel() {
    private val eventChannel = Channel<FixtureLinkEvent>()
    val events: Flow<FixtureLinkEvent> = eventChannel.receiveAsFlow()

    fun onAction(action: FixtureLinkAction) {
        when (action) {
            is FixtureLinkAction.LinkReceived -> open(action.url, action.coldStart)
        }
    }

    private fun open(url: String, coldStart: Boolean) {
        val fixtureId = fixtureLinks.fixtureIdOf(url)
        if (fixtureId == null) {
            logger.w { "Ignoring a link that is not a Fixture link" }
            return
        }
        logger.i { "Opening Fixture $fixtureId from a Fixture link" }
        val start = if (coldStart) {
            AnalyticsStart.COLD
        } else {
            AnalyticsStart.RUNNING
        }
        analytics.fixtureLinkOpened(start)
        viewModelScope.launch { eventChannel.send(FixtureLinkEvent.OpenFixture(fixtureId)) }
    }

    private companion object {
        val logger = Logger.withTag("FixtureLinkViewModel")
    }
}
