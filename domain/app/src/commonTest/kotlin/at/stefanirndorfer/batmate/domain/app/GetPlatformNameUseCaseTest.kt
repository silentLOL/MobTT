package at.stefanirndorfer.batmate.domain.app

import at.stefanirndorfer.batmate.core.platform.Platform
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetPlatformNameUseCaseTest {
    @Test
    fun returnsNameOfCurrentPlatform() = runTest {
        val platform = object : Platform {
            override val name = "Fake OS 1"
        }

        val result = GetPlatformNameUseCase(platform)()

        assertEquals("Fake OS 1", result)
    }
}
