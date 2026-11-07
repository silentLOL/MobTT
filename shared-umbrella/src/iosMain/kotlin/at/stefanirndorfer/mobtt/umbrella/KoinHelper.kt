package at.stefanirndorfer.mobtt.umbrella

import at.stefanirndorfer.mobtt.core.di.initKoin
import at.stefanirndorfer.mobtt.core.fakedirectory.fakeDirectoryModule
import at.stefanirndorfer.mobtt.core.network.AppConfig
import at.stefanirndorfer.mobtt.core.network.Environment
import at.stefanirndorfer.mobtt.core.network.installLogWriters
import at.stefanirndorfer.mobtt.domain.app.GetPlatformNameUseCase
import at.stefanirndorfer.mobtt.domain.club.Association
import at.stefanirndorfer.mobtt.domain.club.Club
import at.stefanirndorfer.mobtt.domain.club.ClubOverview
import at.stefanirndorfer.mobtt.domain.club.Fixture
import at.stefanirndorfer.mobtt.domain.club.FixtureDetail
import at.stefanirndorfer.mobtt.domain.club.FixtureLinks
import at.stefanirndorfer.mobtt.domain.club.GetClubOverviewUseCase
import at.stefanirndorfer.mobtt.domain.club.GetFixtureDetailUseCase
import at.stefanirndorfer.mobtt.domain.club.GetLeagueDetailUseCase
import at.stefanirndorfer.mobtt.domain.club.GetLeagueFixturesUseCase
import at.stefanirndorfer.mobtt.domain.club.GetTeamDetailUseCase
import at.stefanirndorfer.mobtt.domain.club.LeagueDetail
import at.stefanirndorfer.mobtt.domain.club.LineupPlayer
import at.stefanirndorfer.mobtt.domain.club.ListAssociationClubsUseCase
import at.stefanirndorfer.mobtt.domain.club.ListAssociationsUseCase
import at.stefanirndorfer.mobtt.domain.club.ListClubPlayersUseCase
import at.stefanirndorfer.mobtt.domain.club.ListLeaguesUseCase
import at.stefanirndorfer.mobtt.domain.club.SearchClubsUseCase
import at.stefanirndorfer.mobtt.domain.club.TeamDetail
import at.stefanirndorfer.mobtt.domain.player.Player
import at.stefanirndorfer.mobtt.domain.rating.GetRatingHistoryUseCase
import at.stefanirndorfer.mobtt.domain.rating.GetRatingsCentralIdUseCase
import at.stefanirndorfer.mobtt.domain.rating.RatingHistory
import at.stefanirndorfer.mobtt.domain.user.AddFavoriteUseCase
import at.stefanirndorfer.mobtt.domain.user.GetPlayerClubUseCase
import at.stefanirndorfer.mobtt.domain.user.GetPlayerRankingUseCase
import at.stefanirndorfer.mobtt.domain.user.GetPlayerTeamsUseCase
import at.stefanirndorfer.mobtt.domain.user.GetPlayerUseCase
import at.stefanirndorfer.mobtt.domain.user.ListLeaguePlayersUseCase
import at.stefanirndorfer.mobtt.domain.user.MyTeams
import at.stefanirndorfer.mobtt.domain.user.ObserveFavoritesUseCase
import at.stefanirndorfer.mobtt.domain.user.ObserveHomeFavoritesUseCase
import at.stefanirndorfer.mobtt.domain.user.ObserveMyTeamsUseCase
import at.stefanirndorfer.mobtt.domain.user.ObserveResolvedFavoritesUseCase
import at.stefanirndorfer.mobtt.domain.user.ObserveUpcomingFixturesUseCase
import at.stefanirndorfer.mobtt.domain.user.ObserveUsageConsentPromptUseCase
import at.stefanirndorfer.mobtt.domain.user.ObserveUsageConsentUseCase
import at.stefanirndorfer.mobtt.domain.user.ObserveUserContextUseCase
import at.stefanirndorfer.mobtt.domain.user.PlayerClub
import at.stefanirndorfer.mobtt.domain.user.PlayerRankingRow
import at.stefanirndorfer.mobtt.domain.user.PlayerTeams
import at.stefanirndorfer.mobtt.domain.user.RememberPlayerUseCase
import at.stefanirndorfer.mobtt.domain.user.RemoveFavoriteUseCase
import at.stefanirndorfer.mobtt.domain.user.ResolvedFavorite
import at.stefanirndorfer.mobtt.domain.user.SetMyClubUseCase
import at.stefanirndorfer.mobtt.domain.user.SetMyPlayerUseCase
import at.stefanirndorfer.mobtt.domain.user.SetUsageConsentUseCase
import at.stefanirndorfer.mobtt.domain.user.UserContext
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject

object KoinHelper : KoinComponent {
    fun start(environmentKey: String, appVersion: String, xttvApiKey: String, crashRecorder: IosCrashRecorder) {
        val environment = Environment.fromKey(environmentKey)
        forwardUnhandledExceptions(crashRecorder, xttvApiKey)
        initKoin(AppConfig(environment, appVersion, xttvApiKey)) {
            installLogWriters(environment, iosCrashLogWriter(crashRecorder, xttvApiKey))
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

    fun observeMyTeams(): Flow<DataResult<MyTeams>> = getObserveMyTeamsUseCase().invoke().asDataResult()

    suspend fun loadClubOverview(clubId: String): DataResult<ClubOverview?> = dataResult { getGetClubOverviewUseCase().invoke(clubId) }

    suspend fun loadTeamDetail(teamId: String): DataResult<TeamDetail?> = dataResult { getGetTeamDetailUseCase().invoke(teamId) }

    suspend fun loadLeagueDetail(leagueId: String): DataResult<LeagueDetail?> = dataResult { getGetLeagueDetailUseCase().invoke(leagueId) }

    suspend fun loadLeagueFixtures(leagueId: String): DataResult<List<Fixture>> = dataResult { getGetLeagueFixturesUseCase().invoke(leagueId) }

    suspend fun loadFixtureDetail(fixtureId: String): DataResult<FixtureDetail?> = dataResult { getGetFixtureDetailUseCase().invoke(fixtureId) }

    suspend fun loadPlayer(playerId: String): DataResult<Player?> = dataResult { get<GetPlayerUseCase>().invoke(playerId) }

    suspend fun loadPlayerClub(player: Player): DataResult<PlayerClub?> = dataResult { get<GetPlayerClubUseCase>().invoke(player) }

    suspend fun loadPlayerTeams(player: Player): DataResult<PlayerTeams> = dataResult { get<GetPlayerTeamsUseCase>().invoke(player) }

    /** False until sharing Fixtures is switched on for this build; the Fixture screen then shows no Share button. */
    fun isFixtureSharingEnabled(): Boolean = get<FixtureLinks>().sharingEnabled

    /** The Fixture link that opens [fixtureId] in MobTT (ADR-0011). */
    fun fixtureLink(fixtureId: String): String = get<FixtureLinks>().linkTo(fixtureId)

    /** The Fixture id in [url], or null when [url] is not a Fixture link. */
    fun fixtureIdOfLink(url: String): String? = get<FixtureLinks>().fixtureIdOf(url)

    /** False in builds that may not show Rating histories (ADR-0010); the Player screen then never asks for one. */
    fun isRatingHistoryEnabled(): Boolean = get<GetRatingHistoryUseCase>().isEnabled

    /** Ratings Central's own explanation of the Rating, linked from the Rating history's info sheet. */
    fun ratingHistoryExplanationUrl(): String = get<GetRatingHistoryUseCase>().explanationUrl

    /** Success with nil when the Player has no Ratings Central entry. */
    suspend fun loadRatingsCentralId(playerId: String, leagueIds: List<String>): DataResult<String?> = dataResult { get<GetRatingsCentralIdUseCase>().invoke(playerId, leagueIds) }

    suspend fun loadRatingHistory(ratingsCentralId: String): DataResult<RatingHistory> = dataResult { get<GetRatingHistoryUseCase>().invoke(ratingsCentralId) }

    suspend fun loadPlayerRanking(leagueId: String): DataResult<List<PlayerRankingRow>> = dataResult { getGetPlayerRankingUseCase().invoke(leagueId) }

    suspend fun listAssociations(): DataResult<List<Association>> = dataResult { getListAssociationsUseCase().invoke() }

    suspend fun listAssociationClubs(associationId: String): DataResult<List<Club>> = dataResult { getListAssociationClubsUseCase().invoke(associationId) }

    suspend fun listClubPlayers(clubId: String): DataResult<List<LineupPlayer>> = dataResult { getListClubPlayersUseCase().invoke(clubId) }

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

    fun getListAssociationClubsUseCase(): ListAssociationClubsUseCase {
        val useCase: ListAssociationClubsUseCase by inject()
        return useCase
    }

    fun getListClubPlayersUseCase(): ListClubPlayersUseCase {
        val useCase: ListClubPlayersUseCase by inject()
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

    fun getGetFixtureDetailUseCase(): GetFixtureDetailUseCase {
        val useCase: GetFixtureDetailUseCase by inject()
        return useCase
    }

    fun getGetLeagueFixturesUseCase(): GetLeagueFixturesUseCase {
        val useCase: GetLeagueFixturesUseCase by inject()
        return useCase
    }

    fun getGetLeagueDetailUseCase(): GetLeagueDetailUseCase {
        val useCase: GetLeagueDetailUseCase by inject()
        return useCase
    }

    fun getObserveMyTeamsUseCase(): ObserveMyTeamsUseCase {
        val useCase: ObserveMyTeamsUseCase by inject()
        return useCase
    }

    fun getGetPlayerRankingUseCase(): GetPlayerRankingUseCase {
        val useCase: GetPlayerRankingUseCase by inject()
        return useCase
    }

    fun getObserveUpcomingFixturesUseCase(): ObserveUpcomingFixturesUseCase {
        val useCase: ObserveUpcomingFixturesUseCase by inject()
        return useCase
    }

    fun getObserveUsageConsentUseCase(): ObserveUsageConsentUseCase {
        val useCase: ObserveUsageConsentUseCase by inject()
        return useCase
    }

    fun getObserveUsageConsentPromptUseCase(): ObserveUsageConsentPromptUseCase {
        val useCase: ObserveUsageConsentPromptUseCase by inject()
        return useCase
    }

    fun getSetUsageConsentUseCase(): SetUsageConsentUseCase {
        val useCase: SetUsageConsentUseCase by inject()
        return useCase
    }
}
