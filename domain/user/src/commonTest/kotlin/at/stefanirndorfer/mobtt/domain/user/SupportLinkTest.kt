package at.stefanirndorfer.mobtt.domain.user

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SupportLinkTest {
    @Test
    fun anHttpsAddressIsKept() {
        assertEquals("https://example.org/coffee", SupportLink("  https://example.org/coffee ").url)
    }

    @Test
    fun anythingElseIsTreatedAsUnset() {
        listOf(null, "", "https:", "https://", "https:// ", "http://example.org", "javascript:alert(1)", "file:///sdcard/x", "intent://x#Intent;end").forEach {
            assertNull(SupportLink(it).url, it)
        }
    }
}
