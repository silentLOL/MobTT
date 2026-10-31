package at.stefanirndorfer.mobtt.feature.club.impl.presentation.chooseclub

import at.stefanirndorfer.mobtt.core.analytics.LoggedEvent
import at.stefanirndorfer.mobtt.core.analytics.RecordingAnalyticsTracker
import at.stefanirndorfer.mobtt.domain.club.ListAssociationClubsUseCase
import at.stefanirndorfer.mobtt.domain.club.ListAssociationsUseCase
import at.stefanirndorfer.mobtt.domain.user.SetMyClubUseCase
import at.stefanirndorfer.mobtt.domain.user.UserSelection
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.FakeClubData
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

@OptIn(ExperimentalCoroutinesApi::class)
class ChooseClubViewModelTest {
    private val data = FakeClubData()
    private val analytics = RecordingAnalyticsTracker()

    @Before
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @After
    fun tearDown() = Dispatchers.resetMain()

    private fun viewModel() = ChooseClubViewModel(ListAssociationsUseCase(data), ListAssociationClubsUseCase(data), SetMyClubUseCase(data), analytics)

    @Test
    fun choosingAnAssociationThenAClubSavesMyClubAndFinishes() = runTest {
        val viewModel = viewModel()

        viewModel.onAction(ChooseClubAction.AssociationRowClicked)
        viewModel.onAction(ChooseClubAction.PickerEntryClicked("a1"))
        viewModel.onAction(ChooseClubAction.ClubRowClicked)
        viewModel.onAction(ChooseClubAction.PickerEntryClicked("c1"))

        assertEquals(listOf<UserSelection>(UserSelection.MyClub("c1")), data.saved)
        assertEquals(ChooseClubEvent.Chosen, viewModel.events.first())
    }

    @Test
    fun theClubRowDoesNothingUntilAnAssociationIsChosen() = runTest {
        val viewModel = viewModel()

        viewModel.onAction(ChooseClubAction.ClubRowClicked)

        assertEquals(null, viewModel.uiState.value.sheet)
    }

    @Test
    fun reportsTheScreenViewAndTheClubChosenFromHome() = runTest {
        val viewModel = viewModel()

        viewModel.onAction(ChooseClubAction.AssociationRowClicked)
        viewModel.onAction(ChooseClubAction.PickerEntryClicked("a1"))
        viewModel.onAction(ChooseClubAction.ClubRowClicked)
        viewModel.onAction(ChooseClubAction.PickerEntryClicked("c1"))

        assertEquals(
            listOf(
                LoggedEvent("screen_view", mapOf("screen" to "choose_club")),
                LoggedEvent("my_club_selected", mapOf("source" to "home")),
            ),
            analytics.events,
        )
    }
}
