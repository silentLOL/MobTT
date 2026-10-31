package at.stefanirndorfer.mobtt.domain.player

/** Remembers the Players the user picked, and only those, so they resolve again without searching. */
interface PickedPlayerStore {
    suspend fun save(player: Player)

    suspend fun get(id: String): Player?
}
