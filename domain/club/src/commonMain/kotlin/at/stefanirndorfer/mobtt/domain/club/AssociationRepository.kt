package at.stefanirndorfer.mobtt.domain.club

interface AssociationRepository {
    suspend fun getAssociation(id: String): Association?

    suspend fun getAssociations(): List<Association>
}
