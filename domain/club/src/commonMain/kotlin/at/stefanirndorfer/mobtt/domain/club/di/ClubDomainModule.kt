package at.stefanirndorfer.mobtt.domain.club.di

import at.stefanirndorfer.mobtt.domain.club.GetClubOverviewUseCase
import at.stefanirndorfer.mobtt.domain.club.GetLeagueDetailUseCase
import at.stefanirndorfer.mobtt.domain.club.GetTeamDetailUseCase
import at.stefanirndorfer.mobtt.domain.club.ListAssociationClubsUseCase
import at.stefanirndorfer.mobtt.domain.club.ListAssociationsUseCase
import at.stefanirndorfer.mobtt.domain.club.ListClubPlayersUseCase
import at.stefanirndorfer.mobtt.domain.club.ListLeaguesUseCase
import at.stefanirndorfer.mobtt.domain.club.SearchClubsUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val clubDomainModule = module {
    factoryOf(::SearchClubsUseCase)
    factoryOf(::ListAssociationsUseCase)
    factoryOf(::ListLeaguesUseCase)
    factoryOf(::ListAssociationClubsUseCase)
    factoryOf(::ListClubPlayersUseCase)
    factory { GetClubOverviewUseCase(get(), get(), get(), get(), get()) }
    factory { GetTeamDetailUseCase(get(), get(), get(), get()) }
    factoryOf(::GetLeagueDetailUseCase)
}
