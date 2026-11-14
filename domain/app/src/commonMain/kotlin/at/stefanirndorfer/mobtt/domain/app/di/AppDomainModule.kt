package at.stefanirndorfer.mobtt.domain.app.di

import at.stefanirndorfer.mobtt.domain.app.GetPlatformNameUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val appDomainModule = module {
    factoryOf(::GetPlatformNameUseCase)
}
