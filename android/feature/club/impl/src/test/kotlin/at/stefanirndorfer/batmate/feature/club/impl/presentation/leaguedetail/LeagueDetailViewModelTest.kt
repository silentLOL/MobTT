package at.stefanirndorfer.batmate.feature.club.impl.presentation.leaguedetail

import at.stefanirndorfer.batmate.domain.club.GetLeagueDetailUseCase
import at.stefanirndorfer.batmate.feature.club.impl.presentation.FakeClubData
import at.stefanirndorfer.batmate.feature.club.impl.presentation.leaguedetail.models.LeagueDetailAction
import at.stefanirndorfer.batmate.feature.club.impl.presentation.leaguedetail.models.LeagueDetailUiState
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
class LeagueDetailViewModelTest {
    private val data = FakeClubData()

    @Before
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @After
    fun tearDown() = Dispatchers.resetMain()

    private fun TestScope.viewModel(): LeagueDetailViewModel {
        val viewModel = LeagueDetailViewModel("l1", GetLeagueDetailUseCase(data, data))
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
        return viewModel
    }

    @Test
    fun unavailableDataShowsErrorAndRetryRecovers() = runTest {
        data.unavailable = true
        val viewModel = viewModel()
        assertEquals(LeagueDetailUiState.Error, viewModel.uiState.value)

        data.unavailable = false
        viewModel.onAction(LeagueDetailAction.RetryClicked)

        assertIs<LeagueDetailUiState.Content>(viewModel.uiState.value)
    }

    @Test
    fun emptyTableAndFixturesAreContentNotAnError() = runTest {
        val viewModel = viewModel()

        val state = assertIs<LeagueDetailUiState.Content>(viewModel.uiState.value)
        assertTrue(state.detail.standings.isEmpty())
        assertTrue(state.detail.fixtures.isEmpty())
    }
}
