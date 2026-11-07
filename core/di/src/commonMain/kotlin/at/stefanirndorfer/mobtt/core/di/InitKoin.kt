package at.stefanirndorfer.mobtt.core.di

import at.stefanirndorfer.mobtt.core.network.AppConfig
import at.stefanirndorfer.mobtt.core.network.Environment
import at.stefanirndorfer.mobtt.core.network.networkModule
import at.stefanirndorfer.mobtt.core.platform.platformModule
import at.stefanirndorfer.mobtt.core.preferences.preferencesModule
import at.stefanirndorfer.mobtt.core.ratingscentral.ratingsCentralModule
import at.stefanirndorfer.mobtt.core.xttv.xttvModule
import at.stefanirndorfer.mobtt.domain.app.di.appDomainModule
import at.stefanirndorfer.mobtt.domain.club.DataUnavailable
import at.stefanirndorfer.mobtt.domain.club.FixtureLinks
import at.stefanirndorfer.mobtt.domain.club.di.clubDomainModule
import at.stefanirndorfer.mobtt.domain.rating.RatingHistory
import at.stefanirndorfer.mobtt.domain.rating.RatingHistoryAvailability
import at.stefanirndorfer.mobtt.domain.rating.RatingHistoryExplanation
import at.stefanirndorfer.mobtt.domain.rating.RatingHistoryRepository
import at.stefanirndorfer.mobtt.domain.rating.di.ratingDomainModule
import at.stefanirndorfer.mobtt.domain.user.SupportLink
import at.stefanirndorfer.mobtt.domain.user.di.userDomainModule
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

private val sharedModules = listOf(
    platformModule,
    preferencesModule,
    appDomainModule,
    clubDomainModule,
    userDomainModule,
    ratingDomainModule,
)

/** Shared settings derived from the build's [AppConfig]. */
private fun configModule(config: AppConfig): Module = module {
    single { RatingHistoryAvailability(config.ratingHistoryEnabled) }
    single { RatingHistoryExplanation(config.ratingsCentralExplanationUrl) }
    single { FixtureLinks(config.linksHost, config.fixtureSharingEnabled) }
    single { SupportLink(config.supportUrl) }
}

/**
 * Bindings an app module may replace: bound before the app's own modules, so a debug build's fake wins over them.
 * Without a source of Rating histories, every request is unavailable.
 */
private val fallbackModule = module {
    single<RatingHistoryRepository> { NoRatingHistories }
}

private object NoRatingHistories : RatingHistoryRepository {
    override suspend fun getRatingHistory(ratingsCentralId: String): RatingHistory = throw DataUnavailable("No source of Rating histories is bound")
}

/**
 * The repositories that read XTTV, in every environment, and Ratings Central, in every environment but `deb`, which
 * keeps its fake Rating histories. Bound after the app's own modules so these win over any fake the app binds.
 */
internal fun repositoryModules(environment: Environment): List<Module> = listOfNotNull(
    xttvModule,
    ratingsCentralModule.takeIf { environment != Environment.Deb },
)

fun initKoin(config: AppConfig, appDeclaration: KoinAppDeclaration = {}): KoinApplication = startKoin {
    modules(fallbackModule)
    appDeclaration()
    modules(sharedModules + configModule(config) + networkModule(config) + repositoryModules(config.environment))
}
