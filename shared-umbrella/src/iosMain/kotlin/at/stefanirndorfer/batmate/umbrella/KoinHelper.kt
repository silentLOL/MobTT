package at.stefanirndorfer.batmate.umbrella

import at.stefanirndorfer.batmate.core.di.initKoin
import at.stefanirndorfer.batmate.core.fakedirectory.fakeDirectoryModule
import at.stefanirndorfer.batmate.core.network.AppConfig
import at.stefanirndorfer.batmate.core.network.Environment
import at.stefanirndorfer.batmate.domain.app.GetPlatformNameUseCase
import at.stefanirndorfer.batmate.domain.club.ClubOverview
import at.stefanirndorfer.batmate.domain.club.GetClubOverviewUseCase
import at.stefanirndorfer.batmate.domain.club.GetLeagueDetailUseCase
import at.stefanirndorfer.batmate.domain.club.GetTeamDetailUseCase
import at.stefanirndorfer.batmate.domain.club.LeagueDetail
import at.stefanirndorfer.batmate.domain.club.ListAssociationsUseCase
import at.stefanirndorfer.batmate.domain.club.ListLeaguesUseCase
import at.stefanirndorfer.batmate.domain.club.SearchClubsUseCase
import at.stefanirndorfer.batmate.domain.club.TeamDetail
import at.stefanirndorfer.batmate.domain.user.AddFavoriteUseCase
import at.stefanirndorfer.batmate.domain.user.GetPlayerDetailUseCase
import at.stefanirndorfer.batmate.domain.user.ListLeaguePlayersUseCase
import at.stefanirndorfer.batmate.domain.user.ObserveFavoritesUseCase
import at.stefanirndorfer.batmate.domain.user.ObserveHomeFavoritesUseCase
import at.stefanirndorfer.batmate.domain.user.ObserveResolvedFavoritesUseCase
import at.stefanirndorfer.batmate.domain.user.ObserveUpcomingFixturesUseCase
import at.stefanirndorfer.batmate.domain.user.ObserveUserContextUseCase
import at.stefanirndorfer.batmate.domain.user.PlayerDetail
import at.stefanirndorfer.batmate.domain.user.RememberPlayerUseCase
import at.stefanirndorfer.batmate.domain.user.RemoveFavoriteUseCase
import at.stefanirndorfer.batmate.domain.user.ResolvedFavorite
import at.stefanirndorfer.batmate.domain.user.SetMyClubUseCase
import at.stefanirndorfer.batmate.domain.user.SetMyPlayerUseCase
import at.stefanirndorfer.batmate.domain.user.UserContext
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object KoinHelper : KoinComponent {
    fun start(environmentKey: String, appVersion: String, xttvApiKey: String) {
        val environment = Environment.fromKey(environmentKey)
        initKoin(AppConfig(environment, appVersion, xttvApiKey)) {
            // TODO: shrink as tickets 03-06 and 09 bring XTTV-backed repositories; `initKoin` binds those after this and wins.
            if (environment == Environment.Deb || environment == Environment.Dev) {
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

    fun observeUserContext(): Flow<DataResult<UserContext>> = getObserveUserContextUseCase().invoke().asDataResult()

    fun observeResolvedFavorites(): Flow<DataResult<List<ResolvedFavorite>>> = getObserveResolvedFavoritesUseCase().invoke().asDataResult()

    fun observeHomeFavorites(): Flow<DataResult<List<ResolvedFavorite>>> = getObserveHomeFavoritesUseCase().invoke().asDataResult()

    suspend fun loadClubOverview(clubId: String): DataResult<ClubOverview?> = dataResult { getGetClubOverviewUseCase().invoke(clubId) }

    suspend fun loadTeamDetail(teamId: String): DataResult<TeamDetail?> = dataResult { getGetTeamDetailUseCase().invoke(teamId) }

    suspend fun loadLeagueDetail(leagueId: String): DataResult<LeagueDetail?> = dataResult { getGetLeagueDetailUseCase().invoke(leagueId) }

    suspend fun loadPlayerDetail(playerId: String): DataResult<PlayerDetail?> = dataResult { getGetPlayerDetailUseCase().invoke(playerId) }

    fun getObserveResolvedFavoritesUseCase(): ObserveResolvedFavoritesUseCase {
        val useCase: ObserveResolvedFavoritesUseCase by inject()
        return useCase
    }

    fun getObserveHomeFavoritesUseCase(): ObserveHomeFavoritesUseCase {
        val useCase: ObserveHomeFavoritesUseCase by inject()
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

    fun getRememberPlayerUseCase(): RememberPlayerUseCase {
        val useCase: RememberPlayerUseCase by inject()
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

    fun getListLeaguePlayersUseCase(): ListLeaguePlayersUseCase {
        val useCase: ListLeaguePlayersUseCase by inject()
        return useCase
    }

    fun getListAssociationsUseCase(): ListAssociationsUseCase {
        val useCase: ListAssociationsUseCase by inject()
        return useCase
    }

    fun getListLeaguesUseCase(): ListLeaguesUseCase {
        val useCase: ListLeaguesUseCase by inject()
        return useCase
    }

    fun getSearchClubsUseCase(): SearchClubsUseCase {
        val useCase: SearchClubsUseCase by inject()
        return useCase
    }

    fun getGetClubOverviewUseCase(): GetClubOverviewUseCase {
        val useCase: GetClubOverviewUseCase by inject()
        return useCase
    }

    fun getGetTeamDetailUseCase(): GetTeamDetailUseCase {
        val useCase: GetTeamDetailUseCase by inject()
        return useCase
    }

    fun getGetLeagueDetailUseCase(): GetLeagueDetailUseCase {
        val useCase: GetLeagueDetailUseCase by inject()
        return useCase
    }

    fun getGetPlayerDetailUseCase(): GetPlayerDetailUseCase {
        val useCase: GetPlayerDetailUseCase by inject()
        return useCase
    }

    fun getObserveUpcomingFixturesUseCase(): ObserveUpcomingFixturesUseCase {
        val useCase: ObserveUpcomingFixturesUseCase by inject()
        return useCase
    }
}
