package at.stefanirndorfer.mobtt.feature.club.impl.presentation.leaguedetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.stefanirndorfer.mobtt.core.analytics.AnalyticsKind
import at.stefanirndorfer.mobtt.core.analytics.AnalyticsScreen
import at.stefanirndorfer.mobtt.core.analytics.AnalyticsTracker
import at.stefanirndorfer.mobtt.core.analytics.NoOpAnalyticsTracker
import at.stefanirndorfer.mobtt.core.analytics.detailOpened
import at.stefanirndorfer.mobtt.core.analytics.screenView
import at.stefanirndorfer.mobtt.core.ui.RetryTrigger
import at.stefanirndorfer.mobtt.domain.club.GetLeagueDetailUseCase
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.leaguedetail.models.LeagueDetailAction
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.leaguedetail.models.LeagueDetailEvent
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.leaguedetail.models.LeagueDetailUiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LeagueDetailViewModel(
    private val leagueId: String,
    private val highlightTeamId: String?,
    private val getLeagueDetail: GetLeagueDetailUseCase,
    private val analytics: AnalyticsTracker = NoOpAnalyticsTracker,
) : ViewModel() {
    init {
        analytics.screenView(AnalyticsScreen.LEAGUE_DETAIL)
    }

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

    private val eventChannel = Channel<LeagueDetailEvent>()
    val events: Flow<LeagueDetailEvent> = eventChannel.receiveAsFlow()

    fun onAction(action: LeagueDetailAction) {
        when (action) {
            LeagueDetailAction.RetryClicked -> retry.retry()
            is LeagueDetailAction.OnlyTeamFixturesToggled -> onlyTeamFixtures.value = action.enabled
            is LeagueDetailAction.TeamClicked -> openTeam(action.teamId)
        }
    }

    private fun openTeam(teamId: String) {
        analytics.detailOpened(AnalyticsKind.TEAM, teamId, from = AnalyticsScreen.LEAGUE_DETAIL)
        viewModelScope.launch { eventChannel.send(LeagueDetailEvent.OpenTeam(teamId)) }
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
