package at.stefanirndorfer.batmate.domain.club

interface TeamRepository {
    suspend fun getTeams(clubId: String): List<Team>
}
