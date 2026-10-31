package at.stefanirndorfer.batmate.domain.club

interface FixtureRepository {
    /** Every Fixture the Team plays in, home or away, finished or not. */
    suspend fun getFixtures(teamId: String): List<Fixture>

    /** Every Fixture of the League, finished or not, ordered by [Fixture.startsAt]. */
    suspend fun getLeagueFixtures(leagueId: String): List<Fixture>
}
