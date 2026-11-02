package at.stefanirndorfer.mobtt.domain.club

interface FixtureRepository {
    /** Every Fixture the Team plays in, home or away, finished or not. */
    suspend fun getFixtures(teamId: String): List<Fixture>

    /** Every Fixture of the League, finished or not, ordered by [Fixture.startsAt]. */
    suspend fun getLeagueFixtures(leagueId: String): List<Fixture>

    /** The Fixture with its Matches, or null when it is not known. Throws [DataUnavailable] when it cannot be loaded. */
    suspend fun getFixtureDetail(fixtureId: String): FixtureDetail?
}
