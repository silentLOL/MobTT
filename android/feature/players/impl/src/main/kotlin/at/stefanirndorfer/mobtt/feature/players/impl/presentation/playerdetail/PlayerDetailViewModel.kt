package at.stefanirndorfer.mobtt.feature.players.impl.presentation.playerdetail

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
import at.stefanirndorfer.mobtt.core.ui.section.section
import at.stefanirndorfer.mobtt.domain.user.AddFavoriteUseCase
import at.stefanirndorfer.mobtt.domain.user.Favorite
import at.stefanirndorfer.mobtt.domain.user.GetPlayerClubUseCase
import at.stefanirndorfer.mobtt.domain.user.GetPlayerTeamsUseCase
import at.stefanirndorfer.mobtt.domain.user.GetPlayerUseCase
import at.stefanirndorfer.mobtt.domain.user.ObserveFavoritesUseCase
import at.stefanirndorfer.mobtt.domain.user.RemoveFavoriteUseCase
import at.stefanirndorfer.mobtt.feature.players.impl.presentation.playerdetail.models.PlayerDetailAction
import at.stefanirndorfer.mobtt.feature.players.impl.presentation.playerdetail.models.PlayerDetailEvent
import at.stefanirndorfer.mobtt.feature.players.impl.presentation.playerdetail.models.PlayerDetailUiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PlayerDetailViewModel(
    private val playerId: String,
    private val getPlayer: GetPlayerUseCase,
    private val getPlayerClub: GetPlayerClubUseCase,
    private val getPlayerTeams: GetPlayerTeamsUseCase,
    private val observeFavorites: ObserveFavoritesUseCase,
    private val addFavorite: AddFavoriteUseCase,
    private val removeFavorite: RemoveFavoriteUseCase,
    private val analytics: AnalyticsTracker = NoOpAnalyticsTracker,
) : ViewModel() {
    init {
        analytics.screenView(AnalyticsScreen.PLAYER_DETAIL)
    }

    private val clubRetry = RetryTrigger()

    private val teamsRetry = RetryTrigger()

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<PlayerDetailUiState> = flow { emit(loadPlayer()) }
        .flatMapLatest { player ->
            if (player == null) {
                flowOf(PlayerDetailUiState.Unavailable)
            } else {
                combine(
                    observeFavorites(),
                    clubRetry.section { getPlayerClub(player) },
                    teamsRetry.section { getPlayerTeams(player) },
                ) { favorites, club, teams ->
                    PlayerDetailUiState.Content(player, Favorite.Player(playerId) in favorites, club, teams)
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

    private suspend fun loadPlayer() = try {
        getPlayer(playerId)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        null
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
