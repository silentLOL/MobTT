package at.stefanirndorfer.batmate.domain.app

import at.stefanirndorfer.batmate.core.platform.Platform

class GetPlatformNameUseCase(private val platform: Platform) {
    operator fun invoke(): String = platform.name
}
