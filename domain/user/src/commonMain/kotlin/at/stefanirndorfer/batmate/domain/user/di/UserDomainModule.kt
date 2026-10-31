package at.stefanirndorfer.batmate.domain.user.di

import at.stefanirndorfer.batmate.domain.player.PickedPlayerStore
import at.stefanirndorfer.batmate.domain.user.AddFavoriteUseCase
import at.stefanirndorfer.batmate.domain.user.DataStoreFavoritesRepository
import at.stefanirndorfer.batmate.domain.user.DataStorePickedPlayerStore
import at.stefanirndorfer.batmate.domain.user.DataStoreUserSelectionRepository
import at.stefanirndorfer.batmate.domain.user.FavoritesRepository
import at.stefanirndorfer.batmate.domain.user.GetPlayerDetailUseCase
import at.stefanirndorfer.batmate.domain.user.ListLeaguePlayersUseCase
import at.stefanirndorfer.batmate.domain.user.ObserveFavoritesUseCase
import at.stefanirndorfer.batmate.domain.user.ObserveHomeFavoritesUseCase
import at.stefanirndorfer.batmate.domain.user.ObserveResolvedFavoritesUseCase
import at.stefanirndorfer.batmate.domain.user.ObserveUpcomingFixturesUseCase
import at.stefanirndorfer.batmate.domain.user.ObserveUserContextUseCase
import at.stefanirndorfer.batmate.domain.user.RememberPlayerUseCase
import at.stefanirndorfer.batmate.domain.user.RemoveFavoriteUseCase
import at.stefanirndorfer.batmate.domain.user.SetMyClubUseCase
import at.stefanirndorfer.batmate.domain.user.SetMyPlayerUseCase
import at.stefanirndorfer.batmate.domain.user.UserSelectionRepository
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val userDomainModule = module {
    singleOf(::DataStoreUserSelectionRepository) bind UserSelectionRepository::class
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
}
