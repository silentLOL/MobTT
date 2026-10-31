package at.stefanirndorfer.batmate.feature.club.impl.presentation.leaguedetail

import at.stefanirndorfer.batmate.domain.club.Fixture
import at.stefanirndorfer.batmate.domain.club.FixtureStatus
import at.stefanirndorfer.batmate.domain.club.GetLeagueDetailUseCase
import at.stefanirndorfer.batmate.feature.club.impl.presentation.FakeClubData
import at.stefanirndorfer.batmate.feature.club.impl.presentation.leaguedetail.models.LeagueDetailAction
import at.stefanirndorfer.batmate.feature.club.impl.presentation.leaguedetail.models.LeagueDetailEvent
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
import kotlin.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class LeagueDetailViewModelTest {
    private val data = FakeClubData()

    @Before
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @After
    fun tearDown() = Dispatchers.resetMain()

    private fun TestScope.viewModel(highlightTeamId: String? = "t1"): LeagueDetailViewModel {
        val viewModel = LeagueDetailViewModel("l1", highlightTeamId, GetLeagueDetailUseCase(data, data))
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

    @Test
    fun theTeamFilterNarrowsTheFixturesToTheHighlightedTeam() = runTest {
        data.leagueFixtures = listOf(fixture("mine", "t1", "t2"), fixture("other", "t2", "t3"))
        val viewModel = viewModel()
        assertEquals(listOf("mine", "other"), assertIs<LeagueDetailUiState.Content>(viewModel.uiState.value).visibleFixtures.map { it.id })

        viewModel.onAction(LeagueDetailAction.OnlyTeamFixturesToggled(true))
        assertEquals(listOf("mine"), assertIs<LeagueDetailUiState.Content>(viewModel.uiState.value).visibleFixtures.map { it.id })

        viewModel.onAction(LeagueDetailAction.OnlyTeamFixturesToggled(false))
        assertEquals(2, assertIs<LeagueDetailUiState.Content>(viewModel.uiState.value).visibleFixtures.size)
    }

    private fun fixture(id: String, home: String, away: String) = Fixture(
        id = id,
        homeTeamId = home,
        homeTeamName = home,
        awayTeamId = away,
        awayTeamName = away,
        startsAt = Instant.fromEpochSeconds(1_800_000_000),
        venue = null,
        status = FixtureStatus.Scheduled,
    )

    @Test
    fun tappingATeamSendsAnOpenTeamEvent() = runTest {
        val viewModel = viewModel()
        val events = mutableListOf<LeagueDetailEvent>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.events.collect(events::add) }

        viewModel.onAction(LeagueDetailAction.TeamClicked("t2"))

        assertEquals(listOf<LeagueDetailEvent>(LeagueDetailEvent.OpenTeam("t2")), events)
    }
}
