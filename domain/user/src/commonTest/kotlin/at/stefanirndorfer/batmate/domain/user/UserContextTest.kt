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
private val orphan = Player("p2", "Orphan", clubId = "missing")

private class FakeSelectionRepository : UserSelectionRepository {
    private val selection = MutableStateFlow<UserSelection>(UserSelection.None)

    override fun observeSelection(): Flow<UserSelection> = selection

    override suspend fun saveSelection(selection: UserSelection) {
        this.selection.value = selection
    }
}

private class FakeDirectory :
    PlayerRepository,
    ClubRepository,
    AssociationRepository {
    override suspend fun getPlayer(id: String) = listOf(anna, orphan).firstOrNull { it.id == id }

    override suspend fun getClub(id: String) = listOf(olympic).firstOrNull { it.id == id }

    override suspend fun getAssociation(id: String) = listOf(wttv).firstOrNull { it.id == id }
}

class UserContextTest {
    private val selections = FakeSelectionRepository()
    private val directory = FakeDirectory()
    private val setMyPlayer = SetMyPlayerUseCase(selections)
    private val setMyClub = SetMyClubUseCase(selections)
    private val observe = ObserveUserContextUseCase(selections, directory, directory, directory)

    @Test
    fun hasNoContextBeforeAnythingIsChosen() = runTest {
        assertEquals(UserContext.None, observe().first())
    }

    @Test
    fun choosingMyPlayerDerivesClubAndAssociation() = runTest {
        setMyPlayer("p1")

        assertEquals(UserContext.MyPlayer(anna, olympic, wttv), observe().first())
    }

    @Test
    fun choosingMyClubDerivesAssociation() = runTest {
        setMyClub("c1")

        assertEquals(UserContext.MyClub(olympic, wttv), observe().first())
    }

    @Test
    fun choosingMyClubReplacesMyPlayer() = runTest {
        setMyPlayer("p1")
        setMyClub("c1")

        assertEquals(UserContext.MyClub(olympic, wttv), observe().first())
    }

    @Test
    fun choosingMyPlayerReplacesMyClub() = runTest {
        setMyClub("c1")
        setMyPlayer("p1")

        assertEquals(UserContext.MyPlayer(anna, olympic, wttv), observe().first())
    }

    @Test
    fun unknownPlayerYieldsNoContext() = runTest {
        setMyPlayer("nobody")

        assertEquals(UserContext.None, observe().first())
    }

    @Test
    fun playerWithUnresolvableClubYieldsNoContext() = runTest {
        setMyPlayer("p2")

        assertEquals(UserContext.None, observe().first())
    }

    @Test
    fun unknownClubYieldsNoContext() = runTest {
        setMyClub("nowhere")

        assertEquals(UserContext.None, observe().first())
    }
}
