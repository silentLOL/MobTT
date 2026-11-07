package at.stefanirndorfer.mobtt.core.ratingscentral

import at.stefanirndorfer.mobtt.core.network.AppConfig
import at.stefanirndorfer.mobtt.core.network.Environment
import at.stefanirndorfer.mobtt.core.network.createHttpClient
import at.stefanirndorfer.mobtt.domain.rating.RatingHistoryRepository
import org.koin.dsl.module

/** Rating histories read from Ratings Central, one request at a time. */
val ratingsCentralModule = module {
    single<RatingHistoryRepository> {
        val config = get<AppConfig>()
        RatingsCentralHistories(
            createHttpClient(
                userAgent = "MobTT/${config.appVersion}",
                logRequests = config.environment != Environment.Prod,
                maxConcurrentRequests = 1,
            ),
            config.ratingsCentralHost,
        )
    }
}
