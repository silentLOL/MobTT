package at.stefanirndorfer.batmate.domain.player

interface PlayerRepository {
    suspend fun getPlayer(id: String): Player?

    /** The Players of one League, ordered by name. XTTV offers no search by name, so Players are found through their League. */
    suspend fun getLeaguePlayers(leagueId: String): List<Player>

    /** Called once the user picked the Player with [id] from a League's list, so [getPlayer] can resolve them later. */
    suspend fun keepPicked(id: String) = Unit
}
