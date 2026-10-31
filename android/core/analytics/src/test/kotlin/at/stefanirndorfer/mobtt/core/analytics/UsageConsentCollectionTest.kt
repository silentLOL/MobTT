package at.stefanirndorfer.mobtt.core.analytics

import at.stefanirndorfer.mobtt.domain.user.ObserveUsageConsentUseCase
import at.stefanirndorfer.mobtt.domain.user.UsageConsent
import at.stefanirndorfer.mobtt.domain.user.UsageConsentRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.plus
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class UsageConsentCollectionTest {
    private val consent = MutableStateFlow(UsageConsent.UNANSWERED)
    private val tracker = RecordingTracker()
    private val crashReporter = RecordingCrashReporter()

    private fun TestScope.start() = UsageConsentCollection(
        ObserveUsageConsentUseCase(
            object : UsageConsentRepository {
                override fun observeConsent(): Flow<UsageConsent> = consent

                override suspend fun saveConsent(consent: UsageConsent) = Unit
            },
        ),
        tracker,
        crashReporter,
    ).start(backgroundScope + UnconfinedTestDispatcher(testScheduler))

    @Test
    fun `collection is off until the user answers`() = runTest {
        start()
        assertEquals(listOf(false), tracker.states)
        assertEquals(listOf(false), crashReporter.states)
    }

    @Test
    fun `collection follows allowing and declining`() = runTest {
        start()
        consent.value = UsageConsent.ALLOWED
        consent.value = UsageConsent.DECLINED
        assertEquals(listOf(false, true, false), tracker.states)
        assertEquals(listOf(false, true, false), crashReporter.states)
    }

    @Test
    fun `declining without having allowed does not toggle again`() = runTest {
        start()
        consent.value = UsageConsent.DECLINED
        assertEquals(listOf(false), tracker.states)
    }
}

private class RecordingTracker : AnalyticsTracker {
    val states = mutableListOf<Boolean>()

    override fun logEvent(name: String, params: Map<String, Any>) = Unit

    override fun setCollectionEnabled(enabled: Boolean) {
        states += enabled
    }
}

private class RecordingCrashReporter : CrashReporter {
    val states = mutableListOf<Boolean>()

    override fun recordNonFatal(throwable: Throwable) = Unit

    override fun log(message: String) = Unit

    override fun setCollectionEnabled(enabled: Boolean) {
        states += enabled
    }
}
