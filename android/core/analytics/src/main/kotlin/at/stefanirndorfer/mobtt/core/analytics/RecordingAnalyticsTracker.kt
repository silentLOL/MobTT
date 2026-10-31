package at.stefanirndorfer.mobtt.core.analytics

data class LoggedEvent(val name: String, val params: Map<String, Any>)

/** Keeps what was logged, for ViewModel tests. */
class RecordingAnalyticsTracker : AnalyticsTracker {
    val events = mutableListOf<LoggedEvent>()

    override fun logEvent(name: String, params: Map<String, Any>) {
        events += LoggedEvent(name, params)
    }

    override fun setCollectionEnabled(enabled: Boolean) = Unit
}
