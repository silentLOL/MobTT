package at.stefanirndorfer.mobtt.core.analytics

/** Sends interaction events to analytics. Parameters carry IDs and enum values only, never names or free text. */
interface AnalyticsTracker {
    fun logEvent(name: String, params: Map<String, Any> = emptyMap())

    /** Switches collection on or off; driven by Usage Consent. */
    fun setCollectionEnabled(enabled: Boolean)
}
