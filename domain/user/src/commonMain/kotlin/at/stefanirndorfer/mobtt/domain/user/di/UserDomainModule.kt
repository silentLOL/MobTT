package at.stefanirndorfer.mobtt.domain.user.di

import at.stefanirndorfer.mobtt.domain.player.PickedPlayerStore
import at.stefanirndorfer.mobtt.domain.user.AddFavoriteUseCase
import at.stefanirndorfer.mobtt.domain.user.DataStoreFavoritesRepository
import at.stefanirndorfer.mobtt.domain.user.DataStorePickedPlayerStore
import at.stefanirndorfer.mobtt.domain.user.DataStoreUsageConsentRepository
import at.stefanirndorfer.mobtt.domain.user.DataStoreUserSelectionRepository
import at.stefanirndorfer.mobtt.domain.user.FavoritesRepository
import at.stefanirndorfer.mobtt.domain.user.GetPlayerDetailUseCase
import at.stefanirndorfer.mobtt.domain.user.ListLeaguePlayersUseCase
import at.stefanirndorfer.mobtt.domain.user.ObserveFavoritesUseCase
import at.stefanirndorfer.mobtt.domain.user.ObserveHomeFavoritesUseCase
import at.stefanirndorfer.mobtt.domain.user.ObserveResolvedFavoritesUseCase
import at.stefanirndorfer.mobtt.domain.user.ObserveUpcomingFixturesUseCase
import at.stefanirndorfer.mobtt.domain.user.ObserveUsageConsentPromptUseCase
import at.stefanirndorfer.mobtt.domain.user.ObserveUsageConsentUseCase
import at.stefanirndorfer.mobtt.domain.user.ObserveUserContextUseCase
import at.stefanirndorfer.mobtt.domain.user.RememberPlayerUseCase
import at.stefanirndorfer.mobtt.domain.user.RemoveFavoriteUseCase
import at.stefanirndorfer.mobtt.domain.user.SetMyClubUseCase
import at.stefanirndorfer.mobtt.domain.user.SetMyPlayerUseCase
import at.stefanirndorfer.mobtt.domain.user.SetUsageConsentUseCase
import at.stefanirndorfer.mobtt.domain.user.UsageConsentRepository
import at.stefanirndorfer.mobtt.domain.user.UserSelectionRepository
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val userDomainModule = module {
    singleOf(::DataStoreUserSelectionRepository) bind UserSelectionRepository::class
    singleOf(::DataStoreUsageConsentRepository) bind UsageConsentRepository::class
    singleOf(::DataStorePickedPlayerStore) bind PickedPlayerStore::class
    singleOf(::DataStoreFavoritesRepository) bind FavoritesRepository::class
    factoryOf(::SetMyPlayerUseCase)
    factoryOf(::RememberPlayerUseCase)
    factoryOf(::SetMyClubUseCase)
    factoryOf(::ObserveUserContextUseCase)
    factoryOf(::AddFavoriteUseCase)
    factoryOf(::RemoveFavoriteUseCase)
    factoryOf(::ObserveFavoritesUseCase)
    factoryOf(::ObserveResolvedFavoritesUseCase)
    factoryOf(::ObserveHomeFavoritesUseCase)
    factoryOf(::ObserveUpcomingFixturesUseCase)
    factoryOf(::ListLeaguePlayersUseCase)
    factoryOf(::GetPlayerDetailUseCase)
    factoryOf(::ObserveUsageConsentUseCase)
    factoryOf(::ObserveUsageConsentPromptUseCase)
    factoryOf(::SetUsageConsentUseCase)
}
