package at.stefanirndorfer.batmate.core.fakedirectory

import at.stefanirndorfer.batmate.domain.club.Association
import at.stefanirndorfer.batmate.domain.club.AssociationRepository
import at.stefanirndorfer.batmate.domain.club.Club
import at.stefanirndorfer.batmate.domain.club.ClubRepository
import at.stefanirndorfer.batmate.domain.player.Player
import at.stefanirndorfer.batmate.domain.player.PlayerRepository

// TODO: replace with the XTTV-backed Player, Club and Association repositories once the API is documented.
class FakeDirectory :
    PlayerRepository,
    ClubRepository,
    AssociationRepository {
    private val associations = listOf(
        Association("wttv", "Wiener Tischtennisverband (WTTV)"),
        Association("noettv", "Niederösterreichischer Tischtennisverband (NÖTTV)"),
        Association("stttv", "Steirischer Tischtennisverband (STTTV)"),
    )

    private val clubs = listOf(
        Club("olympic", "TTC Olympic", "wttv"),
        Club("voest", "SK VÖEST Wien", "wttv"),
        Club("nussdorf", "Nussdorf TTC", "wttv"),
        Club("stpoelten", "Union Tischtennisclub St. Pölten Nord-West", "noettv"),
        Club("graz", "TTV Graz Liebenau", "stttv"),
    )

    private val players = listOf(
        Player("huber", "Michael Huber", "olympic"),
        Player("gruber", "Anna Gruber", "olympic"),
        Player("bauer", "Lukas Bauer", "voest"),
        Player("wagner", "Sophie Wagner", "nussdorf"),
        Player("steiner", "Jakob Steiner", "stpoelten"),
        Player("pichler", "Katharina Habsburg-Lothringen-Esterházy", "stpoelten"),
        Player("moser", "Felix Moser", "graz"),
        Player("leitner", "Laura Leitner", "graz"),
    )

    override suspend fun getPlayer(id: String): Player? = players.firstOrNull { it.id == id }

    override suspend fun searchPlayers(query: String): List<Player> = players.filter { player ->
        player.name.contains(query, ignoreCase = true) ||
            clubs.firstOrNull { it.id == player.clubId }?.name?.contains(query, ignoreCase = true) == true
    }

    override suspend fun getClub(id: String): Club? = clubs.firstOrNull { it.id == id }

    override suspend fun searchClubs(query: String): List<Club> = clubs.filter { it.name.contains(query, ignoreCase = true) }

    override suspend fun getAssociation(id: String): Association? = associations.firstOrNull { it.id == id }
}
