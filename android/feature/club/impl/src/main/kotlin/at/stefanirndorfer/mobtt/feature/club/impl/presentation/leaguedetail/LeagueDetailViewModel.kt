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
import at.stefanirndorfer.mobtt.core.ui.section.lazySection
import at.stefanirndorfer.mobtt.domain.club.GetLeagueDetailUseCase
import at.stefanirndorfer.mobtt.domain.club.GetLeagueFixturesUseCase
import at.stefanirndorfer.mobtt.domain.user.GetPlayerRankingUseCase
import at.stefanirndorfer.mobtt.domain.user.ObserveUserContextUseCase
import at.stefanirndorfer.mobtt.domain.user.UserContext
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.leaguedetail.models.LeagueDetailAction
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.leaguedetail.models.LeagueDetailEvent
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.leaguedetail.models.LeagueDetailUiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LeagueDetailViewModel(
    private val leagueId: String,
    private val highlightTeamId: String?,
    private val openedFromTeamId: String?,
    private val getLeagueDetail: GetLeagueDetailUseCase,
    private val getLeagueFixtures: GetLeagueFixturesUseCase,
    private val getPlayerRanking: GetPlayerRankingUseCase,
    observeUserContext: ObserveUserContextUseCase,
    private val analytics: AnalyticsTracker = NoOpAnalyticsTracker,
    reportsScreenView: Boolean = true,
) : ViewModel() {
    init {
        if (reportsScreenView) {
            analytics.screenView(AnalyticsScreen.LEAGUE_DETAIL)
        }
    }

    private val retry = RetryTrigger()

    private val onlyTeamFixtures = MutableStateFlow(false)

    private val myPlayerId: Flow<String?> = observeUserContext()
        .map { (it as? UserContext.MyPlayer)?.player?.id }
        .onStart { emit(null) }
        .catch { emit(null) }

    private val fixturesExpanded = MutableStateFlow(false)

    private val rankingExpanded = MutableStateFlow(false)

    private val fixtures = retry.lazySection(fixturesExpanded) { getLeagueFixtures(leagueId) }

    private val ranking = retry.lazySection(rankingExpanded) { getPlayerRanking(leagueId) }

    val uiState: StateFlow<LeagueDetailUiState> = combine(
        retry.restarting {
            flow {
                emit(LeagueDetailUiState.Loading)
                emit(load())
            }
        },
        combine(fixtures, fixturesExpanded, onlyTeamFixtures, ::Triple),
        combine(ranking, rankingExpanded, ::Pair),
        myPlayerId,
    ) { state, (fixtures, fixturesOpen, onlyTeam), (ranking, rankingOpen), myPlayer ->
        if (state is LeagueDetailUiState.Content) {
            state.copy(
                fixtures = fixtures,
                fixturesExpanded = fixturesOpen,
                onlyTeamFixtures = onlyTeam,
                ranking = ranking,
                rankingExpanded = rankingOpen,
                myPlayerId = myPlayer,
            )
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
            is LeagueDetailAction.FixtureInfoClicked -> openFixture(action.fixtureId)
            is LeagueDetailAction.PlayerClicked -> openPlayer(action.playerId)
            LeagueDetailAction.FixturesToggled -> fixturesExpanded.update { !it }
            LeagueDetailAction.RankingToggled -> rankingExpanded.update { !it }
        }
    }

    private fun openTeam(teamId: String) {
        analytics.detailOpened(AnalyticsKind.TEAM, teamId, from = AnalyticsScreen.LEAGUE_DETAIL)
        viewModelScope.launch { eventChannel.send(LeagueDetailEvent.OpenTeam(teamId)) }
    }

    private fun openFixture(fixtureId: String) {
        analytics.detailOpened(AnalyticsKind.FIXTURE, fixtureId, from = AnalyticsScreen.LEAGUE_DETAIL)
        viewModelScope.launch { eventChannel.send(LeagueDetailEvent.OpenFixture(fixtureId)) }
    }

    private fun openPlayer(playerId: String) {
        analytics.detailOpened(AnalyticsKind.PLAYER, playerId, from = AnalyticsScreen.LEAGUE_DETAIL)
        viewModelScope.launch { eventChannel.send(LeagueDetailEvent.OpenPlayer(playerId)) }
    }

    private suspend fun load(): LeagueDetailUiState = try {
        getLeagueDetail(leagueId)?.let { LeagueDetailUiState.Content(it, highlightTeamId, openedFromTeamId = openedFromTeamId) } ?: LeagueDetailUiState.Error
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        LeagueDetailUiState.Error
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
