package at.stefanirndorfer.mobtt.feature.club.impl.presentation.teamdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.stefanirndorfer.mobtt.core.analytics.AnalyticsKind
import at.stefanirndorfer.mobtt.core.analytics.AnalyticsScreen
import at.stefanirndorfer.mobtt.core.analytics.AnalyticsTracker
import at.stefanirndorfer.mobtt.core.analytics.NoOpAnalyticsTracker
import at.stefanirndorfer.mobtt.core.analytics.detailOpened
import at.stefanirndorfer.mobtt.core.analytics.favoriteToggled
import at.stefanirndorfer.mobtt.core.analytics.screenView
import at.stefanirndorfer.mobtt.core.ui.RetryTrigger
import at.stefanirndorfer.mobtt.domain.club.GetTeamDetailUseCase
import at.stefanirndorfer.mobtt.domain.club.LineupPlayer
import at.stefanirndorfer.mobtt.domain.player.Player
import at.stefanirndorfer.mobtt.domain.user.AddFavoriteUseCase
import at.stefanirndorfer.mobtt.domain.user.Favorite
import at.stefanirndorfer.mobtt.domain.user.ObserveFavoritesUseCase
import at.stefanirndorfer.mobtt.domain.user.RememberPlayerUseCase
import at.stefanirndorfer.mobtt.domain.user.RemoveFavoriteUseCase
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.teamdetail.models.TeamDetailAction
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.teamdetail.models.TeamDetailEvent
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.teamdetail.models.TeamDetailUiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TeamDetailViewModel(
    private val teamId: String,
    private val getTeamDetail: GetTeamDetailUseCase,
    private val observeFavorites: ObserveFavoritesUseCase,
    private val addFavorite: AddFavoriteUseCase,
    private val removeFavorite: RemoveFavoriteUseCase,
    private val rememberPlayer: RememberPlayerUseCase,
    private val analytics: AnalyticsTracker = NoOpAnalyticsTracker,
) : ViewModel() {
    init {
        analytics.screenView(AnalyticsScreen.TEAM_DETAIL)
    }

    private val retry = RetryTrigger()

    val uiState: StateFlow<TeamDetailUiState> = retry
        .restarting {
            flow {
                emit(TeamDetailUiState.Loading)
                val state = load()
                if (state is TeamDetailUiState.Content) {
                    observeFavorites().collect { favorites ->
                        emit(state.copy(isFavorite = Favorite.Team(teamId) in favorites))
                    }
                } else {
                    emit(state)
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), TeamDetailUiState.Loading)

    private val eventChannel = Channel<TeamDetailEvent>()
    val events: Flow<TeamDetailEvent> = eventChannel.receiveAsFlow()

    fun onAction(action: TeamDetailAction) {
        when (action) {
            TeamDetailAction.RetryClicked -> retry.retry()
            TeamDetailAction.FavoriteClicked -> toggleFavorite()
            is TeamDetailAction.LeagueClicked -> openLeague(action.leagueId)
            is TeamDetailAction.PlayerClicked -> openPlayer(action.player)
        }
    }

    private fun openLeague(leagueId: String) {
        analytics.detailOpened(AnalyticsKind.LEAGUE, leagueId, from = AnalyticsScreen.TEAM_DETAIL)
        viewModelScope.launch { eventChannel.send(TeamDetailEvent.OpenLeague(leagueId, teamId)) }
    }

    private fun openPlayer(player: LineupPlayer) {
        val content = uiState.value as? TeamDetailUiState.Content ?: return
        analytics.detailOpened(AnalyticsKind.PLAYER, player.playerId, from = AnalyticsScreen.TEAM_DETAIL)
        viewModelScope.launch {
            rememberPlayer(Player(player.playerId, player.name, content.detail.team.clubId))
            eventChannel.send(TeamDetailEvent.OpenPlayer(player.playerId))
        }
    }

    private fun toggleFavorite() {
        val content = uiState.value as? TeamDetailUiState.Content ?: return
        val favorite = Favorite.Team(teamId)
        viewModelScope.launch {
            if (content.isFavorite) {
                removeFavorite(favorite)
            } else {
                addFavorite(favorite)
            }
            analytics.favoriteToggled(AnalyticsKind.TEAM, teamId, enabled = !content.isFavorite)
        }
    }

    private suspend fun load(): TeamDetailUiState = try {
        getTeamDetail(teamId)?.let(TeamDetailUiState::Content) ?: TeamDetailUiState.Error
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        TeamDetailUiState.Error
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
