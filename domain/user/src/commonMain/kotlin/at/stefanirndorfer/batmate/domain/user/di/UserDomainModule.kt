package at.stefanirndorfer.batmate.domain.user.di

import at.stefanirndorfer.batmate.domain.user.DataStoreUserSelectionRepository
import at.stefanirndorfer.batmate.domain.user.ObserveUserContextUseCase
import at.stefanirndorfer.batmate.domain.user.SetMyClubUseCase
import at.stefanirndorfer.batmate.domain.user.SetMyPlayerUseCase
import at.stefanirndorfer.batmate.domain.user.UserSelectionRepository
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val userDomainModule = module {
    singleOf(::DataStoreUserSelectionRepository) bind UserSelectionRepository::class
    factoryOf(::SetMyPlayerUseCase)
    factoryOf(::SetMyClubUseCase)
    factoryOf(::ObserveUserContextUseCase)
}
