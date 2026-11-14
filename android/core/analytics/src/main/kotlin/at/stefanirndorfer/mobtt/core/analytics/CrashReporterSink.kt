package at.stefanirndorfer.mobtt.core.analytics

import at.stefanirndorfer.mobtt.core.network.CrashLogWriter
import at.stefanirndorfer.mobtt.core.network.CrashSink
import at.stefanirndorfer.mobtt.core.network.SecretRedactor
import co.touchlab.kermit.LogWriter

/** The redacted description replaces the original message; the original stack trace is kept. */
private class RedactedException(description: String, original: Throwable) : Exception(description) {
    init {
        stackTrace = original.stackTrace
    }
}

private class CrashReporterSink(private val crashReporter: () -> CrashReporter) : CrashSink {
    override fun recordNonFatal(throwable: Throwable, description: String) {
        crashReporter().recordNonFatal(RedactedException(description, throwable))
    }

    override fun log(message: String) {
        crashReporter().log(message)
    }
}

/**
 * The Kermit writer for shared-code logs; add it with `Logger.addLogWriter` in `initKoin`'s `appDeclaration`.
 * [crashReporter] is resolved on use, so the writer can be created before the Koin modules are loaded.
 */
fun crashLogWriter(xttvApiKey: String, crashReporter: () -> CrashReporter): LogWriter = CrashLogWriter(CrashReporterSink(crashReporter), SecretRedactor(listOf(xttvApiKey)))
