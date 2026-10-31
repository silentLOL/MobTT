package at.stefanirndorfer.mobtt.core.analytics

/** Reports crashes and handled failures. Messages must never contain tokens or the XTTV API key. */
interface CrashReporter {
    fun recordNonFatal(throwable: Throwable)

    fun log(message: String)

    /** Switches collection on or off; driven by Usage Consent. */
    fun setCollectionEnabled(enabled: Boolean)
}
