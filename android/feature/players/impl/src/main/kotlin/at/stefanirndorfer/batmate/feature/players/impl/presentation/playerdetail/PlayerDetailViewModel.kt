package at.stefanirndorfer.batmate.feature.players.impl.presentation.playerdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.stefanirndorfer.batmate.domain.user.AddFavoriteUseCase
import at.stefanirndorfer.batmate.domain.user.Favorite
import at.stefanirndorfer.batmate.domain.user.GetPlayerDetailUseCase
import at.stefanirndorfer.batmate.domain.user.ObserveFavoritesUseCase
import at.stefanirndorfer.batmate.domain.user.RemoveFavoriteUseCase
import at.stefanirndorfer.batmate.feature.players.impl.presentation.playerdetail.models.PlayerDetailAction
import at.stefanirndorfer.batmate.feature.players.impl.presentation.playerdetail.models.PlayerDetailUiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PlayerDetailViewModel(
    private val playerId: String,
    private val getPlayerDetail: GetPlayerDetailUseCase,
    private val observeFavorites: ObserveFavoritesUseCase,
    private val addFavorite: AddFavoriteUseCase,
    private val removeFavorite: RemoveFavoriteUseCase,
) : ViewModel() {
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

    fun onAction(action: PlayerDetailAction) {
        when (action) {
            PlayerDetailAction.FavoriteClicked -> toggleFavorite()
        }
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
