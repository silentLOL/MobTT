package at.stefanirndorfer.batmate.feature.settings.impl.presentation.settings

import at.stefanirndorfer.batmate.domain.club.Association
import at.stefanirndorfer.batmate.domain.club.AssociationRepository
import at.stefanirndorfer.batmate.domain.club.Club
import at.stefanirndorfer.batmate.domain.club.ClubRepository
import at.stefanirndorfer.batmate.domain.club.SearchClubsUseCase
import at.stefanirndorfer.batmate.domain.player.Player
import at.stefanirndorfer.batmate.domain.player.PlayerRepository
import at.stefanirndorfer.batmate.domain.user.ObserveUserContextUseCase
import at.stefanirndorfer.batmate.domain.user.SearchPlayersUseCase
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
    PlayerRepository,
    UserSelectionRepository,
    AppearanceRepository {
    private val clubs = listOf(Club("c1", "TTC Olympic", "a1"), Club("c2", "Nussdorf TTC", "a2"))
    private val associations = listOf(Association("a1", "WTTV"), Association("a2", "ÖTTV"))
    private val players = listOf(Player("p1", "Anna Gruber", "c1"))

    override suspend fun getClub(id: String): Club? = clubs.firstOrNull { it.id == id }

    override suspend fun searchClubs(query: String): List<Club> = clubs

    override suspend fun getAssociation(id: String): Association? = associations.firstOrNull { it.id == id }

    override suspend fun getPlayer(id: String): Player? = players.firstOrNull { it.id == id }

    override suspend fun searchPlayers(query: String): List<Player> = players

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
            setMyPlayer = SetMyPlayerUseCase(data),
            setMyClub = SetMyClubUseCase(data),
            searchPlayers = SearchPlayersUseCase(data, data),
            searchClubs = SearchClubsUseCase(data),
        )
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
        return viewModel
    }

    @Test
    fun choosingAPlayerShowsItsClubAndAssociationAndClosesThePicker() = runTest {
        val viewModel = viewModel()

        viewModel.onAction(SettingsAction.ChoosePlayerClicked)
        assertEquals(PickerKind.PLAYER, viewModel.uiState.value.picker?.kind)
        viewModel.onAction(SettingsAction.PickerEntryClicked("p1"))

        val state = viewModel.uiState.value
        assertEquals("Anna Gruber", state.myPlayerName)
        assertEquals(DerivedFrom.PLAYER, state.derivedFrom)
        assertEquals("TTC Olympic", state.derivedClubName)
        assertEquals("WTTV", state.derivedAssociationName)
        assertNull(state.picker)
    }

    @Test
    fun choosingAClubReplacesTheChosenPlayer() = runTest {
        val viewModel = viewModel()
        viewModel.onAction(SettingsAction.ChoosePlayerClicked)
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
        viewModel.onAction(SettingsAction.ChoosePlayerClicked)
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
        viewModel.onAction(SettingsAction.ChoosePlayerClicked)
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
