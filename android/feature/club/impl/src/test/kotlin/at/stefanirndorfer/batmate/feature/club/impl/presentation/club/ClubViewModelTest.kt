package at.stefanirndorfer.batmate.feature.club.impl.presentation.club

import at.stefanirndorfer.batmate.domain.club.GetClubOverviewUseCase
import at.stefanirndorfer.batmate.domain.user.AddFavoriteUseCase
import at.stefanirndorfer.batmate.domain.user.Favorite
import at.stefanirndorfer.batmate.domain.user.ObserveFavoritesUseCase
import at.stefanirndorfer.batmate.domain.user.ObserveUserContextUseCase
import at.stefanirndorfer.batmate.domain.user.RememberPlayerUseCase
import at.stefanirndorfer.batmate.domain.user.RemoveFavoriteUseCase
import at.stefanirndorfer.batmate.feature.club.impl.presentation.FakeClubData
import at.stefanirndorfer.batmate.feature.club.impl.presentation.club.models.ClubAction
import at.stefanirndorfer.batmate.feature.club.impl.presentation.club.models.ClubSource
import at.stefanirndorfer.batmate.feature.club.impl.presentation.club.models.ClubUiState
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
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ClubViewModelTest {
    private val data = FakeClubData()

    @Before
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @After
    fun tearDown() = Dispatchers.resetMain()

    private fun TestScope.viewModel(): ClubViewModel {
        val viewModel = ClubViewModel(
            source = ClubSource.Specific("c1"),
            observeUserContext = ObserveUserContextUseCase(data, data, data, data),
            getClubOverview = GetClubOverviewUseCase(data, data, data, data, data),
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
        assertEquals(ClubUiState.Error, viewModel.uiState.value)

        data.unavailable = false
        viewModel.onAction(ClubAction.RetryClicked)

        assertIs<ClubUiState.Content>(viewModel.uiState.value)
    }

    @Test
    fun retryWhileStillUnavailableStaysOnError() = runTest {
        data.unavailable = true
        val viewModel = viewModel()

        viewModel.onAction(ClubAction.RetryClicked)

        assertEquals(ClubUiState.Error, viewModel.uiState.value)
    }

    @Test
    fun aClubWithoutTeamsIsContentNotAnError() = runTest {
        data.teams = emptyList()
        val viewModel = viewModel()

        val state = assertIs<ClubUiState.Content>(viewModel.uiState.value)
        assertTrue(state.overview.teams.isEmpty())
    }

    @Test
    fun aClubThatIsNotFavoriteShowsAsNotFavorite() = runTest {
        val viewModel = viewModel()

        assertFalse(assertIs<ClubUiState.Content>(viewModel.uiState.value).isFavorite)
    }

    @Test
    fun tappingTheStarAddsTheClubAsFavoriteAndTappingAgainRemovesIt() = runTest {
        val viewModel = viewModel()

        viewModel.onAction(ClubAction.FavoriteClicked)
        assertEquals(setOf<Favorite>(Favorite.Club("c1")), data.favorites.value)
        assertTrue(assertIs<ClubUiState.Content>(viewModel.uiState.value).isFavorite)

        viewModel.onAction(ClubAction.FavoriteClicked)
        assertEquals(emptySet(), data.favorites.value)
        assertFalse(assertIs<ClubUiState.Content>(viewModel.uiState.value).isFavorite)
    }

    @Test
    fun aClubAlreadyFavoriteShowsAsFavoriteAndOtherFavoritesStayUntouched() = runTest {
        data.favorites.value = setOf(Favorite.Club("c1"), Favorite.Team("t1"))
        val viewModel = viewModel()
        assertTrue(assertIs<ClubUiState.Content>(viewModel.uiState.value).isFavorite)

        viewModel.onAction(ClubAction.FavoriteClicked)

        assertEquals(setOf<Favorite>(Favorite.Team("t1")), data.favorites.value)
    }

    @Test
    fun tappingTheStarWithoutContentChangesNothing() = runTest {
        data.unavailable = true
        val viewModel = viewModel()

        viewModel.onAction(ClubAction.FavoriteClicked)

        assertEquals(emptySet(), data.favorites.value)
    }
}
