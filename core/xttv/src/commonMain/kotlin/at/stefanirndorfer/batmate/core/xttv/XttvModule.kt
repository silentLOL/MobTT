package at.stefanirndorfer.batmate.core.xttv

import at.stefanirndorfer.batmate.core.network.AppConfig
import at.stefanirndorfer.batmate.core.network.createHttpClient
import at.stefanirndorfer.batmate.domain.club.AssociationRepository
import at.stefanirndorfer.batmate.domain.club.ClubRepository
import at.stefanirndorfer.batmate.domain.club.TeamRepository
import org.koin.dsl.module

/** The XTTV-backed repositories. Bound in every environment except `deb`, which keeps the fake directory. */
val xttvModule = module {
    single { XttvApi(createHttpClient(userAgent = "BatMate/${get<AppConfig>().appVersion}")) }
    single { XttvHostResolver(defaultHost = get<AppConfig>().xttvHost) }
    single<AssociationRepository> { XttvAssociationRepository(get(), get()) }
    single<ClubRepository> { XttvClubRepository(get(), get()) }
    single<TeamRepository> { XttvTeamRepository(get(), get(), get(), getOrNull<LineupSource>()) }
}
