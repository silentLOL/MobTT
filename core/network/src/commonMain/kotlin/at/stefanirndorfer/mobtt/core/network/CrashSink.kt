package at.stefanirndorfer.mobtt.core.network

/** Where [CrashLogWriter] hands errors and breadcrumbs; each platform adapts it to Crashlytics. */
interface CrashSink {
    /** [description] is the redacted `Type: message` of [throwable] and its causes; never use the raw messages. */
    fun recordNonFatal(throwable: Throwable, description: String)

    fun log(message: String)
}
