package at.stefanirndorfer.mobtt.core.analytics

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class SearchEventsTest {
    private val tracker = RecordingAnalyticsTracker()

    private fun TestScope.events() = SearchEvents(this, tracker, AnalyticsScreen.HOME, settleMillis = 100)

    @Test
    fun `typing sends one event with the query length and not the text`() = runTest {
        val events = events()

        events.onQueryChanged("o")
        advanceTimeBy(50)
        events.onQueryChanged("olympic")
        advanceUntilIdle()

        val logged = tracker.events.single()
        assertEquals(AnalyticsEvent.SEARCH_PERFORMED, logged.name)
        assertEquals(mapOf(AnalyticsParam.SCREEN to "home", AnalyticsParam.QUERY_LENGTH to 7L), logged.params)
    }

    @Test
    fun `a blank query sends nothing`() = runTest {
        val events = events()

        events.onQueryChanged("ab")
        events.onQueryChanged("  ")
        advanceUntilIdle()

        assertEquals(emptyList(), tracker.events)
    }
}
