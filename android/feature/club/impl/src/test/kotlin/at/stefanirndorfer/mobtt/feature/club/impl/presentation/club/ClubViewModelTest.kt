package at.stefanirndorfer.mobtt.feature.club.impl.presentation.club

import at.stefanirndorfer.mobtt.core.analytics.LoggedEvent
import at.stefanirndorfer.mobtt.core.analytics.RecordingAnalyticsTracker
import at.stefanirndorfer.mobtt.domain.club.GetClubOverviewUseCase
import at.stefanirndorfer.mobtt.domain.club.LineupPlayer
import at.stefanirndorfer.mobtt.domain.club.ListAssociationClubsUseCase
import at.stefanirndorfer.mobtt.domain.club.ListAssociationsUseCase
import at.stefanirndorfer.mobtt.domain.user.AddFavoriteUseCase
import at.stefanirndorfer.mobtt.domain.user.Favorite
import at.stefanirndorfer.mobtt.domain.user.ObserveFavoritesUseCase
import at.stefanirndorfer.mobtt.domain.user.ObserveUserContextUseCase
import at.stefanirndorfer.mobtt.domain.user.RememberPlayerUseCase
import at.stefanirndorfer.mobtt.domain.user.RemoveFavoriteUseCase
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.FakeClubData
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.club.models.ClubAction
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.club.models.ClubSource
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.club.models.ClubUiState
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
    private val analytics = RecordingAnalyticsTracker()

    @Before
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @After
    fun tearDown() = Dispatchers.resetMain()

    private fun TestScope.viewModel(source: ClubSource = ClubSource.Specific("c1")): ClubViewModel {
        val viewModel = ClubViewModel(
            source = source,
            observeUserContext = ObserveUserContextUseCase(data, data, data, data),
            getClubOverview = GetClubOverviewUseCase(data, data, data, data, data),
            observeFavorites = ObserveFavoritesUseCase(data),
            addFavorite = AddFavoriteUseCase(data, data),
            removeFavorite = RemoveFavoriteUseCase(data),
            rememberPlayer = RememberPlayerUseCase(data),
            listAssociations = ListAssociationsUseCase(data),
            listAssociationClubs = ListAssociationClubsUseCase(data),
            analytics = analytics,
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

    @Test
    fun withoutAClubTheTabOffersTheAssociationAndClubPicker() = runTest {
        val viewModel = viewModel(ClubSource.Mine)

        val state = assertIs<ClubUiState.NoClub>(viewModel.uiState.value)
        assertEquals(null, state.picker.association)
    }

    @Test
    fun pickingAClubInTheTabShowsItWithoutSavingItAsMyClub() = runTest {
        val viewModel = viewModel(ClubSource.Mine)

        viewModel.onAction(ClubAction.AssociationRowClicked)
        viewModel.onAction(ClubAction.PickerEntryClicked("a1"))
        viewModel.onAction(ClubAction.ClubRowClicked)
        viewModel.onAction(ClubAction.PickerEntryClicked("c1"))

        assertEquals("c1", assertIs<ClubUiState.Content>(viewModel.uiState.value).overview.club.id)
        assertEquals(emptyList(), data.saved)
    }

    @Test
    fun reportsTheScreenViewTheFavoriteAndTheDetailsOpened() = runTest {
        val viewModel = viewModel()

        viewModel.onAction(ClubAction.FavoriteClicked)
        viewModel.onAction(ClubAction.TeamClicked("t1"))
        viewModel.onAction(ClubAction.PlayerClicked(LineupPlayer("p1", "Anna Gruber")))

        assertEquals(
            listOf(
                LoggedEvent("screen_view", mapOf("screen" to "club")),
                LoggedEvent("favorite_toggled", mapOf("kind" to "club", "id" to "c1", "enabled" to "true")),
                LoggedEvent("detail_opened", mapOf("kind" to "team", "id" to "t1", "from" to "club")),
                LoggedEvent("detail_opened", mapOf("kind" to "player", "id" to "p1", "from" to "club")),
            ),
            analytics.events,
        )
    }
}
