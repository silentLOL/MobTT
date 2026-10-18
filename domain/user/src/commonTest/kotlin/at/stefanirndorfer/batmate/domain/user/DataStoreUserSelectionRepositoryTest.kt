package at.stefanirndorfer.batmate.domain.user

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import okio.FileSystem
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

class DataStoreUserSelectionRepositoryTest {
    private val file = FileSystem.SYSTEM_TEMPORARY_DIRECTORY / "batmate-${Random.nextLong()}.preferences_pb"

    private fun openRepository(scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)) = DataStoreUserSelectionRepository(
        PreferenceDataStoreFactory.createWithPath(scope = scope, produceFile = { file }),
    )

    @Test
    fun hasNoSelectionInitially() = runTest {
        assertEquals(UserSelection.None, openRepository().observeSelection().first())
    }

    @Test
    fun savedChoiceIsObserved() = runTest {
        val repository = openRepository()
        repository.saveSelection(UserSelection.MyPlayer("p1"))

        assertEquals(UserSelection.MyPlayer("p1"), repository.observeSelection().first())
    }

    @Test
    fun savingMyClubReplacesMyPlayer() = runTest {
        val repository = openRepository()
        repository.saveSelection(UserSelection.MyPlayer("p1"))
        repository.saveSelection(UserSelection.MyClub("c1"))

        assertEquals(UserSelection.MyClub("c1"), repository.observeSelection().first())
    }

    @Test
    fun savingMyPlayerReplacesMyClub() = runTest {
        val repository = openRepository()
        repository.saveSelection(UserSelection.MyClub("c1"))
        repository.saveSelection(UserSelection.MyPlayer("p1"))

        assertEquals(UserSelection.MyPlayer("p1"), repository.observeSelection().first())
    }

    @Test
    fun choiceSurvivesReopeningTheStore() = runTest {
        val firstSession = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
        openRepository(firstSession).saveSelection(UserSelection.MyPlayer("p1"))
        firstSession.cancel()

        assertEquals(UserSelection.MyPlayer("p1"), openRepository().observeSelection().first())
    }
}
