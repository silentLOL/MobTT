package at.stefanirndorfer.mobtt.domain.user

import at.stefanirndorfer.mobtt.domain.club.DataUnavailable
import at.stefanirndorfer.mobtt.domain.club.LineupPlayer
import at.stefanirndorfer.mobtt.domain.club.Team
import at.stefanirndorfer.mobtt.domain.club.TeamRepository
import at.stefanirndorfer.mobtt.domain.player.Player

/**
 * The Teams of [player]'s Club whose lineup names them, with their lineup entry. XTTV has no Player-to-Team lookup,
 * so every Team of the Club is asked for its lineup. A lineup XTTV cannot answer for leaves only that Team out; the
 * Club's Team list failing throws [DataUnavailable].
 */
internal suspend fun TeamRepository.lineupEntriesOf(player: Player): List<Pair<Team, LineupPlayer>> = getTeams(player.clubId).mapNotNull { team ->
    try {
        getLineup(team.id).firstOrNull { it.playerId == player.id }?.let { team to it }
    } catch (e: DataUnavailable) {
        null
    }
}
