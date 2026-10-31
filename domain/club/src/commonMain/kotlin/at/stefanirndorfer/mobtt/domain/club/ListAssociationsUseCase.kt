package at.stefanirndorfer.mobtt.domain.club

class ListAssociationsUseCase(private val associationRepository: AssociationRepository) {
    suspend operator fun invoke(): List<Association> = associationRepository.getAssociations()
}
