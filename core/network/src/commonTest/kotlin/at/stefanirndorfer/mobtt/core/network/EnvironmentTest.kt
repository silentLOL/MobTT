package at.stefanirndorfer.mobtt.core.network

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class EnvironmentTest {
    @Test
    fun everyEnvironmentResolvesFromItsKey() {
        Environment.entries.forEach { assertEquals(it, Environment.fromKey(it.key)) }
    }

    @Test
    fun unknownKeyFailsFast() {
        assertFailsWith<IllegalStateException> { Environment.fromKey("staging") }
    }
}
