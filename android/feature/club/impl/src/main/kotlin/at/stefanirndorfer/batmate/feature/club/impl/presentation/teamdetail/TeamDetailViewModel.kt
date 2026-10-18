package at.stefanirndorfer.batmate.feature.club.impl.presentation.teamdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.stefanirndorfer.batmate.core.ui.RetryTrigger
import at.stefanirndorfer.batmate.domain.club.GetTeamDetailUseCase
import at.stefanirndorfer.batmate.feature.club.impl.presentation.teamdetail.models.TeamDetailAction
import at.stefanirndorfer.batmate.feature.club.impl.presentation.teamdetail.models.TeamDetailUiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

class TeamDetailViewModel(
    private val teamId: String,
    private val getTeamDetail: GetTeamDetailUseCase,
) : ViewModel() {
    private val retry = RetryTrigger()

    val uiState: StateFlow<TeamDetailUiState> = retry
        .restarting {
            flow {
                emit(TeamDetailUiState.Loading)
                emit(load())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), TeamDetailUiState.Loading)

    fun onAction(action: TeamDetailAction) {
        when (action) {
            TeamDetailAction.RetryClicked -> retry.retry()
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
