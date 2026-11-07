package at.stefanirndorfer.mobtt.feature.club.impl.presentation

import at.stefanirndorfer.mobtt.core.analytics.LoggedEvent
import at.stefanirndorfer.mobtt.core.analytics.RecordingAnalyticsTracker
import at.stefanirndorfer.mobtt.domain.club.FixtureLinks
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.fixturelink.FixtureLinkViewModel
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.fixturelink.models.FixtureLinkAction
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.fixturelink.models.FixtureLinkEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class FixtureLinkViewModelTest {
    private val analytics = RecordingAnalyticsTracker()

    // Sharing is off: opening a link must work regardless.
    private val viewModel = FixtureLinkViewModel(FixtureLinks(host = "links.mobtt.example", sharingEnabled = false), analytics)

    @Before
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @After
    fun tearDown() = Dispatchers.resetMain()

    private fun TestScope.events(): List<FixtureLinkEvent> {
        val events = mutableListOf<FixtureLinkEvent>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.events.collect(events::add) }
        return events
    }

    @Test
    fun aFixtureLinkOpensItsFixtureAndReportsAColdStart() = runTest {
        val events = events()

        viewModel.onAction(FixtureLinkAction.LinkReceived("https://links.mobtt.example/f/464821", coldStart = true))

        assertEquals(listOf<FixtureLinkEvent>(FixtureLinkEvent.OpenFixture("464821")), events)
        assertEquals(listOf(LoggedEvent("fixture_link_opened", mapOf("start" to "cold"))), analytics.events)
    }

    @Test
    fun aFixtureLinkWhileRunningIsReportedAsRunning() = runTest {
        viewModel.onAction(FixtureLinkAction.LinkReceived("https://links.mobtt.example/f/464821", coldStart = false))

        assertEquals(listOf(LoggedEvent("fixture_link_opened", mapOf("start" to "running"))), analytics.events)
    }

    @Test
    fun anythingElseChangesNothing() = runTest {
        val events = events()

        viewModel.onAction(FixtureLinkAction.LinkReceived("https://links.mobtt.example/t/8846", coldStart = true))

        assertEquals(emptyList(), events)
        assertEquals(emptyList(), analytics.events)
    }
}
