package at.stefanirndorfer.batmate.domain.user

import at.stefanirndorfer.batmate.domain.club.Association
import at.stefanirndorfer.batmate.domain.club.AssociationRepository
import at.stefanirndorfer.batmate.domain.club.Club
import at.stefanirndorfer.batmate.domain.club.ClubRepository
import at.stefanirndorfer.batmate.domain.player.Player
import at.stefanirndorfer.batmate.domain.player.PlayerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

private val wttv = Association("a1", "WTTV")
private val olympic = Club("c1", "TTC Olympic", associationId = "a1")
private val anna = Player("p1", "Anna", clubId = "c1")

private class Directory :
    PlayerRepository,
    ClubRepository,
    AssociationRepository {
    override suspend fun getPlayer(id: String) = listOf(anna).firstOrNull { it.id == id }

    override suspend fun searchPlayers(query: String) = listOf(anna)

    override suspend fun getClub(id: String) = listOf(olympic).firstOrNull { it.id == id }

    override suspend fun searchClubs(query: String) = listOf(olympic)

    override suspend fun getAssociation(id: String) = listOf(wttv).firstOrNull { it.id == id }
}

private class Favorites(initial: Set<Favorite>) : FavoritesRepository {
    private val favorites = MutableStateFlow(initial)

    override fun observeFavorites(): Flow<Set<Favorite>> = favorites

    override suspend fun add(favorite: Favorite) {
        favorites.value += favorite
    }

    override suspend fun remove(favorite: Favorite) {
        favorites.value -= favorite
    }
}

class ResolvedFavoritesTest {
    private val directory = Directory()

    private fun observe(vararg favorites: Favorite) = ObserveResolvedFavoritesUseCase(
        Favorites(favorites.toSet()),
        directory,
        directory,
        directory,
    )

    @Test
    fun playerFavoriteShowsNameAndClub() = runTest {
        val resolved = observe(Favorite.Player("p1"))().first()

        assertEquals(listOf(ResolvedFavorite(Favorite.Player("p1"), "Anna", "TTC Olympic")), resolved)
    }

    @Test
    fun clubFavoriteShowsNameAndAssociation() = runTest {
        val resolved = observe(Favorite.Club("c1"))().first()

        assertEquals(listOf(ResolvedFavorite(Favorite.Club("c1"), "TTC Olympic", "WTTV")), resolved)
    }

    @Test
    fun playerSearchSkipsQueriesShorterThanThreeCharacters() = runTest {
        assertEquals(emptyList(), SearchPlayersUseCase(directory, directory)(" an "))
    }

    @Test
    fun playerSearchResultsCarryTheClubName() = runTest {
        val results = SearchPlayersUseCase(directory, directory)("  ann ")

        assertEquals(listOf(PlayerSearchResult(anna, "TTC Olympic")), results)
    }

    @Test
    fun favoritesThatCannotBeResolvedAreLeftOut() = runTest {
        val resolved = observe(Favorite.Player("gone"), Favorite.Team("t1"), Favorite.Club("c1"))().first()

        assertEquals(listOf(Favorite.Club("c1")), resolved.map { it.favorite })
    }
}
