package at.stefanirndorfer.batmate.domain.club

interface AssociationRepository {
    suspend fun getAssociation(id: String): Association?
}
