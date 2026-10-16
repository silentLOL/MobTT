package at.stefanirndorfer.batmate.core.di

import at.stefanirndorfer.batmate.core.platform.platformModule
import at.stefanirndorfer.batmate.domain.app.di.appDomainModule
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

private val sharedModules = listOf(
    platformModule,
    appDomainModule,
)

fun initKoin(appDeclaration: KoinAppDeclaration = {}): KoinApplication {
    return startKoin {
        appDeclaration()
        modules(sharedModules)
    }
}
