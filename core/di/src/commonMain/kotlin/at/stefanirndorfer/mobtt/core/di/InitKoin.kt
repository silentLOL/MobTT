package at.stefanirndorfer.mobtt.core.di

import at.stefanirndorfer.mobtt.core.network.AppConfig
import at.stefanirndorfer.mobtt.core.network.networkModule
import at.stefanirndorfer.mobtt.core.platform.platformModule
import at.stefanirndorfer.mobtt.core.preferences.preferencesModule
import at.stefanirndorfer.mobtt.core.xttv.xttvModule
import at.stefanirndorfer.mobtt.domain.app.di.appDomainModule
import at.stefanirndorfer.mobtt.domain.club.di.clubDomainModule
import at.stefanirndorfer.mobtt.domain.user.di.userDomainModule
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
