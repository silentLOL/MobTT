package at.stefanirndorfer.mobtt.domain.club

interface LeagueRepository {
    suspend fun getLeague(id: String): League?

    suspend fun getLeagues(associationId: String): List<League>

    /** Ordered by [Standing.position]. */
    suspend fun getStandings(leagueId: String): List<Standing>
}
