package at.stefanirndorfer.batmate.domain.user

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

private class FakeFavoritesRepository : FavoritesRepository {
    private val favorites = MutableStateFlow<Set<Favorite>>(emptySet())

    override fun observeFavorites(): Flow<Set<Favorite>> = favorites

    override suspend fun add(favorite: Favorite) {
        favorites.value += favorite
    }

    override suspend fun remove(favorite: Favorite) {
        favorites.value -= favorite
    }
}

class FavoritesTest {
    private val favorites = FakeFavoritesRepository()
    private val add = AddFavoriteUseCase(favorites)
    private val remove = RemoveFavoriteUseCase(favorites)
    private val observe = ObserveFavoritesUseCase(favorites)

    @Test
    fun hasNoFavoritesInitially() = runTest {
        assertEquals(emptySet(), observe().first())
    }

    @Test
    fun addedPlayerTeamAndClubAreObserved() = runTest {
        add(Favorite.Player("p1"))
        add(Favorite.Team("t1"))
        add(Favorite.Club("c1"))

        assertEquals(
            setOf(Favorite.Player("p1"), Favorite.Team("t1"), Favorite.Club("c1")),
            observe().first(),
        )
    }

    @Test
    fun addingTheSameFavoriteTwiceKeepsOneEntry() = runTest {
        add(Favorite.Club("c1"))
        add(Favorite.Club("c1"))

        assertEquals(setOf(Favorite.Club("c1")), observe().first())
    }

    @Test
    fun playerAndClubWithTheSameIdAreDifferentFavorites() = runTest {
        add(Favorite.Player("1"))
        add(Favorite.Club("1"))

        assertEquals(setOf(Favorite.Player("1"), Favorite.Club("1")), observe().first())
    }

    @Test
    fun removedFavoriteIsGone() = runTest {
        add(Favorite.Player("p1"))
        add(Favorite.Team("t1"))
        remove(Favorite.Player("p1"))

        assertEquals(setOf(Favorite.Team("t1")), observe().first())
    }

    @Test
    fun removingAFavoriteThatIsNotThereChangesNothing() = runTest {
        add(Favorite.Team("t1"))
        remove(Favorite.Team("other"))

        assertEquals(setOf(Favorite.Team("t1")), observe().first())
    }
}
