package at.stefanirndorfer.batmate.feature.club.impl.presentation.teamdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.stefanirndorfer.batmate.core.ui.RetryTrigger
import at.stefanirndorfer.batmate.domain.club.GetTeamDetailUseCase
import at.stefanirndorfer.batmate.domain.user.AddFavoriteUseCase
import at.stefanirndorfer.batmate.domain.user.Favorite
import at.stefanirndorfer.batmate.domain.user.ObserveFavoritesUseCase
import at.stefanirndorfer.batmate.domain.user.RemoveFavoriteUseCase
import at.stefanirndorfer.batmate.feature.club.impl.presentation.teamdetail.models.TeamDetailAction
import at.stefanirndorfer.batmate.feature.club.impl.presentation.teamdetail.models.TeamDetailUiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TeamDetailViewModel(
    private val teamId: String,
    private val getTeamDetail: GetTeamDetailUseCase,
    private val observeFavorites: ObserveFavoritesUseCase,
    private val addFavorite: AddFavoriteUseCase,
    private val removeFavorite: RemoveFavoriteUseCase,
) : ViewModel() {
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

    fun onAction(action: TeamDetailAction) {
        when (action) {
            TeamDetailAction.RetryClicked -> retry.retry()
            TeamDetailAction.FavoriteClicked -> toggleFavorite()
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
