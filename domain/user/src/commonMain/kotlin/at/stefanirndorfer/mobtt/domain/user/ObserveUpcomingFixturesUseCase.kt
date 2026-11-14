package at.stefanirndorfer.mobtt.domain.user

import at.stefanirndorfer.mobtt.domain.club.GetClubOverviewUseCase
import at.stefanirndorfer.mobtt.domain.club.NextFixture
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Instant

/**
 * XTTV links a Player to a Club, not to Teams, so My Player shows the Teams of their Club until
 * lineups are available.
 */
class ObserveUpcomingFixturesUseCase(
    private val observeUserContext: ObserveUserContextUseCase,
    private val getClubOverview: GetClubOverviewUseCase,
) {
    operator fun invoke(): Flow<UpcomingFixtures> = observeUserContext().map { context ->
        val club = context.club ?: return@map UpcomingFixtures.NoSelection
        load(club.id)
    }

    private suspend fun load(clubId: String): UpcomingFixtures = try {
        getClubOverview(clubId)
            ?.teams
            ?.map { UpcomingFixture(it.team, it.nextFixture) }
            ?.sortedBy { (it.next as? NextFixture.Scheduled)?.fixture?.startsAt ?: Instant.DISTANT_FUTURE }
            ?.let(UpcomingFixtures::Loaded)
            ?: UpcomingFixtures.Unavailable
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        UpcomingFixtures.Unavailable
    }
}
