package at.stefanirndorfer.batmate.domain.club

interface ClubRepository {
    suspend fun getClub(id: String): Club?

    /** A blank [query] lists everything; otherwise matches on Club name. */
    suspend fun searchClubs(query: String): List<Club>
}
