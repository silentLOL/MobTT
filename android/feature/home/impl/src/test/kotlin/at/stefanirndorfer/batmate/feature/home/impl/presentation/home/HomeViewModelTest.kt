package at.stefanirndorfer.batmate.feature.home.impl.presentation.home

import at.stefanirndorfer.batmate.domain.club.Association
import at.stefanirndorfer.batmate.domain.club.AssociationRepository
import at.stefanirndorfer.batmate.domain.club.Club
import at.stefanirndorfer.batmate.domain.club.ClubRepository
import at.stefanirndorfer.batmate.domain.club.DataUnavailable
import at.stefanirndorfer.batmate.domain.club.Fixture
import at.stefanirndorfer.batmate.domain.club.FixtureRepository
import at.stefanirndorfer.batmate.domain.club.GetClubOverviewUseCase
import at.stefanirndorfer.batmate.domain.club.League
import at.stefanirndorfer.batmate.domain.club.LeagueRepository
import at.stefanirndorfer.batmate.domain.club.LineupPlayer
import at.stefanirndorfer.batmate.domain.club.SearchClubsUseCase
import at.stefanirndorfer.batmate.domain.club.Standing
import at.stefanirndorfer.batmate.domain.club.Team
import at.stefanirndorfer.batmate.domain.club.TeamRepository
import at.stefanirndorfer.batmate.domain.player.Player
import at.stefanirndorfer.batmate.domain.player.PlayerRepository
import at.stefanirndorfer.batmate.domain.user.AddFavoriteUseCase
import at.stefanirndorfer.batmate.domain.user.Favorite
import at.stefanirndorfer.batmate.domain.user.FavoritesRepository
import at.stefanirndorfer.batmate.domain.user.ObserveFavoritesUseCase
import at.stefanirndorfer.batmate.domain.user.ObserveResolvedFavoritesUseCase
import at.stefanirndorfer.batmate.domain.user.ObserveUpcomingFixturesUseCase
import at.stefanirndorfer.batmate.domain.user.ObserveUserContextUseCase
import at.stefanirndorfer.batmate.domain.user.RemoveFavoriteUseCase
import at.stefanirndorfer.batmate.domain.user.SearchPlayersUseCase
import at.stefanirndorfer.batmate.domain.user.UpcomingFixtures
import at.stefanirndorfer.batmate.domain.user.UserSelection
import at.stefanirndorfer.batmate.domain.user.UserSelectionRepository
import at.stefanirndorfer.batmate.feature.home.impl.presentation.home.models.HomeAction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
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

/** XTTV stand-in: every read throws [DataUnavailable] while [unavailable] is set. */
private class FakeData :
    ClubRepository,
    AssociationRepository,
    TeamRepository,
    LeagueRepository,
    FixtureRepository,
    PlayerRepository,
    UserSelectionRepository,
    FavoritesRepository {
    var unavailable = false
    var teams = listOf(Team("t1", "Olympic 1", "c1", "l1"))

    private val club = Club("c1", "TTC Olympic", "a1")

    private fun <T> read(value: T): T {
        if (unavailable) {
            throw DataUnavailable("XTTV unreachable")
        }
        return value
    }

    override suspend fun getClub(id: String): Club? = read(club.takeIf { it.id == id })

    override suspend fun searchClubs(query: String): List<Club> = read(listOf(club))

    override suspend fun getAssociation(id: String): Association? = read(Association("a1", "WTTV"))

    override suspend fun getTeam(id: String): Team? = read(teams.firstOrNull { it.id == id })

    override suspend fun getTeams(clubId: String): List<Team> = read(teams)

    override suspend fun getLineup(teamId: String): List<LineupPlayer> = read(emptyList())

    override suspend fun getLeague(id: String): League? = read(League(id, "Liga"))

    override suspend fun getStandings(leagueId: String): List<Standing> = read(emptyList())

    override suspend fun getFixtures(teamId: String): List<Fixture> = read(emptyList())

    override suspend fun getPlayer(id: String): Player? = null

    override suspend fun searchPlayers(query: String): List<Player> = emptyList()

    private val selection = MutableStateFlow<UserSelection>(UserSelection.MyClub("c1"))

    override fun observeSelection(): Flow<UserSelection> = selection

    override suspend fun saveSelection(selection: UserSelection) {
        this.selection.value = selection
    }

    private val favorites = MutableStateFlow<Set<Favorite>>(emptySet())

    override fun observeFavorites(): Flow<Set<Favorite>> = favorites

    override suspend fun add(favorite: Favorite) {
        favorites.value += favorite
    }

    override suspend fun remove(favorite: Favorite) {
        favorites.value -= favorite
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    private val data = FakeData()

    @Before
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @After
    fun tearDown() = Dispatchers.resetMain()

    private fun TestScope.viewModel(): HomeViewModel {
        val userContext = ObserveUserContextUseCase(data, data, data, data)
        val viewModel = HomeViewModel(
            observeUserContext = userContext,
            observeResolvedFavorites = ObserveResolvedFavoritesUseCase(data, data, data, data),
            observeUpcomingFixtures = ObserveUpcomingFixturesUseCase(
                userContext,
                GetClubOverviewUseCase(data, data, data, data, data),
            ),
            observeFavorites = ObserveFavoritesUseCase(data),
            addFavorite = AddFavoriteUseCase(data),
            removeFavorite = RemoveFavoriteUseCase(data),
            searchPlayers = SearchPlayersUseCase(data, data),
            searchClubs = SearchClubsUseCase(data),
        )
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
        return viewModel
    }

    @Test
    fun unavailableDataShowsUnavailableAndRetryRecovers() = runTest {
        data.unavailable = true
        val viewModel = viewModel()
        assertEquals(UpcomingFixtures.Unavailable, viewModel.uiState.value.fixtures)

        data.unavailable = false
        viewModel.onAction(HomeAction.RetryFixturesClicked)

        assertIs<UpcomingFixtures.Loaded>(viewModel.uiState.value.fixtures)
    }

    @Test
    fun retryWhileStillUnavailableStaysUnavailable() = runTest {
        data.unavailable = true
        val viewModel = viewModel()

        viewModel.onAction(HomeAction.RetryFixturesClicked)

        assertEquals(UpcomingFixtures.Unavailable, viewModel.uiState.value.fixtures)
    }

    @Test
    fun aClubWithoutTeamsIsLoadedNotUnavailable() = runTest {
        data.teams = emptyList()
        val viewModel = viewModel()

        val fixtures = assertIs<UpcomingFixtures.Loaded>(viewModel.uiState.value.fixtures)
        assertTrue(fixtures.entries.isEmpty())
    }
}
