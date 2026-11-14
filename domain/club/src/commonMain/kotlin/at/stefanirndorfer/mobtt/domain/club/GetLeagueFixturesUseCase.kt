package at.stefanirndorfer.mobtt.domain.club

/** The League's Fixtures by kickoff. Read apart from [GetLeagueDetailUseCase], so a League opens without them. */
class GetLeagueFixturesUseCase(
    private val fixtureRepository: FixtureRepository,
) {
    suspend operator fun invoke(leagueId: String): List<Fixture> = fixtureRepository.getLeagueFixtures(leagueId).sortedBy { it.startsAt }
}
