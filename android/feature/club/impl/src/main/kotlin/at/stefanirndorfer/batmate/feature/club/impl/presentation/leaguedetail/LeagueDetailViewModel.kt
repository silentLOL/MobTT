package at.stefanirndorfer.batmate.feature.club.impl.presentation.leaguedetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.stefanirndorfer.batmate.core.ui.RetryTrigger
import at.stefanirndorfer.batmate.domain.club.GetLeagueDetailUseCase
import at.stefanirndorfer.batmate.feature.club.impl.presentation.leaguedetail.models.LeagueDetailAction
import at.stefanirndorfer.batmate.feature.club.impl.presentation.leaguedetail.models.LeagueDetailUiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

class LeagueDetailViewModel(
    private val leagueId: String,
    private val highlightTeamId: String?,
    private val getLeagueDetail: GetLeagueDetailUseCase,
) : ViewModel() {
    private val retry = RetryTrigger()

    private val onlyTeamFixtures = MutableStateFlow(false)

    val uiState: StateFlow<LeagueDetailUiState> = combine(
        retry.restarting {
            flow {
                emit(LeagueDetailUiState.Loading)
                emit(load())
            }
        },
        onlyTeamFixtures,
    ) { state, onlyTeam ->
        if (state is LeagueDetailUiState.Content) {
            state.copy(onlyTeamFixtures = onlyTeam)
        } else {
            state
        }
    }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), LeagueDetailUiState.Loading)

    fun onAction(action: LeagueDetailAction) {
        when (action) {
            LeagueDetailAction.RetryClicked -> retry.retry()
            is LeagueDetailAction.OnlyTeamFixturesToggled -> onlyTeamFixtures.value = action.enabled
        }
    }

    private suspend fun load(): LeagueDetailUiState = try {
        getLeagueDetail(leagueId)?.let { LeagueDetailUiState.Content(it, highlightTeamId) } ?: LeagueDetailUiState.Error
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        LeagueDetailUiState.Error
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
