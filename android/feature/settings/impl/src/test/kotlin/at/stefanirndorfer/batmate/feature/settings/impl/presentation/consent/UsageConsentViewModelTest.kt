package at.stefanirndorfer.batmate.feature.settings.impl.presentation.consent

import at.stefanirndorfer.batmate.domain.user.ObserveUsageConsentPromptUseCase
import at.stefanirndorfer.batmate.domain.user.SetUsageConsentUseCase
import at.stefanirndorfer.batmate.domain.user.UsageConsent
import at.stefanirndorfer.batmate.domain.user.UsageConsentRepository
import at.stefanirndorfer.batmate.domain.user.UserSelection
import at.stefanirndorfer.batmate.domain.user.UserSelectionRepository
import at.stefanirndorfer.batmate.feature.settings.impl.presentation.consent.models.UsageConsentAction
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

private class FakeData :
    UserSelectionRepository,
    UsageConsentRepository {
    val selection = MutableStateFlow<UserSelection>(UserSelection.None)
    val consent = MutableStateFlow(UsageConsent.UNANSWERED)

    override fun observeSelection(): Flow<UserSelection> = selection

    override suspend fun saveSelection(selection: UserSelection) {
        this.selection.value = selection
    }

    override fun observeConsent(): Flow<UsageConsent> = consent

    override suspend fun saveConsent(consent: UsageConsent) {
        this.consent.value = consent
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class UsageConsentViewModelTest {
    private val data = FakeData()

    @Before
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @After
    fun tearDown() = Dispatchers.resetMain()

    private fun TestScope.viewModel(): UsageConsentViewModel {
        val viewModel = UsageConsentViewModel(
            observePrompt = ObserveUsageConsentPromptUseCase(data, data),
            setUsageConsent = SetUsageConsentUseCase(data),
        )
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.isPromptVisible.collect {} }
        return viewModel
    }

    @Test
    fun staysHiddenUntilAPlayerOrClubIsChosen() = runTest {
        val viewModel = viewModel()
        assertEquals(false, viewModel.isPromptVisible.value)

        data.saveSelection(UserSelection.MyClub("c1"))
        assertEquals(true, viewModel.isPromptVisible.value)
    }

    @Test
    fun allowingHidesThePromptAndStoresAllowed() = runTest {
        data.saveSelection(UserSelection.MyPlayer("p1"))
        val viewModel = viewModel()

        viewModel.onAction(UsageConsentAction.AllowClicked)

        assertEquals(false, viewModel.isPromptVisible.value)
        assertEquals(UsageConsent.ALLOWED, data.consent.first())
    }

    @Test
    fun notNowHidesThePromptForGoodAndStoresDeclined() = runTest {
        data.saveSelection(UserSelection.MyPlayer("p1"))
        val viewModel = viewModel()

        viewModel.onAction(UsageConsentAction.NotNowClicked)
        data.saveSelection(UserSelection.MyClub("c1"))

        assertEquals(false, viewModel.isPromptVisible.value)
        assertEquals(UsageConsent.DECLINED, data.consent.first())
    }
}
