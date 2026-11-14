package at.stefanirndorfer.mobtt.core.platform

import org.koin.dsl.module

val platformModule = module {
    single<Platform> { currentPlatform() }
}

internal expect fun currentPlatform(): Platform
