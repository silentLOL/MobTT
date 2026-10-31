package at.stefanirndorfer.mobtt.umbrella

import at.stefanirndorfer.mobtt.core.network.CrashLogWriter
import at.stefanirndorfer.mobtt.core.network.CrashSink
import at.stefanirndorfer.mobtt.core.network.SecretRedactor
import co.touchlab.kermit.LogWriter
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.setUnhandledExceptionHook
import kotlin.native.terminateWithUnhandledException

/** Implemented in Swift on top of Crashlytics. Frames are Kotlin stack trace lines, so reports show Kotlin symbols. */
interface IosCrashRecorder {
    fun recordNonFatal(name: String, reason: String, frames: List<String>)

    fun log(message: String)
}

@OptIn(ExperimentalNativeApi::class)
private class IosCrashSink(private val recorder: IosCrashRecorder) : CrashSink {
    override fun recordNonFatal(throwable: Throwable, description: String) {
        recorder.recordNonFatal(throwable::class.simpleName ?: "Throwable", description, throwable.getStackTrace().toList())
    }

    override fun log(message: String) {
        recorder.log(message)
    }
}

internal fun iosCrashLogWriter(recorder: IosCrashRecorder, xttvApiKey: String): LogWriter = CrashLogWriter(IosCrashSink(recorder), SecretRedactor(listOf(xttvApiKey)))

/**
 * Kotlin's default would print the exception and abort without telling Crashlytics. Record it with its Kotlin
 * frames first (the recorder drops it unless Usage Consent allows), then terminate as usual.
 */
@OptIn(ExperimentalNativeApi::class)
internal fun forwardUnhandledExceptions(recorder: IosCrashRecorder, xttvApiKey: String) {
    val sink = IosCrashSink(recorder)
    val redactor = SecretRedactor(listOf(xttvApiKey))
    setUnhandledExceptionHook { throwable ->
        sink.recordNonFatal(throwable, redactor.redact("${throwable::class.simpleName}: ${throwable.message.orEmpty()}"))
        terminateWithUnhandledException(throwable)
    }
}
