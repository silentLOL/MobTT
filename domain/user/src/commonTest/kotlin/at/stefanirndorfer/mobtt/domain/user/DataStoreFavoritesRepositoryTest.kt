package at.stefanirndorfer.mobtt.domain.user

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import okio.FileSystem
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

class DataStoreFavoritesRepositoryTest {
    private val file = FileSystem.SYSTEM_TEMPORARY_DIRECTORY / "batmate-${Random.nextLong()}.preferences_pb"
    private val dataStore = PreferenceDataStoreFactory.createWithPath(produceFile = { file })
    private val favorites = DataStoreFavoritesRepository(dataStore)

    @Test
    fun hasNoFavoritesInitially() = runTest {
        assertEquals(emptySet(), favorites.observeFavorites().first())
    }

    @Test
    fun savedFavoritesKeepTheirKind() = runTest {
        favorites.add(Favorite.Player("1"))
        favorites.add(Favorite.Team("1"))
        favorites.add(Favorite.Club("1"))

        assertEquals(
            setOf(Favorite.Player("1"), Favorite.Team("1"), Favorite.Club("1")),
            favorites.observeFavorites().first(),
        )
    }

    @Test
    fun addingTheSameFavoriteTwiceKeepsOneEntry() = runTest {
        favorites.add(Favorite.Club("c1"))
        favorites.add(Favorite.Club("c1"))

        assertEquals(setOf(Favorite.Club("c1")), favorites.observeFavorites().first())
    }

    @Test
    fun removedFavoriteIsGone() = runTest {
        favorites.add(Favorite.Player("p1"))
        favorites.add(Favorite.Team("t1"))
        favorites.remove(Favorite.Player("p1"))

        assertEquals(setOf(Favorite.Team("t1")), favorites.observeFavorites().first())
    }

    @Test
    fun changingMyPlayerAndMyClubLeavesFavoritesUntouched() = runTest {
        val selection = DataStoreUserSelectionRepository(dataStore)
        favorites.add(Favorite.Team("t1"))

        selection.saveSelection(UserSelection.MyPlayer("p1"))
        selection.saveSelection(UserSelection.MyClub("c1"))

        assertEquals(setOf(Favorite.Team("t1")), favorites.observeFavorites().first())
    }

    @Test
    fun favoritesSurviveReopeningTheStore() = runTest {
        val firstSession = newStoreScope()
        val firstStore = PreferenceDataStoreFactory.createWithPath(scope = firstSession, produceFile = { file })
        DataStoreFavoritesRepository(firstStore).add(Favorite.Team("t1"))
        firstSession.cancel()

        val reopened = PreferenceDataStoreFactory.createWithPath(
            scope = newStoreScope(),
            produceFile = { file },
        )

        assertEquals(setOf(Favorite.Team("t1")), DataStoreFavoritesRepository(reopened).observeFavorites().first())
    }
}
