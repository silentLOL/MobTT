package at.stefanirndorfer.mobtt.feature.players.impl.presentation.playerdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.stefanirndorfer.mobtt.core.analytics.AnalyticsKind
import at.stefanirndorfer.mobtt.core.analytics.AnalyticsScreen
import at.stefanirndorfer.mobtt.core.analytics.AnalyticsTracker
import at.stefanirndorfer.mobtt.core.analytics.NoOpAnalyticsTracker
import at.stefanirndorfer.mobtt.core.analytics.detailOpened
import at.stefanirndorfer.mobtt.core.analytics.favoriteToggled
import at.stefanirndorfer.mobtt.core.analytics.ratingHistoryOpened
import at.stefanirndorfer.mobtt.core.analytics.ratingInfoOpened
import at.stefanirndorfer.mobtt.core.analytics.screenView
import at.stefanirndorfer.mobtt.core.ui.RetryTrigger
import at.stefanirndorfer.mobtt.core.ui.section.SectionUi
import at.stefanirndorfer.mobtt.core.ui.section.lazySection
import at.stefanirndorfer.mobtt.core.ui.section.section
import at.stefanirndorfer.mobtt.domain.player.Player
import at.stefanirndorfer.mobtt.domain.rating.GetRatingHistoryUseCase
import at.stefanirndorfer.mobtt.domain.rating.GetRatingsCentralIdUseCase
import at.stefanirndorfer.mobtt.domain.user.AddFavoriteUseCase
import at.stefanirndorfer.mobtt.domain.user.Favorite
import at.stefanirndorfer.mobtt.domain.user.GetPlayerClubUseCase
import at.stefanirndorfer.mobtt.domain.user.GetPlayerTeamsUseCase
import at.stefanirndorfer.mobtt.domain.user.GetPlayerUseCase
import at.stefanirndorfer.mobtt.domain.user.ObserveFavoritesUseCase
import at.stefanirndorfer.mobtt.domain.user.PlayerTeams
import at.stefanirndorfer.mobtt.domain.user.RemoveFavoriteUseCase
import at.stefanirndorfer.mobtt.feature.players.impl.presentation.playerdetail.models.PlayerDetailAction
import at.stefanirndorfer.mobtt.feature.players.impl.presentation.playerdetail.models.PlayerDetailEvent
import at.stefanirndorfer.mobtt.feature.players.impl.presentation.playerdetail.models.PlayerDetailUiState
import at.stefanirndorfer.mobtt.feature.players.impl.presentation.playerdetail.models.RatingHistorySectionUi
import at.stefanirndorfer.mobtt.feature.players.impl.presentation.playerdetail.models.RatingHistorySpan
import co.touchlab.kermit.Logger
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

class PlayerDetailViewModel(
    private val playerId: String,
    private val getPlayer: GetPlayerUseCase,
    private val getPlayerClub: GetPlayerClubUseCase,
    private val getPlayerTeams: GetPlayerTeamsUseCase,
    private val getRatingsCentralId: GetRatingsCentralIdUseCase,
    private val getRatingHistory: GetRatingHistoryUseCase,
    private val observeFavorites: ObserveFavoritesUseCase,
    private val addFavorite: AddFavoriteUseCase,
    private val removeFavorite: RemoveFavoriteUseCase,
    private val analytics: AnalyticsTracker = NoOpAnalyticsTracker,
    private val clock: Clock = Clock.System,
) : ViewModel() {
    init {
        analytics.screenView(AnalyticsScreen.PLAYER_DETAIL)
    }

    private val clubRetry = RetryTrigger()

    private val teamsRetry = RetryTrigger()

    private val ratingHistoryRetry = RetryTrigger()

    private val ratingHistoryExpanded = MutableStateFlow(false)

    private val ratingHistorySpan = MutableStateFlow(RatingHistorySpan.LastYear)

    private val ratingInfoShown = MutableStateFlow(false)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<PlayerDetailUiState> = flow { emit(loadPlayer()) }
        .flatMapLatest { player ->
            if (player == null) {
                flowOf(PlayerDetailUiState.Unavailable)
            } else {
                val teams = teamsRetry.section { getPlayerTeams(player) }
                    .shareIn(viewModelScope, SharingStarted.WhileSubscribed(), replay = 1)
                combine(
                    observeFavorites(),
                    clubRetry.section { getPlayerClub(player) },
                    teams,
                    ratingHistorySection(player, teams),
                    ratingInfoShown,
                ) { favorites, club, teamsSection, ratingHistory, infoShown ->
                    PlayerDetailUiState.Content(player, Favorite.Player(playerId) in favorites, club, teamsSection, ratingHistory, infoShown)
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), PlayerDetailUiState.Loading)

    private val eventChannel = Channel<PlayerDetailEvent>()
    val events: Flow<PlayerDetailEvent> = eventChannel.receiveAsFlow()

    fun onAction(action: PlayerDetailAction) {
        when (action) {
            PlayerDetailAction.FavoriteClicked -> toggleFavorite()
            PlayerDetailAction.ClubClicked -> openClub()
            is PlayerDetailAction.TeamClicked -> openTeam(action.teamId)
            PlayerDetailAction.ClubRetryClicked -> clubRetry.retry()
            PlayerDetailAction.TeamsRetryClicked -> teamsRetry.retry()
            PlayerDetailAction.RatingHistoryToggled -> toggleRatingHistory()
            PlayerDetailAction.RatingHistoryRetryClicked -> ratingHistoryRetry.retry()
            is PlayerDetailAction.RatingHistorySpanChosen -> ratingHistorySpan.value = action.span
            PlayerDetailAction.RatingInfoClicked -> {
                ratingInfoShown.value = true
                analytics.ratingInfoOpened()
            }
            PlayerDetailAction.RatingInfoDismissed -> ratingInfoShown.value = false
            PlayerDetailAction.RatingInfoLinkClicked -> viewModelScope.launch { eventChannel.send(PlayerDetailEvent.OpenWebPage(getRatingHistory.explanationUrl)) }
        }
    }

    private fun openClub() {
        val content = uiState.value as? PlayerDetailUiState.Content ?: return
        val clubId = content.player.clubId
        analytics.detailOpened(AnalyticsKind.CLUB, clubId, from = AnalyticsScreen.PLAYER_DETAIL)
        viewModelScope.launch { eventChannel.send(PlayerDetailEvent.OpenClub(clubId)) }
    }

    private fun openTeam(teamId: String) {
        analytics.detailOpened(AnalyticsKind.TEAM, teamId, from = AnalyticsScreen.PLAYER_DETAIL)
        viewModelScope.launch { eventChannel.send(PlayerDetailEvent.OpenTeam(teamId)) }
    }

    private fun toggleFavorite() {
        val content = uiState.value as? PlayerDetailUiState.Content ?: return
        val favorite = Favorite.Player(playerId)
        viewModelScope.launch {
            if (content.isFavorite) {
                removeFavorite(favorite)
            } else {
                addFavorite(favorite)
            }
            analytics.favoriteToggled(AnalyticsKind.PLAYER, playerId, enabled = !content.isFavorite)
        }
    }

    /**
     * Hidden until the Teams have loaded and their Leagues name the Player's Ratings Central entry; hidden for good
     * without one, or in builds that may not show Rating histories. Ratings Central is asked only once it is opened.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun ratingHistorySection(player: Player, teams: Flow<SectionUi<PlayerTeams>>): Flow<RatingHistorySectionUi?> {
        if (!getRatingHistory.isEnabled) {
            return flowOf(null)
        }
        return teams
            .mapNotNull { (it as? SectionUi.Loaded)?.value }
            .mapLatest { loaded -> ratingsCentralIdOf(player, loaded) }
            .onStart { emit(null) }
            .distinctUntilChanged()
            .flatMapLatest { ratingsCentralId ->
                if (ratingsCentralId == null) {
                    flowOf(null)
                } else {
                    combine(
                        ratingHistoryExpanded,
                        ratingHistoryRetry.lazySection(ratingHistoryExpanded) { getRatingHistory(ratingsCentralId) },
                        ratingHistorySpan,
                    ) { expanded, history, span ->
                        RatingHistorySectionUi(expanded, history, span, since = firstDayOf(span))
                    }
                }
            }
    }

    /** The Rating history is a bonus: a ranking XTTV cannot answer only hides it. */
    private suspend fun ratingsCentralIdOf(player: Player, teams: PlayerTeams): String? = try {
        getRatingsCentralId(player.id, teams.teams.map { it.leagueId })
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Logger.withTag(TAG).w(e) { "Ratings Central entry of Player ${player.id} not found" }
        null
    }

    /** The last 12 months run from this day a year ago, so a Rating update on that day still counts. */
    private fun firstDayOf(span: RatingHistorySpan): LocalDate? = when (span) {
        RatingHistorySpan.LastYear -> clock.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.minus(DatePeriod(years = 1))
        RatingHistorySpan.All -> null
    }

    private fun toggleRatingHistory() {
        val opened = !ratingHistoryExpanded.value
        ratingHistoryExpanded.value = opened
        if (opened) {
            analytics.ratingHistoryOpened()
        }
    }

    private suspend fun loadPlayer() = try {
        getPlayer(playerId)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        null
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
        const val TAG = "PlayerDetailViewModel"
    }
}
