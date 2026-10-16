package at.stefanirndorfer.batmate.domain.club.di

import at.stefanirndorfer.batmate.domain.club.SearchClubsUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val clubDomainModule = module {
    factoryOf(::SearchClubsUseCase)
}
