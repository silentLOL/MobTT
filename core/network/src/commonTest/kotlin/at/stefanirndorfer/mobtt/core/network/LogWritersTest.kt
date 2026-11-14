package at.stefanirndorfer.mobtt.core.network

import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import kotlin.test.Test
import kotlin.test.assertEquals

class LogWritersTest {
    private val console = CountingWriter()
    private val crash = CountingWriter()

    @Test
    fun `prod logs only to the crash writer`() {
        Logger.setLogWriters(console)

        installLogWriters(Environment.Prod, crash)
        Logger.withTag("Test").i { "hello" }

        assertEquals(0, console.count)
        assertEquals(1, crash.count)
    }

    @Test
    fun `other environments log to the console and the crash writer`() {
        for (environment in Environment.entries - Environment.Prod) {
            Logger.setLogWriters(console)
            console.count = 0
            crash.count = 0

            installLogWriters(environment, crash)
            Logger.withTag("Test").i { "hello" }

            assertEquals(1, console.count, environment.key)
            assertEquals(1, crash.count, environment.key)
        }
    }

    private class CountingWriter : LogWriter() {
        var count = 0

        override fun log(severity: Severity, message: String, tag: String, throwable: Throwable?) {
            count++
        }
    }
}
