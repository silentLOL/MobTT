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
import at.stefanirndorfer.mobtt.domain.user.AddFavoriteUseCase
import at.stefanirndorfer.mobtt.domain.user.Favorite
import at.stefanirndorfer.mobtt.domain.user.GetPlayerDetailUseCase
import at.stefanirndorfer.mobtt.domain.user.ObserveFavoritesUseCase
import at.stefanirndorfer.mobtt.domain.user.RemoveFavoriteUseCase
import at.stefanirndorfer.mobtt.feature.players.impl.presentation.playerdetail.models.PlayerDetailAction
import at.stefanirndorfer.mobtt.feature.players.impl.presentation.playerdetail.models.PlayerDetailEvent
import at.stefanirndorfer.mobtt.feature.players.impl.presentation.playerdetail.models.PlayerDetailUiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PlayerDetailViewModel(
    private val playerId: String,
    private val getPlayerDetail: GetPlayerDetailUseCase,
    private val observeFavorites: ObserveFavoritesUseCase,
    private val addFavorite: AddFavoriteUseCase,
    private val removeFavorite: RemoveFavoriteUseCase,
    private val analytics: AnalyticsTracker = NoOpAnalyticsTracker,
) : ViewModel() {
    init {
        analytics.screenView(AnalyticsScreen.PLAYER_DETAIL)
    }

    val uiState: StateFlow<PlayerDetailUiState> = flow {
        val detail = load()
        if (detail == null) {
            emit(PlayerDetailUiState.Unavailable)
        } else {
            observeFavorites().collect { favorites ->
                emit(PlayerDetailUiState.Content(detail, isFavorite = Favorite.Player(playerId) in favorites))
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), PlayerDetailUiState.Loading)

    private val eventChannel = Channel<PlayerDetailEvent>()
    val events: Flow<PlayerDetailEvent> = eventChannel.receiveAsFlow()

    fun onAction(action: PlayerDetailAction) {
        when (action) {
            PlayerDetailAction.FavoriteClicked -> toggleFavorite()
            PlayerDetailAction.ClubClicked -> openClub()
            is PlayerDetailAction.TeamClicked -> openTeam(action.teamId)
        }
    }

    private fun openClub() {
        val content = uiState.value as? PlayerDetailUiState.Content ?: return
        val clubId = content.detail.player.clubId
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

    private suspend fun load() = try {
        getPlayerDetail(playerId)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        null
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
