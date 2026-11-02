package at.stefanirndorfer.mobtt.domain.user

import at.stefanirndorfer.mobtt.domain.club.ClubRepository
import at.stefanirndorfer.mobtt.domain.club.DataUnavailable
import at.stefanirndorfer.mobtt.domain.player.Player
import at.stefanirndorfer.mobtt.domain.player.PlayerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/** [clubName] is null when the Player's Club can no longer be resolved. */
data class PlayerSearchResult(val player: Player, val clubName: String?)

class ListLeaguePlayersUseCase(
    private val playerRepository: PlayerRepository,
    private val clubRepository: ClubRepository,
) {
    /** The League's Players with every Club name that could be resolved. */
    suspend operator fun invoke(leagueId: String): List<PlayerSearchResult> = stream(leagueId).last()

    /**
     * The Players as soon as the standings arrive, without Club names, then again each time a Club name resolves.
     * XTTV can be slow, and the names are only subtitles, so they must not hold the list back.
     */
    fun stream(leagueId: String): Flow<List<PlayerSearchResult>> = channelFlow {
        val players = playerRepository.getLeaguePlayers(leagueId)
        val clubNames = mutableMapOf<String, String>()
        send(players.map { PlayerSearchResult(it, null) })
        val lock = Mutex()
        players.map { it.clubId }.distinct().forEach { clubId ->
            launch {
                val name = clubName(clubId) ?: return@launch
                lock.withLock {
                    clubNames[clubId] = name
                    send(players.map { PlayerSearchResult(it, clubNames[it.clubId]) })
                }
            }
        }
    }

    /** The name is only a subtitle, so a Club XTTV cannot answer for must not hide the League's Players. */
    private suspend fun clubName(clubId: String): String? = try {
        clubRepository.getClub(clubId)?.name
    } catch (e: DataUnavailable) {
        null
    }
}
