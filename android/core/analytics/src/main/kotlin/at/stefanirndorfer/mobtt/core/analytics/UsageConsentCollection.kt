package at.stefanirndorfer.mobtt.core.analytics

import at.stefanirndorfer.mobtt.domain.user.ObserveUsageConsentUseCase
import at.stefanirndorfer.mobtt.domain.user.UsageConsent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/** Keeps SDK collection in step with Usage Consent: off until the user allows it, off again when they decline. */
class UsageConsentCollection(
    private val observeConsent: ObserveUsageConsentUseCase,
    private val tracker: AnalyticsTracker,
    private val crashReporter: CrashReporter,
) {
    fun start(scope: CoroutineScope): Job = scope.launch {
        observeConsent()
            .map { it == UsageConsent.ALLOWED }
            .distinctUntilChanged()
            .collect { allowed ->
                tracker.setCollectionEnabled(allowed)
                crashReporter.setCollectionEnabled(allowed)
            }
    }
}
