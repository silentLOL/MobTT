package at.stefanirndorfer.batmate.feature.club.impl.presentation.club

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.stefanirndorfer.batmate.core.ui.RetryTrigger
import at.stefanirndorfer.batmate.core.ui.catchUnavailable
import at.stefanirndorfer.batmate.domain.club.GetClubOverviewUseCase
import at.stefanirndorfer.batmate.domain.user.ObserveUserContextUseCase
import at.stefanirndorfer.batmate.feature.club.impl.presentation.club.models.ClubAction
import at.stefanirndorfer.batmate.feature.club.impl.presentation.club.models.ClubEvent
import at.stefanirndorfer.batmate.feature.club.impl.presentation.club.models.ClubSource
import at.stefanirndorfer.batmate.feature.club.impl.presentation.club.models.ClubUiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.launch

class ClubViewModel(
    source: ClubSource,
    observeUserContext: ObserveUserContextUseCase,
    private val getClubOverview: GetClubOverviewUseCase,
) : ViewModel() {
    private val retry = RetryTrigger()

    private val clubId: Flow<String?> = when (source) {
        ClubSource.Mine -> observeUserContext().map { it.club?.id }
        is ClubSource.Specific -> flowOf(source.clubId)
    }

    val uiState: StateFlow<ClubUiState> = retry
        .restarting { states() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), ClubUiState.Loading)

    // My Club resolves through XTTV too, so the stream can end at a DataUnavailable; a retry restarts it.
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun states(): Flow<ClubUiState> = clubId
        .transformLatest { id ->
            if (id == null) {
                emit(ClubUiState.NoClub)
            } else {
                emit(ClubUiState.Loading)
                emit(load(id))
            }
        }
        .catchUnavailable(ClubUiState.Error)

    private val eventChannel = Channel<ClubEvent>()
    val events: Flow<ClubEvent> = eventChannel.receiveAsFlow()

    fun onAction(action: ClubAction) {
        when (action) {
            ClubAction.ChooseClicked -> viewModelScope.launch { eventChannel.send(ClubEvent.OpenSettings) }
            is ClubAction.TeamClicked -> viewModelScope.launch { eventChannel.send(ClubEvent.OpenTeam(action.teamId)) }
            ClubAction.RetryClicked -> retry.retry()
        }
    }

    private suspend fun load(clubId: String): ClubUiState = try {
        getClubOverview(clubId)?.let(ClubUiState::Content) ?: ClubUiState.Error
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        ClubUiState.Error
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
