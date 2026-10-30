package at.stefanirndorfer.batmate.feature.home.impl.presentation.home

import at.stefanirndorfer.batmate.core.ui.directory.models.PlayerStep
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
import at.stefanirndorfer.batmate.domain.club.ListAssociationsUseCase
import at.stefanirndorfer.batmate.domain.club.ListLeaguesUseCase
import at.stefanirndorfer.batmate.domain.club.SearchClubsUseCase
import at.stefanirndorfer.batmate.domain.club.Standing
import at.stefanirndorfer.batmate.domain.club.Team
import at.stefanirndorfer.batmate.domain.club.TeamRepository
import at.stefanirndorfer.batmate.domain.player.Player
import at.stefanirndorfer.batmate.domain.player.PlayerRepository
import at.stefanirndorfer.batmate.domain.user.AddFavoriteUseCase
import at.stefanirndorfer.batmate.domain.user.Favorite
import at.stefanirndorfer.batmate.domain.user.FavoritesRepository
import at.stefanirndorfer.batmate.domain.user.ListLeaguePlayersUseCase
import at.stefanirndorfer.batmate.domain.user.ObserveFavoritesUseCase
import at.stefanirndorfer.batmate.domain.user.ObserveHomeFavoritesUseCase
import at.stefanirndorfer.batmate.domain.user.ObserveResolvedFavoritesUseCase
import at.stefanirndorfer.batmate.domain.user.ObserveUpcomingFixturesUseCase
import at.stefanirndorfer.batmate.domain.user.ObserveUserContextUseCase
import at.stefanirndorfer.batmate.domain.user.RemoveFavoriteUseCase
import at.stefanirndorfer.batmate.domain.user.ResolvedFavorite
import at.stefanirndorfer.batmate.domain.user.UpcomingFixtures
import at.stefanirndorfer.batmate.domain.user.UserSelection
import at.stefanirndorfer.batmate.domain.user.UserSelectionRepository
import at.stefanirndorfer.batmate.feature.home.impl.presentation.home.models.HomeAction
import at.stefanirndorfer.batmate.feature.home.impl.presentation.home.models.HomeEvent
import at.stefanirndorfer.batmate.feature.home.impl.presentation.home.models.HomeSelectionUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
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
import kotlin.test.assertNull
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

    override suspend fun getAssociations(): List<Association> = read(listOf(Association("a1", "WTTV")))

    override suspend fun getTeam(id: String): Team? = read(teams.firstOrNull { it.id == id })

    override suspend fun getTeams(clubId: String): List<Team> = read(teams)

    override suspend fun getLineup(teamId: String): List<LineupPlayer> = read(emptyList())

    override suspend fun getLeague(id: String): League? = read(League(id, "Liga"))

    override suspend fun getLeagues(associationId: String): List<League> = read(listOf(League("l1", "Liga")))

    override suspend fun getStandings(leagueId: String): List<Standing> = read(emptyList())

    override suspend fun getFixtures(teamId: String): List<Fixture> = read(emptyList())

    override suspend fun getPlayer(id: String): Player? = null

    var leaguePlayerReads = 0

    override suspend fun getLeaguePlayers(leagueId: String): List<Player> {
        leaguePlayerReads++
        return read(listOf(Player("p1", "Anna Gruber", "c1"), Player("p2", "Jakob Irndorfer", "c1")))
    }

    private val selection = MutableStateFlow<UserSelection>(UserSelection.MyClub("c1"))

    override fun observeSelection(): Flow<UserSelection> = selection

    override suspend fun saveSelection(selection: UserSelection) {
        this.selection.value = selection
    }

    val favorites = MutableStateFlow<Set<Favorite>>(emptySet())

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
            observeHomeFavorites = ObserveHomeFavoritesUseCase(ObserveResolvedFavoritesUseCase(data, data, data, data, data), data),
            observeUpcomingFixtures = ObserveUpcomingFixturesUseCase(
                userContext,
                GetClubOverviewUseCase(data, data, data, data, data),
            ),
            observeFavorites = ObserveFavoritesUseCase(data),
            addFavorite = AddFavoriteUseCase(data, data),
            removeFavorite = RemoveFavoriteUseCase(data),
            searchClubs = SearchClubsUseCase(data),
            listAssociations = ListAssociationsUseCase(data),
            listLeagues = ListLeaguesUseCase(data),
            listLeaguePlayers = ListLeaguePlayersUseCase(data, data),
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
    fun unavailableDataDoesNotAskTheUserToChooseAgainAndRetryRecovers() = runTest {
        data.unavailable = true
        val viewModel = viewModel()
        assertEquals(HomeSelectionUi.Unavailable, viewModel.uiState.value.selection)

        data.unavailable = false
        viewModel.onAction(HomeAction.RetryFixturesClicked)

        assertEquals(HomeSelectionUi.Club("TTC Olympic", "WTTV"), viewModel.uiState.value.selection)
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

    @Test
    fun addingAClubFromThePickerShowsItAsAResolvedFavoriteAndMarksItSelected() = runTest {
        data.saveSelection(UserSelection.None)
        val viewModel = viewModel()
        viewModel.onAction(HomeAction.AddFavoriteClicked)

        viewModel.onAction(HomeAction.PickerClubClicked("c1"))

        val state = viewModel.uiState.value
        assertEquals(listOf(ResolvedFavorite(Favorite.Club("c1"), "TTC Olympic", "WTTV")), state.favorites)
        assertTrue(state.picker!!.clubs.single().selected)
    }

    @Test
    fun clickingASelectedPickerEntryAgainRemovesTheFavorite() = runTest {
        data.saveSelection(UserSelection.None)
        val viewModel = viewModel()
        viewModel.onAction(HomeAction.AddFavoriteClicked)
        viewModel.onAction(HomeAction.PickerClubClicked("c1"))

        viewModel.onAction(HomeAction.PickerClubClicked("c1"))

        val state = viewModel.uiState.value
        assertTrue(state.favorites.isEmpty())
        assertTrue(state.picker!!.clubs.none { it.selected })
    }

    @Test
    fun removingAFavoriteFromHomeDropsItFromTheList() = runTest {
        data.saveSelection(UserSelection.None)
        data.add(Favorite.Club("c1"))
        val viewModel = viewModel()
        assertEquals(1, viewModel.uiState.value.favorites.size)

        viewModel.onAction(HomeAction.RemoveFavoriteClicked(Favorite.Club("c1")))

        assertTrue(viewModel.uiState.value.favorites.isEmpty())
    }

    @Test
    fun aFavoriteThatCannotBeResolvedIsLeftOut() = runTest {
        data.add(Favorite.Club("gone"))
        val viewModel = viewModel()

        assertTrue(viewModel.uiState.value.favorites.isEmpty())
    }

    @Test
    fun dismissingThePickerClosesItAndKeepsTheFavorites() = runTest {
        data.saveSelection(UserSelection.None)
        val viewModel = viewModel()
        viewModel.onAction(HomeAction.AddFavoriteClicked)
        viewModel.onAction(HomeAction.PickerClubClicked("c1"))

        viewModel.onAction(HomeAction.PickerDismissed)

        val state = viewModel.uiState.value
        assertNull(state.picker)
        assertEquals(1, state.favorites.size)
    }

    @Test
    fun unavailableSearchLeavesThePickerOpenAndEmpty() = runTest {
        val viewModel = viewModel()
        data.unavailable = true

        viewModel.onAction(HomeAction.AddFavoriteClicked)

        val picker = viewModel.uiState.value.picker!!
        assertTrue(picker.clubs.isEmpty())
        assertTrue(picker.entries.isEmpty())
    }

    @Test
    fun browsingPlayersDrillsFromAssociationToLeagueToPlayersAndTogglesAFavorite() = runTest {
        val viewModel = viewModel()
        viewModel.onAction(HomeAction.AddFavoriteClicked)

        viewModel.onAction(HomeAction.PickerBrowsePlayersClicked)
        assertEquals(PlayerStep.ASSOCIATION, viewModel.uiState.value.picker!!.step)
        assertEquals(listOf("a1"), viewModel.uiState.value.picker!!.entries.map { it.id })

        viewModel.onAction(HomeAction.PickerPlayerClicked("a1"))
        assertEquals(PlayerStep.LEAGUE, viewModel.uiState.value.picker!!.step)
        assertEquals("WTTV", viewModel.uiState.value.picker!!.breadcrumb)

        viewModel.onAction(HomeAction.PickerPlayerClicked("l1"))
        val players = viewModel.uiState.value.picker!!
        assertEquals(PlayerStep.PLAYER, players.step)
        assertEquals("WTTV › Liga", players.breadcrumb)
        assertEquals(listOf("p1", "p2"), players.entries.map { it.id })

        viewModel.onAction(HomeAction.PickerPlayerClicked("p2"))

        assertEquals(setOf(Favorite.Player("p2")), data.observeFavorites().first())
        assertEquals(listOf("p2"), viewModel.uiState.value.picker!!.entries.filter { it.selected }.map { it.id })
    }

    @Test
    fun theQueryFiltersTheCurrentStepLocallyWithoutReadingXttvAgain() = runTest {
        val viewModel = viewModel()
        viewModel.onAction(HomeAction.AddFavoriteClicked)
        viewModel.onAction(HomeAction.PickerBrowsePlayersClicked)
        viewModel.onAction(HomeAction.PickerPlayerClicked("a1"))
        viewModel.onAction(HomeAction.PickerPlayerClicked("l1"))
        assertEquals(1, data.leaguePlayerReads)

        viewModel.onAction(HomeAction.PickerQueryChanged("irn"))
        viewModel.onAction(HomeAction.PickerQueryChanged("irndorfer"))

        assertEquals(listOf("p2"), viewModel.uiState.value.picker!!.entries.map { it.id })
        assertEquals(1, data.leaguePlayerReads)
    }

    @Test
    fun backGoesUpOneStepClearingTheQueryThenReturnsToClubsThenDismisses() = runTest {
        val viewModel = viewModel()
        viewModel.onAction(HomeAction.AddFavoriteClicked)
        viewModel.onAction(HomeAction.PickerBrowsePlayersClicked)
        viewModel.onAction(HomeAction.PickerPlayerClicked("a1"))
        viewModel.onAction(HomeAction.PickerQueryChanged("zzz"))

        viewModel.onAction(HomeAction.PickerBackClicked)
        assertEquals(PlayerStep.ASSOCIATION, viewModel.uiState.value.picker!!.step)
        assertEquals("", viewModel.uiState.value.picker!!.query)

        viewModel.onAction(HomeAction.PickerBackClicked)
        assertNull(viewModel.uiState.value.picker!!.step)
        assertEquals(listOf("c1"), viewModel.uiState.value.picker!!.clubs.map { it.id })

        viewModel.onAction(HomeAction.PickerBackClicked)
        assertNull(viewModel.uiState.value.picker)
    }

    @Test
    fun tappingATeamFavoriteOpensThatTeam() = runTest {
        val viewModel = viewModel()
        val events = mutableListOf<HomeEvent>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.events.collect { events += it } }

        viewModel.onAction(HomeAction.FavoriteTeamClicked("t1"))

        assertEquals(listOf<HomeEvent>(HomeEvent.OpenTeam("t1")), events)
    }

    @Test
    fun tappingAPlayerFavoriteOpensThatPlayer() = runTest {
        val viewModel = viewModel()
        val events = mutableListOf<HomeEvent>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.events.collect { events += it } }

        viewModel.onAction(HomeAction.FavoritePlayerClicked("p1"))

        assertEquals(listOf<HomeEvent>(HomeEvent.OpenPlayer("p1")), events)
    }

    @Test
    fun aTeamFavoriteIsListedWithItsClub() = runTest {
        data.favorites.value = setOf(Favorite.Team("t1"))
        val viewModel = viewModel()

        assertEquals(listOf(ResolvedFavorite(Favorite.Team("t1"), "Olympic 1", "TTC Olympic")), viewModel.uiState.value.favorites)
    }

    @Test
    fun theHeroIsAbsentFromTheFavoritesListAndReturnsWhenTheHeroChanges() = runTest {
        data.favorites.value = setOf(Favorite.Club("c1"), Favorite.Team("t1"))
        val viewModel = viewModel()
        assertEquals(listOf(Favorite.Team("t1")), viewModel.uiState.value.favorites.map { it.favorite })

        data.saveSelection(UserSelection.None)

        assertEquals(setOf<Favorite>(Favorite.Club("c1"), Favorite.Team("t1")), viewModel.uiState.value.favorites.map { it.favorite }.toSet())
        assertEquals(setOf<Favorite>(Favorite.Club("c1"), Favorite.Team("t1")), data.favorites.value)
    }
}
