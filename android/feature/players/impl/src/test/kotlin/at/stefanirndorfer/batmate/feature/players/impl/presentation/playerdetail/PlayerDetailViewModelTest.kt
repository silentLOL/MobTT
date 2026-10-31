package at.stefanirndorfer.batmate.feature.players.impl.presentation.playerdetail

import at.stefanirndorfer.batmate.domain.club.Association
import at.stefanirndorfer.batmate.domain.club.AssociationRepository
import at.stefanirndorfer.batmate.domain.club.Club
import at.stefanirndorfer.batmate.domain.club.ClubRepository
import at.stefanirndorfer.batmate.domain.club.DataUnavailable
import at.stefanirndorfer.batmate.domain.club.LineupPlayer
import at.stefanirndorfer.batmate.domain.club.Team
import at.stefanirndorfer.batmate.domain.club.TeamRepository
import at.stefanirndorfer.batmate.domain.player.Player
import at.stefanirndorfer.batmate.domain.player.PlayerRepository
import at.stefanirndorfer.batmate.domain.user.AddFavoriteUseCase
import at.stefanirndorfer.batmate.domain.user.Favorite
import at.stefanirndorfer.batmate.domain.user.FavoritesRepository
import at.stefanirndorfer.batmate.domain.user.GetPlayerDetailUseCase
import at.stefanirndorfer.batmate.domain.user.ObserveFavoritesUseCase
import at.stefanirndorfer.batmate.domain.user.PlayerDetail
import at.stefanirndorfer.batmate.domain.user.RemoveFavoriteUseCase
import at.stefanirndorfer.batmate.domain.user.UserSelection
import at.stefanirndorfer.batmate.domain.user.UserSelectionRepository
import at.stefanirndorfer.batmate.feature.players.impl.presentation.playerdetail.models.PlayerDetailAction
import at.stefanirndorfer.batmate.feature.players.impl.presentation.playerdetail.models.PlayerDetailEvent
import at.stefanirndorfer.batmate.feature.players.impl.presentation.playerdetail.models.PlayerDetailUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
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

/** XTTV stand-in: every read throws [DataUnavailable] while [unavailable] is set. */
private class FakePlayerData :
    PlayerRepository,
    ClubRepository,
    AssociationRepository,
    TeamRepository,
    FavoritesRepository,
    UserSelectionRepository {
    var unavailable = false
    var players = listOf(Player("p1", "Anna Gruber", "c1"))
    var teams = listOf(Team("t1", "TTC Olympic 1", "c1", "l1"))
    var lineup = listOf(LineupPlayer("p1", "Anna Gruber"))
    val favorites = MutableStateFlow<Set<Favorite>>(emptySet())

    private fun <T> read(value: T): T {
        if (unavailable) {
            throw DataUnavailable("XTTV unreachable")
        }
        return value
    }

    override suspend fun getPlayer(id: String): Player? = read(players.firstOrNull { it.id == id })

    override suspend fun getLeaguePlayers(leagueId: String): List<Player> = read(players)

    override suspend fun getClub(id: String): Club? = read(Club("c1", "TTC Olympic", "a1").takeIf { it.id == id })

    override suspend fun searchClubs(query: String): List<Club> = read(emptyList())

    override suspend fun getAssociation(id: String): Association? = read(Association("a1", "WTTV"))

    override suspend fun getAssociations(): List<Association> = read(emptyList())

    override suspend fun getTeam(id: String): Team? = read(teams.firstOrNull { it.id == id })

    override suspend fun getTeams(clubId: String): List<Team> = read(teams.filter { it.clubId == clubId })

    override suspend fun getLineup(teamId: String): List<LineupPlayer> = read(lineup)

    override fun observeFavorites(): Flow<Set<Favorite>> = favorites

    override suspend fun add(favorite: Favorite) = favorites.update { it + favorite }

    override suspend fun remove(favorite: Favorite) = favorites.update { it - favorite }

    override fun observeSelection(): Flow<UserSelection> = flowOf(UserSelection.None)

    override suspend fun saveSelection(selection: UserSelection) = Unit
}

@OptIn(ExperimentalCoroutinesApi::class)
class PlayerDetailViewModelTest {
    private val data = FakePlayerData()

    @Before
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @After
    fun tearDown() = Dispatchers.resetMain()

    private fun TestScope.viewModel(playerId: String = "p1"): PlayerDetailViewModel {
        val viewModel = PlayerDetailViewModel(
            playerId = playerId,
            getPlayerDetail = GetPlayerDetailUseCase(data, data, data, data),
            observeFavorites = ObserveFavoritesUseCase(data),
            addFavorite = AddFavoriteUseCase(data, data),
            removeFavorite = RemoveFavoriteUseCase(data),
        )
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
        return viewModel
    }

    @Test
    fun aResolvablePlayerShowsNameClubAndAssociation() = runTest {
        val state = assertIs<PlayerDetailUiState.Content>(viewModel().uiState.value)

        assertEquals(PlayerDetail(Player("p1", "Anna Gruber", "c1"), "TTC Olympic", "WTTV", data.teams), state.detail)
        assertFalse(state.isFavorite)
    }

    @Test
    fun anUnresolvablePlayerShowsUnavailable() = runTest {
        assertEquals(PlayerDetailUiState.Unavailable, viewModel("gone").uiState.value)
    }

    @Test
    fun unreachableDataShowsUnavailableInsteadOfCrashing() = runTest {
        data.unavailable = true

        assertEquals(PlayerDetailUiState.Unavailable, viewModel().uiState.value)
    }

    @Test
    fun tappingTheStarAddsThePlayerAsFavoriteAndTappingAgainRemovesIt() = runTest {
        val viewModel = viewModel()

        viewModel.onAction(PlayerDetailAction.FavoriteClicked)
        assertEquals(setOf<Favorite>(Favorite.Player("p1")), data.favorites.value)
        assertTrue(assertIs<PlayerDetailUiState.Content>(viewModel.uiState.value).isFavorite)

        viewModel.onAction(PlayerDetailAction.FavoriteClicked)
        assertEquals(emptySet(), data.favorites.value)
        assertFalse(assertIs<PlayerDetailUiState.Content>(viewModel.uiState.value).isFavorite)
    }

    @Test
    fun aPlayerAlreadyFavoriteShowsAsFavoriteAndOtherFavoritesStayUntouched() = runTest {
        data.favorites.value = setOf(Favorite.Player("p1"), Favorite.Club("c1"))
        val viewModel = viewModel()
        assertTrue(assertIs<PlayerDetailUiState.Content>(viewModel.uiState.value).isFavorite)

        viewModel.onAction(PlayerDetailAction.FavoriteClicked)

        assertEquals(setOf<Favorite>(Favorite.Club("c1")), data.favorites.value)
    }

    @Test
    fun tappingTheStarWithoutContentChangesNothing() = runTest {
        data.unavailable = true
        val viewModel = viewModel()

        viewModel.onAction(PlayerDetailAction.FavoriteClicked)

        assertEquals(emptySet(), data.favorites.value)
    }

    @Test
    fun tappingTheClubOrATeamEmitsTheMatchingEvent() = runTest {
        val viewModel = viewModel()

        viewModel.onAction(PlayerDetailAction.ClubClicked)
        assertEquals(PlayerDetailEvent.OpenClub("c1"), viewModel.events.first())

        viewModel.onAction(PlayerDetailAction.TeamClicked("t1"))
        assertEquals(PlayerDetailEvent.OpenTeam("t1"), viewModel.events.first())
    }
}
