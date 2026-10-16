package at.stefanirndorfer.batmate.domain.player

interface PlayerRepository {
    suspend fun getPlayer(id: String): Player?
}
