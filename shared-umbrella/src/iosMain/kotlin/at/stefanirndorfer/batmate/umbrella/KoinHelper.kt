package at.stefanirndorfer.batmate.umbrella

import at.stefanirndorfer.batmate.core.di.initKoin
import at.stefanirndorfer.batmate.core.fakedirectory.fakeDirectoryModule
import at.stefanirndorfer.batmate.core.network.AppConfig
import at.stefanirndorfer.batmate.core.network.Environment
import at.stefanirndorfer.batmate.domain.app.GetPlatformNameUseCase
import at.stefanirndorfer.batmate.domain.club.SearchClubsUseCase
import at.stefanirndorfer.batmate.domain.user.AddFavoriteUseCase
import at.stefanirndorfer.batmate.domain.user.ObserveFavoritesUseCase
import at.stefanirndorfer.batmate.domain.user.ObserveResolvedFavoritesUseCase
import at.stefanirndorfer.batmate.domain.user.ObserveUserContextUseCase
import at.stefanirndorfer.batmate.domain.user.RemoveFavoriteUseCase
import at.stefanirndorfer.batmate.domain.user.SearchPlayersUseCase
import at.stefanirndorfer.batmate.domain.user.SetMyClubUseCase
import at.stefanirndorfer.batmate.domain.user.SetMyPlayerUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object KoinHelper : KoinComponent {
    fun start(environmentKey: String) {
        val environment = Environment.fromKey(environmentKey)
        initKoin(AppConfig(environment)) {
            // TODO: bind the XTTV-backed Player, Club and Association repositories for Dev, Beta and Prod; Home and Settings cannot resolve them until then.
            if (environment == Environment.Deb) {
                modules(fakeDirectoryModule)
            }
        }
    }

    fun getPlatformNameUseCase(): GetPlatformNameUseCase {
        val useCase: GetPlatformNameUseCase by inject()
        return useCase
    }

    fun getObserveUserContextUseCase(): ObserveUserContextUseCase {
        val useCase: ObserveUserContextUseCase by inject()
        return useCase
    }

    fun getObserveResolvedFavoritesUseCase(): ObserveResolvedFavoritesUseCase {
        val useCase: ObserveResolvedFavoritesUseCase by inject()
        return useCase
    }

    fun getObserveFavoritesUseCase(): ObserveFavoritesUseCase {
        val useCase: ObserveFavoritesUseCase by inject()
        return useCase
    }

    fun getAddFavoriteUseCase(): AddFavoriteUseCase {
        val useCase: AddFavoriteUseCase by inject()
        return useCase
    }

    fun getRemoveFavoriteUseCase(): RemoveFavoriteUseCase {
        val useCase: RemoveFavoriteUseCase by inject()
        return useCase
    }

    fun getSetMyPlayerUseCase(): SetMyPlayerUseCase {
        val useCase: SetMyPlayerUseCase by inject()
        return useCase
    }

    fun getSetMyClubUseCase(): SetMyClubUseCase {
        val useCase: SetMyClubUseCase by inject()
        return useCase
    }

    fun getSearchPlayersUseCase(): SearchPlayersUseCase {
        val useCase: SearchPlayersUseCase by inject()
        return useCase
    }

    fun getSearchClubsUseCase(): SearchClubsUseCase {
        val useCase: SearchClubsUseCase by inject()
        return useCase
    }
}
