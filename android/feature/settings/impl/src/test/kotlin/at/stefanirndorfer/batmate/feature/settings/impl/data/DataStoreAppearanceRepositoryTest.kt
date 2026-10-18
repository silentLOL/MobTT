package at.stefanirndorfer.batmate.feature.settings.impl.data

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import at.stefanirndorfer.batmate.feature.settings.api.Appearance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import kotlin.test.assertEquals

class DataStoreAppearanceRepositoryTest {
    @get:Rule
    val folder = TemporaryFolder()

    private fun openRepository(scope: CoroutineScope) = DataStoreAppearanceRepository(
        PreferenceDataStoreFactory.create(scope = scope) { folder.root.resolve("appearance.preferences_pb") },
    )

    private fun newScope() = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)

    @Test
    fun followsTheSystemInitially() = runTest {
        assertEquals(Appearance.SYSTEM, openRepository(newScope()).observeAppearance().first())
    }

    @Test
    fun savedAppearanceIsObserved() = runTest {
        val repository = openRepository(newScope())
        repository.saveAppearance(Appearance.DARK)

        assertEquals(Appearance.DARK, repository.observeAppearance().first())
    }

    @Test
    fun appearanceSurvivesReopeningTheStore() = runTest {
        val firstSession = newScope()
        openRepository(firstSession).saveAppearance(Appearance.LIGHT)
        firstSession.cancel()

        assertEquals(Appearance.LIGHT, openRepository(newScope()).observeAppearance().first())
    }
}
