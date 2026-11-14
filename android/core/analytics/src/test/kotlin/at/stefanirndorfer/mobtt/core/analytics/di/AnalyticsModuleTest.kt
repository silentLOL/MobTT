package at.stefanirndorfer.mobtt.core.analytics.di

import at.stefanirndorfer.mobtt.core.analytics.AnalyticsTracker
import at.stefanirndorfer.mobtt.core.analytics.NoOpAnalyticsTracker
import at.stefanirndorfer.mobtt.core.analytics.RecordingAnalyticsTracker
import at.stefanirndorfer.mobtt.core.analytics.screenView
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class AnalyticsModuleTest {
    @Test
    fun `deb records nothing because its tracker is the no-op`() {
        val firebase = RecordingAnalyticsTracker()

        val tracker: AnalyticsTracker = chooseTracker(enabled = false) { firebase }
        tracker.screenView("home")

        assertSame(NoOpAnalyticsTracker, tracker)
        assertEquals(emptyList(), firebase.events)
    }

    @Test
    fun `dev beta and prod use the Firebase tracker`() {
        val firebase = RecordingAnalyticsTracker()

        chooseTracker(enabled = true) { firebase }.screenView("home")

        assertEquals(1, firebase.events.size)
    }
}
