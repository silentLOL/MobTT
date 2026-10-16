package at.stefanirndorfer.batmate.domain.app

import at.stefanirndorfer.batmate.core.platform.Platform

class GetPlatformNameUseCase(private val platform: Platform) {
    suspend operator fun invoke(): String = platform.name
}
