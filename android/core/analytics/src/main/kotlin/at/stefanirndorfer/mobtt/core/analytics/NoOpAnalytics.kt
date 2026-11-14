package at.stefanirndorfer.mobtt.core.analytics

/** Used in previews, tests, the `deb` flavor and builds without Firebase config. */
object NoOpAnalyticsTracker : AnalyticsTracker {
    override fun logEvent(name: String, params: Map<String, Any>) = Unit

    override fun setCollectionEnabled(enabled: Boolean) = Unit
}

object NoOpCrashReporter : CrashReporter {
    override fun recordNonFatal(throwable: Throwable) = Unit

    override fun log(message: String) = Unit

    override fun setCollectionEnabled(enabled: Boolean) = Unit
}
