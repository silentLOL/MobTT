package at.stefanirndorfer.batmate.domain.club

interface LeagueRepository {
    suspend fun getLeague(id: String): League?

    /** Ordered by [Standing.position]. */
    suspend fun getStandings(leagueId: String): List<Standing>
}
