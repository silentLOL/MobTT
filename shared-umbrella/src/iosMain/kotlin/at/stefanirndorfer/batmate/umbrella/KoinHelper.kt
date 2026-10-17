package at.stefanirndorfer.batmate.umbrella

import at.stefanirndorfer.batmate.core.di.initKoin
import at.stefanirndorfer.batmate.core.network.AppConfig
import at.stefanirndorfer.batmate.core.network.Environment
import at.stefanirndorfer.batmate.domain.app.GetPlatformNameUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object KoinHelper : KoinComponent {
    fun start(environmentKey: String) {
        initKoin(AppConfig(Environment.fromKey(environmentKey)))
    }

    fun getPlatformNameUseCase(): GetPlatformNameUseCase {
        val useCase: GetPlatformNameUseCase by inject()
        return useCase
    }
}
