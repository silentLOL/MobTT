package at.stefanirndorfer.mobtt.feature.club.impl.presentation.fixturedetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.stefanirndorfer.mobtt.core.analytics.AnalyticsFixtureStatus
import at.stefanirndorfer.mobtt.core.analytics.AnalyticsKind
import at.stefanirndorfer.mobtt.core.analytics.AnalyticsScreen
import at.stefanirndorfer.mobtt.core.analytics.AnalyticsTracker
import at.stefanirndorfer.mobtt.core.analytics.NoOpAnalyticsTracker
import at.stefanirndorfer.mobtt.core.analytics.detailOpened
import at.stefanirndorfer.mobtt.core.analytics.fixtureShareTapped
import at.stefanirndorfer.mobtt.core.analytics.fixtureVenueTapped
import at.stefanirndorfer.mobtt.core.analytics.screenView
import at.stefanirndorfer.mobtt.core.ui.RetryTrigger
import at.stefanirndorfer.mobtt.domain.club.FixtureLinks
import at.stefanirndorfer.mobtt.domain.club.FixtureStatus
import at.stefanirndorfer.mobtt.domain.club.GetFixtureDetailUseCase
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.fixturedetail.formatting.venueSearchText
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.fixturedetail.models.FixtureDetailAction
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.fixturedetail.models.FixtureDetailEvent
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.fixturedetail.models.FixtureDetailUiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FixtureDetailViewModel(
    private val fixtureId: String,
    private val getFixtureDetail: GetFixtureDetailUseCase,
    private val fixtureLinks: FixtureLinks,
    private val analytics: AnalyticsTracker = NoOpAnalyticsTracker,
) : ViewModel() {
    init {
        analytics.screenView(AnalyticsScreen.FIXTURE_DETAIL)
    }

    private val retry = RetryTrigger()

    val uiState: StateFlow<FixtureDetailUiState> = retry.restarting {
        flow {
            emit(FixtureDetailUiState.Loading)
            emit(load())
        }
    }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), FixtureDetailUiState.Loading)

    private val eventChannel = Channel<FixtureDetailEvent>()
    val events: Flow<FixtureDetailEvent> = eventChannel.receiveAsFlow()

    fun onAction(action: FixtureDetailAction) {
        when (action) {
            FixtureDetailAction.RetryClicked -> retry.retry()
            is FixtureDetailAction.TeamClicked -> openTeam(action.teamId)
            FixtureDetailAction.ShareClicked -> share()
            FixtureDetailAction.VenueClicked -> openVenue()
        }
    }

    private fun openTeam(teamId: String) {
        analytics.detailOpened(AnalyticsKind.TEAM, teamId, from = AnalyticsScreen.FIXTURE_DETAIL)
        viewModelScope.launch { eventChannel.send(FixtureDetailEvent.OpenTeam(teamId)) }
    }

    private fun share() {
        val content = uiState.value as? FixtureDetailUiState.Content ?: return
        val fixture = content.detail.fixture
        analytics.fixtureShareTapped(fixture.status.analyticsStatus())
        viewModelScope.launch { eventChannel.send(FixtureDetailEvent.Share(content.detail, fixtureLinks.linkTo(fixture.id))) }
    }

    private fun openVenue() {
        val detail = (uiState.value as? FixtureDetailUiState.Content)?.detail ?: return
        val searchText = detail.venueSearchText() ?: return
        analytics.fixtureVenueTapped(detail.fixture.status.analyticsStatus())
        viewModelScope.launch { eventChannel.send(FixtureDetailEvent.OpenVenue(searchText)) }
    }

    private fun FixtureStatus.analyticsStatus(): String = when (this) {
        is FixtureStatus.Finished -> AnalyticsFixtureStatus.FINISHED
        FixtureStatus.Scheduled -> AnalyticsFixtureStatus.SCHEDULED
    }

    private suspend fun load(): FixtureDetailUiState = try {
        getFixtureDetail(fixtureId)?.let { FixtureDetailUiState.Content(it, canShare = fixtureLinks.sharingEnabled) } ?: FixtureDetailUiState.Error
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        FixtureDetailUiState.Error
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
