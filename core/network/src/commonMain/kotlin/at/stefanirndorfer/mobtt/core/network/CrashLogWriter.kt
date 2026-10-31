package at.stefanirndorfer.mobtt.core.network

import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Severity

private const val MAX_CAUSES = 5

/**
 * Sends shared-code logs to Crashlytics: `Error` with a throwable is a non-fatal, everything else a breadcrumb.
 * All text is redacted first. Whether anything is actually sent follows Usage Consent in the platform's sink.
 */
class CrashLogWriter(
    private val sink: CrashSink,
    private val redactor: SecretRedactor,
) : LogWriter() {
    override fun log(severity: Severity, message: String, tag: String, throwable: Throwable?) {
        val line = redactor.redact("${severity.name.first()}/$tag: $message")
        sink.log(line)
        if (severity == Severity.Error && throwable != null) {
            sink.recordNonFatal(throwable, describe(throwable))
        }
    }

    private fun describe(throwable: Throwable): String = generateSequence(throwable) { it.cause }
        .take(MAX_CAUSES)
        .joinToString(" <- ") { redactor.redact("${it::class.simpleName}: ${it.message.orEmpty()}") }
}
