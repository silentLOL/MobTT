package at.stefanirndorfer.batmate.domain.club

interface ClubRepository {
    suspend fun getClub(id: String): Club?

    /** A blank [query] lists everything; otherwise matches on Club name. */
    suspend fun searchClubs(query: String): List<Club>

    /** The Clubs of one Association, in the repository's order. */
    suspend fun getAssociationClubs(associationId: String): List<Club> = searchClubs("").filter { it.associationId == associationId }
}
