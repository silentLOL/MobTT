package at.stefanirndorfer.mobtt.feature.club.impl.presentation.leaguetab

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.stefanirndorfer.mobtt.core.analytics.AnalyticsScreen
import at.stefanirndorfer.mobtt.core.analytics.AnalyticsTracker
import at.stefanirndorfer.mobtt.core.analytics.NoOpAnalyticsTracker
import at.stefanirndorfer.mobtt.core.analytics.screenView
import at.stefanirndorfer.mobtt.core.ui.RetryTrigger
import at.stefanirndorfer.mobtt.core.ui.catchUnavailable
import at.stefanirndorfer.mobtt.domain.user.MyTeams
import at.stefanirndorfer.mobtt.domain.user.ObserveMyTeamsUseCase
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.leaguetab.models.LeagueTabAction
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.leaguetab.models.LeagueTabEvent
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.leaguetab.models.LeagueTabUiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** The League tab: the League of My Player's Team, or why there is none. */
class LeagueTabViewModel(
    observeMyTeams: ObserveMyTeamsUseCase,
    private val analytics: AnalyticsTracker = NoOpAnalyticsTracker,
) : ViewModel() {
    init {
        analytics.screenView(AnalyticsScreen.LEAGUE)
    }

    private val retry = RetryTrigger()

    val uiState: StateFlow<LeagueTabUiState> = retry.restarting {
        observeMyTeams()
            .map { it.toUiState() }
            .onStart { emit(LeagueTabUiState.Loading) }
            .catchUnavailable(LeagueTabUiState.Unavailable)
    }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), LeagueTabUiState.Loading)

    private val eventChannel = Channel<LeagueTabEvent>()
    val events: Flow<LeagueTabEvent> = eventChannel.receiveAsFlow()

    fun onAction(action: LeagueTabAction) {
        when (action) {
            LeagueTabAction.RetryClicked -> retry.retry()
            LeagueTabAction.ChoosePlayerClicked -> send(LeagueTabEvent.OpenChoosePlayer)
            is LeagueTabAction.MyPlayerClicked -> send(LeagueTabEvent.OpenPlayer(action.playerId))
        }
    }

    private fun send(event: LeagueTabEvent) {
        viewModelScope.launch { eventChannel.send(event) }
    }

    private fun MyTeams.toUiState(): LeagueTabUiState = when (this) {
        MyTeams.NoMyPlayer -> LeagueTabUiState.NoMyPlayer
        is MyTeams.Found -> teams.firstOrNull()?.let { detail ->
            detail.league?.let { LeagueTabUiState.League(leagueId = it.id, teamId = detail.team.id) }
        } ?: LeagueTabUiState.NoTeam(playerId = player.id, playerName = player.name)
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
