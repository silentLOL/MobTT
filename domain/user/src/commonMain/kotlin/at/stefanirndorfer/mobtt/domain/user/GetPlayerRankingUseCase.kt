package at.stefanirndorfer.mobtt.domain.user

import at.stefanirndorfer.mobtt.domain.club.LeagueRepository
import at.stefanirndorfer.mobtt.domain.player.PlayerRepository
import at.stefanirndorfer.mobtt.domain.player.RankedPlayer

/** [teamName] is null when the Team is missing from the League's standings. */
data class PlayerRankingRow(val ranked: RankedPlayer, val teamName: String?)

class GetPlayerRankingUseCase(
    private val playerRepository: PlayerRepository,
    private val leagueRepository: LeagueRepository,
) {
    /**
     * The League's Players by rank, unranked Players last by name. Team names come from the League's standings, which
     * are cached with the ranking, so no Club or Team is asked for.
     */
    suspend operator fun invoke(leagueId: String): List<PlayerRankingRow> {
        val teamNames = leagueRepository.getStandings(leagueId).associate { it.teamId to it.teamName }
        return playerRepository.getPlayerRanking(leagueId)
            .sortedWith(compareBy<RankedPlayer>({ it.rank == null }, { it.rank }, { it.player.name.lowercase() }))
            .map { PlayerRankingRow(it, teamNames[it.teamId]) }
    }
}
