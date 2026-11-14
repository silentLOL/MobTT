package at.stefanirndorfer.mobtt.core.analytics

import com.google.firebase.crashlytics.FirebaseCrashlytics

internal class FirebaseCrashReporter : CrashReporter {
    private val crashlytics = FirebaseCrashlytics.getInstance()

    // Off until Usage Consent allows it, so nothing logged before the answer is sent later.
    @Volatile
    private var enabled = false

    override fun recordNonFatal(throwable: Throwable) {
        if (enabled) {
            crashlytics.recordException(throwable)
        }
    }

    override fun log(message: String) {
        if (enabled) {
            crashlytics.log(message)
        }
    }

    override fun setCollectionEnabled(enabled: Boolean) {
        this.enabled = enabled
        crashlytics.isCrashlyticsCollectionEnabled = enabled
        if (!enabled) {
            crashlytics.deleteUnsentReports()
        }
    }
}
