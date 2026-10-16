package at.stefanirndorfer.batmate.domain.player

interface PlayerRepository {
    suspend fun getPlayer(id: String): Player?

    /** A blank [query] lists everything; otherwise matches on Player name or Club name. */
    suspend fun searchPlayers(query: String): List<Player>
}
