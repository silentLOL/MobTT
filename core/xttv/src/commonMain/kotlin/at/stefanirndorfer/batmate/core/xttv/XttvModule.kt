package at.stefanirndorfer.batmate.core.xttv

import at.stefanirndorfer.batmate.core.network.AppConfig
import at.stefanirndorfer.batmate.core.network.Environment
import at.stefanirndorfer.batmate.core.network.createHttpClient
import at.stefanirndorfer.batmate.domain.club.AssociationRepository
import at.stefanirndorfer.batmate.domain.club.ClubRepository
import at.stefanirndorfer.batmate.domain.club.FixtureRepository
import at.stefanirndorfer.batmate.domain.club.LeagueRepository
import at.stefanirndorfer.batmate.domain.club.TeamRepository
import org.koin.dsl.module

/** The XTTV-backed repositories, bound in every environment. */
val xttvModule = module {
    single {
        val config = get<AppConfig>()
        XttvApi(
            createHttpClient(
                userAgent = "BatMate/${config.appVersion}",
                apiKey = config.xttvApiKey,
                logRequests = config.environment != Environment.Prod,
            ),
        )
    }
    single<AssociationRepository> { XttvAssociationRepository(get(), get<AppConfig>().xttvHost) }
    single<ClubRepository> { XttvClubRepository(get(), get<AppConfig>().xttvHost) }
    single<TeamRepository> { XttvTeamRepository(get(), get<AppConfig>().xttvHost, getOrNull<LineupSource>()) }
    single<FixtureRepository> { XttvFixtureRepository(get(), get<AppConfig>().xttvHost, get()) }
    single<LeagueRepository> { XttvLeagueRepository(get(), get<AppConfig>().xttvHost) }
}
