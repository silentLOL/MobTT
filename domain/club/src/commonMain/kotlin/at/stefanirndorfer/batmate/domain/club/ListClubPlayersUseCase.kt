package at.stefanirndorfer.batmate.domain.club

/** XTTV has no Player list per Club, so the Players are those on any of the Club's lineups, once each, ordered by name. */
class ListClubPlayersUseCase(private val teamRepository: TeamRepository) {
    suspend operator fun invoke(clubId: String): List<LineupPlayer> = teamRepository.getTeams(clubId)
        .flatMap { teamRepository.getLineup(it.id) }
        .distinctBy { it.playerId }
        .sortedBy { it.name.lowercase() }
}
