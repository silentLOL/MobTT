package at.stefanirndorfer.mobtt.domain.club

class GetFixtureDetailUseCase(private val fixtureRepository: FixtureRepository) {
    /** Null when the Fixture cannot be resolved. */
    suspend operator fun invoke(fixtureId: String): FixtureDetail? = fixtureRepository.getFixtureDetail(fixtureId)
}
