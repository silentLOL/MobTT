package at.stefanirndorfer.mobtt.core.network

import co.touchlab.kermit.Severity
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CrashLogWriterTest {
    private val sink = RecordingSink()
    private val writer = CrashLogWriter(sink, SecretRedactor(listOf(KEY)))

    @Test
    fun `an error with a throwable is a non-fatal`() {
        val error = IllegalStateException("boom")

        writer.log(Severity.Error, "XTTV failed", "Xttv", error)

        assertEquals(listOf(error), sink.nonFatals.map { it.first })
        assertEquals("IllegalStateException: boom", sink.nonFatals.single().second)
    }

    @Test
    fun `lower severities and errors without a throwable are breadcrumbs only`() {
        writer.log(Severity.Info, "loaded", "Xttv", null)
        writer.log(Severity.Warn, "slow", "Xttv", IllegalStateException("x"))
        writer.log(Severity.Error, "failed", "Xttv", null)

        assertEquals(3, sink.lines.size)
        assertTrue(sink.nonFatals.isEmpty())
    }

    @Test
    fun `the api key never reaches the sink`() {
        val url = "https://oettv.xttv.at/xml/v1/getClubs.php?federationId=1&apikey=$KEY"
        val error = RuntimeException("Connect timeout has expired [url=$url, connect_timeout=1000 ms]", RuntimeException(KEY))

        writer.log(Severity.Error, "Request to $url failed", "Http", error)

        assertNothingLeaks(KEY)
        assertTrue(sink.nonFatals.single().second.contains("apikey=<redacted>"))
    }

    @Test
    fun `tokens and bearer headers are redacted even when the key is not known`() {
        val error = RuntimeException("401 for Authorization: Bearer abc.DEF-123 at /cb?access_token=secret1&code_verifier=secret2")

        writer.log(Severity.Error, "auth failed: refresh_token=secret3", "Auth", error)

        assertNothingLeaks("abc.DEF-123", "secret1", "secret2", "secret3")
    }

    private fun assertNothingLeaks(vararg secrets: String) {
        val everything = sink.lines + sink.nonFatals.map { it.second }
        for (secret in secrets) {
            assertFalse(everything.any { secret in it }, "leaked $secret in $everything")
        }
    }

    private class RecordingSink : CrashSink {
        val lines = mutableListOf<String>()
        val nonFatals = mutableListOf<Pair<Throwable, String>>()

        override fun recordNonFatal(throwable: Throwable, description: String) {
            nonFatals += throwable to description
        }

        override fun log(message: String) {
            lines += message
        }
    }

    private companion object {
        const val KEY = "k3y-VALUE-9f8e7d"
    }
}
