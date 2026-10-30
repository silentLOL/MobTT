package at.stefanirndorfer.batmate.domain.user

import at.stefanirndorfer.batmate.domain.club.Association
import at.stefanirndorfer.batmate.domain.club.AssociationRepository
import at.stefanirndorfer.batmate.domain.club.Club
import at.stefanirndorfer.batmate.domain.club.ClubRepository
import at.stefanirndorfer.batmate.domain.club.LineupPlayer
import at.stefanirndorfer.batmate.domain.club.Team
import at.stefanirndorfer.batmate.domain.club.TeamRepository
import at.stefanirndorfer.batmate.domain.player.Player
import at.stefanirndorfer.batmate.domain.player.PlayerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

private val wttv = Association("a1", "WTTV")
private val clubs = listOf(Club("c1", "TTC Olympic", "a1"), Club("c2", "Nussdorf TTC", "a1"))
private val players = listOf(Player("p1", "Anna", "c1"), Player("p2", "Jakob", "c1"))
private val team = Team("t1", "Olympic 1", "c1", "l1")

private class HomeDirectory :
    PlayerRepository,
    ClubRepository,
    AssociationRepository,
    TeamRepository {
    override suspend fun getPlayer(id: String) = players.firstOrNull { it.id == id }

    override suspend fun getLeaguePlayers(leagueId: String) = players

    override suspend fun getClub(id: String) = clubs.firstOrNull { it.id == id }

    override suspend fun searchClubs(query: String) = clubs

    override suspend fun getAssociation(id: String) = wttv.takeIf { it.id == id }

    override suspend fun getAssociations() = listOf(wttv)

    override suspend fun getTeam(id: String) = team.takeIf { it.id == id }

    override suspend fun getTeams(clubId: String) = listOf(team)

    override suspend fun getLineup(teamId: String) = emptyList<LineupPlayer>()
}

private class StoredFavorites(initial: Set<Favorite>) : FavoritesRepository {
    val favorites = MutableStateFlow(initial)

    override fun observeFavorites(): Flow<Set<Favorite>> = favorites

    override suspend fun add(favorite: Favorite) {
        favorites.value += favorite
    }

    override suspend fun remove(favorite: Favorite) {
        favorites.value -= favorite
    }
}

class HomeFavoritesTest {
    private val directory = HomeDirectory()
    private val selection = FakeSelectionRepository()
    private val stored = StoredFavorites(
        setOf(Favorite.Player("p1"), Favorite.Player("p2"), Favorite.Club("c1"), Favorite.Club("c2"), Favorite.Team("t1")),
    )
    private val observe = ObserveHomeFavoritesUseCase(
        ObserveResolvedFavoritesUseCase(stored, directory, directory, directory, directory),
        selection,
    )

    private suspend fun listed() = observe().first().map { it.favorite }

    @Test
    fun nothingIsHiddenWithoutAHero() = runTest {
        assertEquals(stored.favorites.value, listed().toSet())
    }

    @Test
    fun myPlayerIsHiddenAndEveryOtherFavoriteStays() = runTest {
        selection.saveSelection(UserSelection.MyPlayer("p1"))

        assertEquals(stored.favorites.value - Favorite.Player("p1"), listed().toSet())
    }

    @Test
    fun myClubIsHiddenButAPlayerOfThatClubStays() = runTest {
        selection.saveSelection(UserSelection.MyClub("c1"))

        assertEquals(stored.favorites.value - Favorite.Club("c1"), listed().toSet())
    }

    @Test
    fun aHeroThatIsNotAFavoriteHidesNothing() = runTest {
        selection.saveSelection(UserSelection.MyPlayer("unknown"))

        assertEquals(stored.favorites.value, listed().toSet())
    }

    @Test
    fun changingTheHeroBringsTheHiddenFavoriteBack() = runTest {
        selection.saveSelection(UserSelection.MyPlayer("p1"))
        assertEquals(false, Favorite.Player("p1") in listed())

        selection.saveSelection(UserSelection.MyClub("c2"))

        val after = listed()
        assertEquals(true, Favorite.Player("p1") in after)
        assertEquals(false, Favorite.Club("c2") in after)
    }

    @Test
    fun hidingNeverTouchesTheStoredFavorites() = runTest {
        val before = stored.favorites.value
        selection.saveSelection(UserSelection.MyPlayer("p1"))
        listed()

        assertEquals(before, stored.favorites.value)
    }
}
