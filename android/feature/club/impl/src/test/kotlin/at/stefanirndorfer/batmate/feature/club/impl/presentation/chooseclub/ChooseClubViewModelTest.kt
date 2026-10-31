package at.stefanirndorfer.batmate.feature.club.impl.presentation.chooseclub

import at.stefanirndorfer.batmate.domain.club.ListAssociationClubsUseCase
import at.stefanirndorfer.batmate.domain.club.ListAssociationsUseCase
import at.stefanirndorfer.batmate.domain.user.SetMyClubUseCase
import at.stefanirndorfer.batmate.domain.user.UserSelection
import at.stefanirndorfer.batmate.feature.club.impl.presentation.FakeClubData
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

    @Before
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @After
    fun tearDown() = Dispatchers.resetMain()

    private fun viewModel() = ChooseClubViewModel(ListAssociationsUseCase(data), ListAssociationClubsUseCase(data), SetMyClubUseCase(data))

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
}
