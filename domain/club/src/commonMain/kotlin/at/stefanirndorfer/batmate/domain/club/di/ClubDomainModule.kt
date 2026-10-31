package at.stefanirndorfer.batmate.domain.club.di

import at.stefanirndorfer.batmate.domain.club.GetClubOverviewUseCase
import at.stefanirndorfer.batmate.domain.club.GetLeagueDetailUseCase
import at.stefanirndorfer.batmate.domain.club.GetTeamDetailUseCase
import at.stefanirndorfer.batmate.domain.club.ListAssociationClubsUseCase
import at.stefanirndorfer.batmate.domain.club.ListAssociationsUseCase
import at.stefanirndorfer.batmate.domain.club.ListClubPlayersUseCase
import at.stefanirndorfer.batmate.domain.club.ListLeaguesUseCase
import at.stefanirndorfer.batmate.domain.club.SearchClubsUseCase
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
