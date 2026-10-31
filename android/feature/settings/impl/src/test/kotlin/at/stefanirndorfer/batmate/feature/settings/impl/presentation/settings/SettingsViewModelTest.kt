package at.stefanirndorfer.batmate.feature.settings.impl.presentation.settings

import at.stefanirndorfer.batmate.core.ui.directory.models.PlayerStep
import at.stefanirndorfer.batmate.domain.club.Association
import at.stefanirndorfer.batmate.domain.club.AssociationRepository
import at.stefanirndorfer.batmate.domain.club.Club
import at.stefanirndorfer.batmate.domain.club.ClubRepository
import at.stefanirndorfer.batmate.domain.club.DataUnavailable
import at.stefanirndorfer.batmate.domain.club.League
import at.stefanirndorfer.batmate.domain.club.LeagueRepository
import at.stefanirndorfer.batmate.domain.club.ListAssociationsUseCase
import at.stefanirndorfer.batmate.domain.club.ListLeaguesUseCase
import at.stefanirndorfer.batmate.domain.club.SearchClubsUseCase
import at.stefanirndorfer.batmate.domain.club.Standing
import at.stefanirndorfer.batmate.domain.player.Player
import at.stefanirndorfer.batmate.domain.player.PlayerRepository
import at.stefanirndorfer.batmate.domain.user.ListLeaguePlayersUseCase
import at.stefanirndorfer.batmate.domain.user.ObserveUserContextUseCase
import at.stefanirndorfer.batmate.domain.user.SetMyClubUseCase
import at.stefanirndorfer.batmate.domain.user.SetMyPlayerUseCase
import at.stefanirndorfer.batmate.domain.user.UserSelection
import at.stefanirndorfer.batmate.domain.user.UserSelectionRepository
import at.stefanirndorfer.batmate.feature.settings.api.Appearance
import at.stefanirndorfer.batmate.feature.settings.api.AppearanceRepository
import at.stefanirndorfer.batmate.feature.settings.impl.presentation.settings.models.DerivedFrom
import at.stefanirndorfer.batmate.feature.settings.impl.presentation.settings.models.PickerKind
import at.stefanirndorfer.batmate.feature.settings.impl.presentation.settings.models.SettingsAction
import at.stefanirndorfer.batmate.feature.settings.impl.presentation.settings.models.SettingsEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
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
import kotlin.test.assertNotNull
import kotlin.test.assertNull

private class FakeData :
    ClubRepository,
    AssociationRepository,
    LeagueRepository,
    PlayerRepository,
    UserSelectionRepository,
    AppearanceRepository {
    private val clubs = listOf(Club("c1", "TTC Olympic", "a1"), Club("c2", "Nussdorf TTC", "a2"))
    private val associations = listOf(Association("a1", "WTTV", "WTTV"), Association("a2", "ÖTTV", "ÖTTV"))
    private val players = listOf(Player("p1", "Anna Gruber", "c1"), Player("p2", "Jakob Irndorfer", "c1"))

    var unavailable = false

    override suspend fun getClub(id: String): Club? {
        if (unavailable) {
            throw DataUnavailable("XTTV is unavailable")
        }
        return clubs.firstOrNull { it.id == id }
    }

    override suspend fun searchClubs(query: String): List<Club> {
        if (unavailable) {
            throw DataUnavailable("XTTV is unavailable")
        }
        return clubs
    }

    override suspend fun getAssociation(id: String): Association? = associations.firstOrNull { it.id == id }

    override suspend fun getAssociations(): List<Association> {
        if (unavailable) {
            throw DataUnavailable("XTTV is unavailable")
        }
        return associations
    }

    var leaguePlayerReads = 0

    override suspend fun getLeague(id: String): League? = League(id, "Liga")

    override suspend fun getLeagues(associationId: String): List<League> = listOf(League("l1", "1. Landesliga"))

    override suspend fun getStandings(leagueId: String): List<Standing> = emptyList()

    override suspend fun getPlayer(id: String): Player? = players.firstOrNull { it.id == id }

    override suspend fun getLeaguePlayers(leagueId: String): List<Player> {
        leaguePlayerReads++
        if (unavailable) {
            throw DataUnavailable("XTTV is unavailable")
        }
        return players
    }

    var failSaves = false
    private val selection = MutableStateFlow<UserSelection>(UserSelection.None)

    override fun observeSelection(): Flow<UserSelection> = selection

    override suspend fun saveSelection(selection: UserSelection) {
        check(!failSaves) { "disk full" }
        this.selection.value = selection
    }

    private val appearance = MutableStateFlow(Appearance.SYSTEM)

    override fun observeAppearance(): Flow<Appearance> = appearance

    override suspend fun saveAppearance(appearance: Appearance) {
        this.appearance.value = appearance
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {
    private val data = FakeData()

    @Before
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @After
    fun tearDown() = Dispatchers.resetMain()

    private fun TestScope.viewModel(): SettingsViewModel {
        val viewModel = SettingsViewModel(
            appearanceRepository = data,
            observeUserContext = ObserveUserContextUseCase(data, data, data, data),
            setMyPlayer = SetMyPlayerUseCase(data, data),
            setMyClub = SetMyClubUseCase(data),
            searchClubs = SearchClubsUseCase(data),
            listAssociations = ListAssociationsUseCase(data),
            listLeagues = ListLeaguesUseCase(data),
            listLeaguePlayers = ListLeaguePlayersUseCase(data, data),
        )
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
        return viewModel
    }

    private fun SettingsViewModel.drillDownToPlayers() {
        onAction(SettingsAction.ChoosePlayerClicked)
        onAction(SettingsAction.PickerEntryClicked("a1"))
        onAction(SettingsAction.PickerEntryClicked("l1"))
    }

    @Test
    fun choosingAPlayerShowsItsClubAndAssociationAndClosesThePicker() = runTest {
        val viewModel = viewModel()

        viewModel.onAction(SettingsAction.ChoosePlayerClicked)
        assertEquals(PickerKind.PLAYER, viewModel.uiState.value.picker?.kind)
        viewModel.drillDownToPlayers()
        viewModel.onAction(SettingsAction.PickerEntryClicked("p1"))

        val state = viewModel.uiState.value
        assertEquals("Anna Gruber", state.myPlayerName)
        assertEquals(DerivedFrom.PLAYER, state.derivedFrom)
        assertEquals("TTC Olympic", state.derivedClubName)
        assertEquals("WTTV", state.derivedAssociationName)
        assertNull(state.picker)
    }

    @Test
    fun aPickerWhileXttvIsUnreachableShowsNoResultsInsteadOfCrashing() = runTest {
        val viewModel = viewModel()
        data.unavailable = true

        viewModel.onAction(SettingsAction.ChoosePlayerClicked)
        assertEquals(emptyList(), viewModel.uiState.value.picker?.entries)
        viewModel.onAction(SettingsAction.ChooseClubClicked)
        assertEquals(emptyList(), viewModel.uiState.value.picker?.entries)
    }

    @Test
    fun thePlayerPickerDrillsFromAssociationToLeagueToPlayers() = runTest {
        val viewModel = viewModel()

        viewModel.onAction(SettingsAction.ChoosePlayerClicked)
        var picker = assertNotNull(viewModel.uiState.value.picker)
        assertEquals(PlayerStep.ASSOCIATION, picker.step)
        assertEquals(listOf("a1", "a2"), picker.entries.map { it.id })

        viewModel.onAction(SettingsAction.PickerEntryClicked("a1"))
        picker = assertNotNull(viewModel.uiState.value.picker)
        assertEquals(PlayerStep.LEAGUE, picker.step)
        assertEquals("WTTV", picker.breadcrumb)
        assertEquals(listOf("l1"), picker.entries.map { it.id })

        viewModel.onAction(SettingsAction.PickerEntryClicked("l1"))
        picker = assertNotNull(viewModel.uiState.value.picker)
        assertEquals(PlayerStep.PLAYER, picker.step)
        assertEquals("WTTV › 1. Landesliga", picker.breadcrumb)
        assertEquals(listOf("Anna Gruber", "Jakob Irndorfer"), picker.entries.map { it.title })
        assertEquals(listOf("TTC Olympic", "TTC Olympic"), picker.entries.map { it.subtitle })
    }

    @Test
    fun theQueryFiltersTheStepLocallyAndNeverReadsLeaguePlayersAgain() = runTest {
        val viewModel = viewModel()
        viewModel.drillDownToPlayers()
        assertEquals(1, data.leaguePlayerReads)

        viewModel.onAction(SettingsAction.PickerQueryChanged("irn"))
        viewModel.onAction(SettingsAction.PickerQueryChanged("IRNDORFER"))

        assertEquals(listOf("p2"), assertNotNull(viewModel.uiState.value.picker).entries.map { it.id })
        assertEquals(1, data.leaguePlayerReads)
    }

    @Test
    fun backGoesUpOneStepClearingTheQueryAndClosesFromTheFirstStep() = runTest {
        val viewModel = viewModel()
        viewModel.drillDownToPlayers()
        viewModel.onAction(SettingsAction.PickerQueryChanged("zzz"))

        viewModel.onAction(SettingsAction.PickerBackClicked)
        var picker = assertNotNull(viewModel.uiState.value.picker)
        assertEquals(PlayerStep.LEAGUE, picker.step)
        assertEquals("", picker.query)
        assertEquals("WTTV", picker.breadcrumb)

        viewModel.onAction(SettingsAction.PickerBackClicked)
        picker = assertNotNull(viewModel.uiState.value.picker)
        assertEquals(PlayerStep.ASSOCIATION, picker.step)

        viewModel.onAction(SettingsAction.PickerBackClicked)
        assertNull(viewModel.uiState.value.picker)
    }

    @Test
    fun choosingAClubReplacesTheChosenPlayer() = runTest {
        val viewModel = viewModel()
        viewModel.drillDownToPlayers()
        viewModel.onAction(SettingsAction.PickerEntryClicked("p1"))

        viewModel.onAction(SettingsAction.ChooseClubClicked)
        viewModel.onAction(SettingsAction.PickerEntryClicked("c2"))

        val state = viewModel.uiState.value
        assertNull(state.myPlayerName)
        assertEquals("Nussdorf TTC", state.myClubName)
        assertEquals(DerivedFrom.CLUB, state.derivedFrom)
        assertEquals("ÖTTV", state.derivedAssociationName)
    }

    @Test
    fun leavingThePickerKeepsTheCurrentChoice() = runTest {
        val viewModel = viewModel()
        viewModel.drillDownToPlayers()
        viewModel.onAction(SettingsAction.PickerEntryClicked("p1"))

        viewModel.onAction(SettingsAction.ChooseClubClicked)
        assertNotNull(viewModel.uiState.value.picker)
        viewModel.onAction(SettingsAction.PickerBackClicked)

        val state = viewModel.uiState.value
        assertNull(state.picker)
        assertEquals("Anna Gruber", state.myPlayerName)
    }

    @Test
    fun aFailedSaveKeepsThePickerOpenAndTheChoiceUnchanged() = runTest {
        val viewModel = viewModel()
        viewModel.drillDownToPlayers()
        viewModel.onAction(SettingsAction.PickerEntryClicked("p1"))

        data.failSaves = true
        viewModel.onAction(SettingsAction.ChooseClubClicked)
        viewModel.onAction(SettingsAction.PickerEntryClicked("c2"))

        val state = viewModel.uiState.value
        assertEquals(PickerKind.CLUB, state.picker?.kind)
        assertEquals("Anna Gruber", state.myPlayerName)
        assertNull(state.myClubName)
    }

    @Test
    fun theChosenEntryIsMarkedSelectedInThePicker() = runTest {
        val viewModel = viewModel()
        viewModel.onAction(SettingsAction.ChooseClubClicked)
        viewModel.onAction(SettingsAction.PickerEntryClicked("c1"))

        viewModel.onAction(SettingsAction.ChooseClubClicked)

        val entries = assertNotNull(viewModel.uiState.value.picker).entries
        assertEquals(listOf("c1"), entries.filter { it.selected }.map { it.id })
    }

    @Test
    fun selectingAnAppearanceIsSavedAndShown() = runTest {
        val viewModel = viewModel()

        viewModel.onAction(SettingsAction.AppearanceSelected(Appearance.DARK))

        assertEquals(Appearance.DARK, viewModel.uiState.value.appearance)
        assertEquals(Appearance.DARK, data.observeAppearance().first())
    }

    @Test
    fun doneAsksTheSheetToDismiss() = runTest {
        val viewModel = viewModel()
        val events = mutableListOf<SettingsEvent>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.events.toList(events) }

        viewModel.onAction(SettingsAction.DoneClicked)

        assertEquals(listOf<SettingsEvent>(SettingsEvent.Dismiss), events)
    }
}
