package at.stefanirndorfer.batmate.feature.club.impl.presentation.teamdetail

import at.stefanirndorfer.batmate.domain.club.GetTeamDetailUseCase
import at.stefanirndorfer.batmate.feature.club.impl.presentation.FakeClubData
import at.stefanirndorfer.batmate.feature.club.impl.presentation.teamdetail.models.TeamDetailAction
import at.stefanirndorfer.batmate.feature.club.impl.presentation.teamdetail.models.TeamDetailUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class TeamDetailViewModelTest {
    private val data = FakeClubData()

    @Before
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @After
    fun tearDown() = Dispatchers.resetMain()

    private fun TestScope.viewModel(): TeamDetailViewModel {
        val viewModel = TeamDetailViewModel("t1", GetTeamDetailUseCase(data, data, data))
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
        return viewModel
    }

    @Test
    fun unavailableDataShowsErrorAndRetryRecovers() = runTest {
        data.unavailable = true
        val viewModel = viewModel()
        assertEquals(TeamDetailUiState.Error, viewModel.uiState.value)

        data.unavailable = false
        viewModel.onAction(TeamDetailAction.RetryClicked)

        assertIs<TeamDetailUiState.Content>(viewModel.uiState.value)
    }

    @Test
    fun emptyStandingsAndLineupAreContentNotAnError() = runTest {
        val viewModel = viewModel()

        val state = assertIs<TeamDetailUiState.Content>(viewModel.uiState.value)
        assertTrue(state.detail.standings.isEmpty())
        assertTrue(state.detail.lineup.isEmpty())
    }
}
