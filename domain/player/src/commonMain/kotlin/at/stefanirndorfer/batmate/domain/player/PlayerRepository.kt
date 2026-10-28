package at.stefanirndorfer.batmate.domain.player

interface PlayerRepository {
    suspend fun getPlayer(id: String): Player?

    /** A blank [query] lists everything where the source allows it; otherwise matches on Player name (and Club name in the fake directory). */
    suspend fun searchPlayers(query: String): List<Player>

    /** Called once the user picked the Player with [id] from a search, so [getPlayer] can resolve them later. */
    suspend fun keepPicked(id: String) = Unit
}
