package at.stefanirndorfer.mobtt.domain.app

import at.stefanirndorfer.mobtt.core.platform.Platform

class GetPlatformNameUseCase(private val platform: Platform) {
    suspend operator fun invoke(): String = platform.name
}
