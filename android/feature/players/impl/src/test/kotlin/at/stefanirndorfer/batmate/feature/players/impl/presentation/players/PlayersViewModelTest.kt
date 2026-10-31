package at.stefanirndorfer.batmate.feature.players.impl.presentation.players

import at.stefanirndorfer.batmate.domain.club.Association
import at.stefanirndorfer.batmate.domain.club.AssociationRepository
import at.stefanirndorfer.batmate.domain.club.Club
import at.stefanirndorfer.batmate.domain.club.ClubRepository
import at.stefanirndorfer.batmate.domain.club.DataUnavailable
import at.stefanirndorfer.batmate.domain.club.LineupPlayer
import at.stefanirndorfer.batmate.domain.club.ListAssociationClubsUseCase
import at.stefanirndorfer.batmate.domain.club.ListAssociationsUseCase
import at.stefanirndorfer.batmate.domain.club.ListClubPlayersUseCase
import at.stefanirndorfer.batmate.domain.club.Team
import at.stefanirndorfer.batmate.domain.club.TeamRepository
import at.stefanirndorfer.batmate.domain.player.Player
import at.stefanirndorfer.batmate.domain.player.PlayerRepository
import at.stefanirndorfer.batmate.domain.user.RememberPlayerUseCase
import at.stefanirndorfer.batmate.feature.players.impl.presentation.players.models.PlayersAction
import at.stefanirndorfer.batmate.feature.players.impl.presentation.players.models.PlayersEvent
import at.stefanirndorfer.batmate.feature.players.impl.presentation.players.models.PlayersListUi
import at.stefanirndorfer.batmate.feature.players.impl.presentation.players.models.PlayersPickerKind
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
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

private class FakeDirectory :
    ClubRepository,
    AssociationRepository,
    TeamRepository,
    PlayerRepository {
    var unavailable = false
    val remembered = mutableListOf<Player>()

    private val clubs = listOf(Club("c1", "TTC Olympic", "a1"), Club("c2", "Nussdorf TTC", "a1"), Club("c3", "Linz AG", "a2"))

    private fun <T> read(value: T): T {
        if (unavailable) {
            throw DataUnavailable("XTTV unreachable")
        }
        return value
    }

    override suspend fun getClub(id: String): Club? = clubs.firstOrNull { it.id == id }

    override suspend fun searchClubs(query: String): List<Club> = clubs

    override suspend fun getAssociation(id: String): Association? = null

    override suspend fun getAssociations(): List<Association> = listOf(Association("a1", "Wiener Verband", "WTTV"), Association("a2", "OÖ Verband", "OÖTTV"))

    override suspend fun getTeam(id: String): Team? = null

    override suspend fun getTeams(clubId: String): List<Team> = read(listOf(Team("t-$clubId", "Team", clubId, "l1")))

    override suspend fun getLineup(teamId: String): List<LineupPlayer> = read(
        if (teamId == "t-c1") listOf(LineupPlayer("p2", "Zoe Zeller"), LineupPlayer("p1", "Anna Gruber", 1992)) else listOf(LineupPlayer("p3", "Max Mustermann")),
    )

    override suspend fun getPlayer(id: String): Player? = null

    override suspend fun getLeaguePlayers(leagueId: String): List<Player> = emptyList()

    override suspend fun remember(player: Player) {
        remembered += player
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class PlayersViewModelTest {
    private val data = FakeDirectory()

    @Before
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @After
    fun tearDown() = Dispatchers.resetMain()

    private fun viewModel() = PlayersViewModel(
        listAssociations = ListAssociationsUseCase(data),
        listAssociationClubs = ListAssociationClubsUseCase(data),
        listClubPlayers = ListClubPlayersUseCase(data),
        rememberPlayer = RememberPlayerUseCase(data),
    )

    private fun PlayersViewModel.pick(row: PlayersAction, entryId: String) {
        onAction(row)
        onAction(PlayersAction.PickerEntryClicked(entryId))
    }

    private fun PlayersViewModel.pickClub() {
        pick(PlayersAction.AssociationRowClicked, "a1")
        pick(PlayersAction.ClubRowClicked, "c1")
    }

    @Test
    fun theClubRowDoesNothingUntilAnAssociationIsChosen() = runTest {
        val viewModel = viewModel()

        viewModel.onAction(PlayersAction.ClubRowClicked)

        assertNull(viewModel.uiState.value.picker)
        assertEquals(PlayersListUi.PickClub, viewModel.uiState.value.list)
    }

    @Test
    fun theAssociationSheetListsAssociationsAndChoosingOneClosesIt() = runTest {
        val viewModel = viewModel()

        viewModel.onAction(PlayersAction.AssociationRowClicked)
        val picker = viewModel.uiState.value.picker!!
        assertEquals(PlayersPickerKind.ASSOCIATION, picker.kind)
        assertEquals(listOf("a1", "a2"), picker.entries.map { it.id })

        viewModel.onAction(PlayersAction.PickerEntryClicked("a2"))

        assertEquals("OÖ Verband", viewModel.uiState.value.association?.name)
        assertNull(viewModel.uiState.value.picker)
    }

    @Test
    fun theClubSheetListsOnlyTheChosenAssociationsClubsAndFilters() = runTest {
        val viewModel = viewModel()
        viewModel.pick(PlayersAction.AssociationRowClicked, "a1")

        viewModel.onAction(PlayersAction.ClubRowClicked)
        assertEquals(listOf("c2", "c1"), viewModel.uiState.value.picker!!.entries.map { it.id })

        viewModel.onAction(PlayersAction.PickerQueryChanged("nuss"))
        assertEquals(listOf("c2"), viewModel.uiState.value.picker!!.entries.map { it.id })
    }

    @Test
    fun choosingAClubLoadsItsPlayersByNameAndTheTextFilters() = runTest {
        val viewModel = viewModel()

        viewModel.pickClub()

        val loaded = assertIs<PlayersListUi.Loaded>(viewModel.uiState.value.list)
        assertEquals(listOf("p1", "p2"), loaded.entries.map { it.id })
        assertEquals(1992, loaded.entries.first().rcPoints)

        viewModel.onAction(PlayersAction.QueryChanged("zoe"))
        assertEquals(listOf("p2"), assertIs<PlayersListUi.Loaded>(viewModel.uiState.value.list).entries.map { it.id })
    }

    @Test
    fun changingTheAssociationClearsClubAndSearch() = runTest {
        val viewModel = viewModel()
        viewModel.pickClub()
        viewModel.onAction(PlayersAction.QueryChanged("zoe"))

        viewModel.pick(PlayersAction.AssociationRowClicked, "a2")

        val state = viewModel.uiState.value
        assertEquals("a2", state.association?.id)
        assertNull(state.club)
        assertEquals("", state.query)
        assertEquals(PlayersListUi.PickClub, state.list)
    }

    @Test
    fun changingTheClubClearsTheSearch() = runTest {
        val viewModel = viewModel()
        viewModel.pickClub()
        viewModel.onAction(PlayersAction.QueryChanged("zoe"))

        viewModel.pick(PlayersAction.ClubRowClicked, "c2")

        assertEquals("", viewModel.uiState.value.query)
        assertEquals(listOf("p3"), assertIs<PlayersListUi.Loaded>(viewModel.uiState.value.list).entries.map { it.id })
    }

    @Test
    fun unreachableXttvShowsUnavailableAndRetryRecovers() = runTest {
        val viewModel = viewModel()
        data.unavailable = true

        viewModel.pickClub()
        assertEquals(PlayersListUi.Unavailable, viewModel.uiState.value.list)

        data.unavailable = false
        viewModel.onAction(PlayersAction.RetryClicked)
        assertIs<PlayersListUi.Loaded>(viewModel.uiState.value.list)
    }

    @Test
    fun tappingAPlayerRemembersThemAndOpensTheirScreen() = runTest {
        val viewModel = viewModel()
        viewModel.pickClub()

        viewModel.onAction(PlayersAction.PlayerClicked("p1"))

        assertEquals(listOf(Player("p1", "Anna Gruber", "c1")), data.remembered)
        assertEquals(PlayersEvent.OpenPlayer("p1"), viewModel.events.first())
    }
}
