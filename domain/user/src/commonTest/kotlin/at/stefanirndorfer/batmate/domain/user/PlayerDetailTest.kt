package at.stefanirndorfer.batmate.domain.user

import at.stefanirndorfer.batmate.domain.club.Association
import at.stefanirndorfer.batmate.domain.club.AssociationRepository
import at.stefanirndorfer.batmate.domain.club.Club
import at.stefanirndorfer.batmate.domain.club.ClubRepository
import at.stefanirndorfer.batmate.domain.player.Player
import at.stefanirndorfer.batmate.domain.player.PlayerRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

private class PlayerDirectory(
    val players: List<Player>,
    val clubs: List<Club>,
    val associations: List<Association>,
) : PlayerRepository,
    ClubRepository,
    AssociationRepository {
    override suspend fun getPlayer(id: String) = players.firstOrNull { it.id == id }

    override suspend fun getLeaguePlayers(leagueId: String) = players

    override suspend fun getClub(id: String) = clubs.firstOrNull { it.id == id }

    override suspend fun searchClubs(query: String) = clubs

    override suspend fun getAssociation(id: String) = associations.firstOrNull { it.id == id }

    override suspend fun getAssociations() = associations
}

class PlayerDetailTest {
    private val anna = Player("p1", "Anna", clubId = "c1")
    private val olympic = Club("c1", "TTC Olympic", associationId = "a1")
    private val wttv = Association("a1", "WTTV")

    private fun getPlayerDetail(
        players: List<Player> = listOf(anna),
        clubs: List<Club> = listOf(olympic),
        associations: List<Association> = listOf(wttv),
    ) = PlayerDirectory(players, clubs, associations).let { GetPlayerDetailUseCase(it, it, it) }

    @Test
    fun playerShowsNameClubAndAssociation() = runTest {
        assertEquals(PlayerDetail(anna, "TTC Olympic", "WTTV"), getPlayerDetail()("p1"))
    }

    @Test
    fun unresolvablePlayerIsNull() = runTest {
        assertNull(getPlayerDetail()("gone"))
    }

    @Test
    fun playerWithoutResolvableClubKeepsTheirNameWithoutClubOrAssociation() = runTest {
        assertEquals(PlayerDetail(anna, null, null), getPlayerDetail(clubs = emptyList())("p1"))
    }

    @Test
    fun playerWhoseAssociationIsGoneKeepsTheClub() = runTest {
        assertEquals(PlayerDetail(anna, "TTC Olympic", null), getPlayerDetail(associations = emptyList())("p1"))
    }
}
