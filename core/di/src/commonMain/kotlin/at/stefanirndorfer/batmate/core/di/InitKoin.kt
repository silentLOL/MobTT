package at.stefanirndorfer.batmate.core.di

import at.stefanirndorfer.batmate.core.network.AppConfig
import at.stefanirndorfer.batmate.core.network.Environment
import at.stefanirndorfer.batmate.core.network.networkModule
import at.stefanirndorfer.batmate.core.platform.platformModule
import at.stefanirndorfer.batmate.core.preferences.preferencesModule
import at.stefanirndorfer.batmate.core.xttv.xttvModule
import at.stefanirndorfer.batmate.domain.app.di.appDomainModule
import at.stefanirndorfer.batmate.domain.club.di.clubDomainModule
import at.stefanirndorfer.batmate.domain.user.di.userDomainModule
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

private val sharedModules = listOf(
    platformModule,
    preferencesModule,
    appDomainModule,
    clubDomainModule,
    userDomainModule,
)

/** `deb` gets its repositories from the apps (the fake directory); every other environment reads XTTV. */
fun initKoin(config: AppConfig, appDeclaration: KoinAppDeclaration = {}): KoinApplication = startKoin {
    appDeclaration()
    modules(sharedModules + networkModule(config))
    if (config.environment != Environment.Deb) {
        modules(xttvModule)
    }
}
