package at.stefanirndorfer.batmate.feature.club.impl.presentation.teamdetail

import at.stefanirndorfer.batmate.domain.club.GetTeamDetailUseCase
import at.stefanirndorfer.batmate.domain.club.LineupPlayer
import at.stefanirndorfer.batmate.domain.user.AddFavoriteUseCase
import at.stefanirndorfer.batmate.domain.user.Favorite
import at.stefanirndorfer.batmate.domain.user.ObserveFavoritesUseCase
import at.stefanirndorfer.batmate.domain.user.RememberPlayerUseCase
import at.stefanirndorfer.batmate.domain.user.RemoveFavoriteUseCase
import at.stefanirndorfer.batmate.feature.club.impl.presentation.FakeClubData
import at.stefanirndorfer.batmate.feature.club.impl.presentation.teamdetail.models.TeamDetailAction
import at.stefanirndorfer.batmate.feature.club.impl.presentation.teamdetail.models.TeamDetailEvent
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
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class TeamDetailViewModelTest {
    private val data = FakeClubData()

    @Before
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @After
    fun tearDown() = Dispatchers.resetMain()

    private fun TestScope.viewModel(): TeamDetailViewModel {
        val viewModel = TeamDetailViewModel(
            teamId = "t1",
            getTeamDetail = GetTeamDetailUseCase(data, data, data, data),
            observeFavorites = ObserveFavoritesUseCase(data),
            addFavorite = AddFavoriteUseCase(data, data),
            removeFavorite = RemoveFavoriteUseCase(data),
            rememberPlayer = RememberPlayerUseCase(data),
        )
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
    fun missingStandingAndLineupAreContentNotAnError() = runTest {
        val viewModel = viewModel()

        val state = assertIs<TeamDetailUiState.Content>(viewModel.uiState.value)
        assertNull(state.detail.standing)
        assertTrue(state.detail.lineup.isEmpty())
    }

    @Test
    fun aTeamThatIsNotFavoriteShowsAsNotFavorite() = runTest {
        val viewModel = viewModel()

        assertFalse(assertIs<TeamDetailUiState.Content>(viewModel.uiState.value).isFavorite)
    }

    @Test
    fun tappingTheStarAddsTheTeamAsFavoriteAndTappingAgainRemovesIt() = runTest {
        val viewModel = viewModel()

        viewModel.onAction(TeamDetailAction.FavoriteClicked)
        assertEquals(setOf<Favorite>(Favorite.Team("t1")), data.favorites.value)
        assertTrue(assertIs<TeamDetailUiState.Content>(viewModel.uiState.value).isFavorite)

        viewModel.onAction(TeamDetailAction.FavoriteClicked)
        assertEquals(emptySet(), data.favorites.value)
        assertFalse(assertIs<TeamDetailUiState.Content>(viewModel.uiState.value).isFavorite)
    }

    @Test
    fun aTeamAlreadyFavoriteShowsAsFavoriteAndOtherFavoritesStayUntouched() = runTest {
        data.favorites.value = setOf(Favorite.Team("t1"), Favorite.Club("c1"))
        val viewModel = viewModel()
        assertTrue(assertIs<TeamDetailUiState.Content>(viewModel.uiState.value).isFavorite)

        viewModel.onAction(TeamDetailAction.FavoriteClicked)

        assertEquals(setOf<Favorite>(Favorite.Club("c1")), data.favorites.value)
    }

    @Test
    fun tappingTheStarWithoutContentChangesNothing() = runTest {
        data.unavailable = true
        val viewModel = viewModel()

        viewModel.onAction(TeamDetailAction.FavoriteClicked)

        assertEquals(emptySet(), data.favorites.value)
    }

    @Test
    fun tappingTheLeagueCardSendsAnOpenLeagueEvent() = runTest {
        val viewModel = viewModel()
        val events = mutableListOf<TeamDetailEvent>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.events.collect(events::add) }

        viewModel.onAction(TeamDetailAction.LeagueClicked("l1"))

        assertEquals(listOf<TeamDetailEvent>(TeamDetailEvent.OpenLeague("l1", "t1")), events)
    }

    @Test
    fun tappingALineupPlayerSendsAnOpenPlayerEvent() = runTest {
        val viewModel = viewModel()
        val events = mutableListOf<TeamDetailEvent>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.events.collect(events::add) }
        val player = LineupPlayer(playerId = "p1", name = "Anna Huber")

        viewModel.onAction(TeamDetailAction.PlayerClicked(player))

        assertEquals(listOf<TeamDetailEvent>(TeamDetailEvent.OpenPlayer(player.playerId)), events)
    }
}
