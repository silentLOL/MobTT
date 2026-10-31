package at.stefanirndorfer.mobtt.domain.user

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

private class FakeConsentRepository(initial: UsageConsent = UsageConsent.UNANSWERED) : UsageConsentRepository {
    private val consent = MutableStateFlow(initial)

    override fun observeConsent(): Flow<UsageConsent> = consent

    override suspend fun saveConsent(consent: UsageConsent) {
        this.consent.value = consent
    }
}

class UsageConsentPromptTest {
    private val selection = FakeSelectionRepository()
    private val consent = FakeConsentRepository()
    private val prompt = ObserveUsageConsentPromptUseCase(selection, consent)

    @Test
    fun isNotShownBeforeTheFirstChoice() = runTest {
        assertEquals(false, prompt().first())
    }

    @Test
    fun isShownAfterTheFirstChoiceUntilAnswered() = runTest {
        selection.saveSelection(UserSelection.MyClub("c1"))

        assertEquals(true, prompt().first())
    }

    @Test
    fun existingInstallWithAChoiceIsAskedOnce() = runTest {
        selection.saveSelection(UserSelection.MyPlayer("p1"))
        assertEquals(true, prompt().first())

        SetUsageConsentUseCase(consent)(allowed = true)

        assertEquals(false, prompt().first())
    }

    @Test
    fun notNowIsNeverAskedAgain() = runTest {
        selection.saveSelection(UserSelection.MyClub("c1"))
        SetUsageConsentUseCase(consent)(allowed = false)
        selection.saveSelection(UserSelection.MyPlayer("p1"))

        assertEquals(false, prompt().first())
        assertEquals(UsageConsent.DECLINED, ObserveUsageConsentUseCase(consent)().first())
    }

    @Test
    fun laterToggleChangesTheAnswerWithoutAskingAgain() = runTest {
        selection.saveSelection(UserSelection.MyClub("c1"))
        val setConsent = SetUsageConsentUseCase(consent)
        setConsent(allowed = false)
        setConsent(allowed = true)

        assertEquals(UsageConsent.ALLOWED, ObserveUsageConsentUseCase(consent)().first())
        assertEquals(false, prompt().first())
    }
}
