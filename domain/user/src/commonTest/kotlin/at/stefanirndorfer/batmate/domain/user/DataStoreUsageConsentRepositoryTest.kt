package at.stefanirndorfer.batmate.domain.user

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import okio.FileSystem
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

class DataStoreUsageConsentRepositoryTest {
    private val file = FileSystem.SYSTEM_TEMPORARY_DIRECTORY / "batmate-consent-${Random.nextLong()}.preferences_pb"

    private fun openRepository(scope: CoroutineScope = newStoreScope()) = DataStoreUsageConsentRepository(
        PreferenceDataStoreFactory.createWithPath(scope = scope, produceFile = { file }),
    )

    @Test
    fun isUnansweredInitially() = runTest {
        assertEquals(UsageConsent.UNANSWERED, openRepository().observeConsent().first())
    }

    @Test
    fun allowedAndDeclinedAreDistinctFromUnanswered() = runTest {
        val repository = openRepository()

        repository.saveConsent(UsageConsent.DECLINED)
        assertEquals(UsageConsent.DECLINED, repository.observeConsent().first())

        repository.saveConsent(UsageConsent.ALLOWED)
        assertEquals(UsageConsent.ALLOWED, repository.observeConsent().first())
    }

    @Test
    fun answerCanBeChangedLater() = runTest {
        val repository = openRepository()
        repository.saveConsent(UsageConsent.ALLOWED)
        repository.saveConsent(UsageConsent.DECLINED)

        assertEquals(UsageConsent.DECLINED, repository.observeConsent().first())
    }

    @Test
    fun answerSurvivesReopeningTheStore() = runTest {
        val firstSession = newStoreScope()
        openRepository(firstSession).saveConsent(UsageConsent.DECLINED)
        firstSession.cancel()

        assertEquals(UsageConsent.DECLINED, openRepository().observeConsent().first())
    }
}
