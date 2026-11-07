package at.stefanirndorfer.mobtt.domain.rating.di

import at.stefanirndorfer.mobtt.domain.rating.GetRatingHistoryUseCase
import at.stefanirndorfer.mobtt.domain.rating.GetRatingsCentralIdUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

/** The repository and [at.stefanirndorfer.mobtt.domain.rating.RatingHistoryAvailability] are bound by `:core:di`. */
val ratingDomainModule = module {
    factoryOf(::GetRatingsCentralIdUseCase)
    factoryOf(::GetRatingHistoryUseCase)
}
