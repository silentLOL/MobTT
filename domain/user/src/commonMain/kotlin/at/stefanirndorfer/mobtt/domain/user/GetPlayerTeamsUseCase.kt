package at.stefanirndorfer.mobtt.domain.user

import at.stefanirndorfer.mobtt.domain.club.DataUnavailable
import at.stefanirndorfer.mobtt.domain.club.TeamRepository
import at.stefanirndorfer.mobtt.domain.player.Player

/** The Player screen's Teams section and stat row, which share the Teams' lineups. */
class GetPlayerTeamsUseCase(
    private val teamRepository: TeamRepository,
) {
    /** Throws [DataUnavailable] when the Club's Team list cannot be read; a single failing lineup leaves only that Team out. */
    suspend operator fun invoke(player: Player): PlayerTeams {
        val lineup = teamRepository.lineupEntriesOf(player)
        return PlayerTeams(
            teams = lineup.map { it.first },
            rcPoints = lineup.firstNotNullOfOrNull { it.second.rcPoints },
            ageGroup = lineup.firstNotNullOfOrNull { it.second.ageGroup },
        )
    }
}
