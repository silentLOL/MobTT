package at.stefanirndorfer.batmate.core.di

import at.stefanirndorfer.batmate.core.network.AppConfig
import at.stefanirndorfer.batmate.core.network.networkModule
import at.stefanirndorfer.batmate.core.platform.platformModule
import at.stefanirndorfer.batmate.core.preferences.preferencesModule
import at.stefanirndorfer.batmate.core.xttv.xttvModule
import at.stefanirndorfer.batmate.domain.app.di.appDomainModule
import at.stefanirndorfer.batmate.domain.club.di.clubDomainModule
import at.stefanirndorfer.batmate.domain.user.di.userDomainModule
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration

private val sharedModules = listOf(
    platformModule,
    preferencesModule,
    appDomainModule,
    clubDomainModule,
    userDomainModule,
)

/**
 * The repositories that read XTTV, in every environment. Bound after the app's own modules so XTTV wins over any
 * fake the app still binds for data XTTV does not serve yet.
 */
internal fun repositoryModules(): List<Module> = listOf(xttvModule)

fun initKoin(config: AppConfig, appDeclaration: KoinAppDeclaration = {}): KoinApplication = startKoin {
    appDeclaration()
    modules(sharedModules + networkModule(config) + repositoryModules())
}
