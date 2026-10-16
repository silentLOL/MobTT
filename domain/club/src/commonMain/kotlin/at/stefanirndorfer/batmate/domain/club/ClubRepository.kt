package at.stefanirndorfer.batmate.domain.club

interface ClubRepository {
    suspend fun getClub(id: String): Club?
}
