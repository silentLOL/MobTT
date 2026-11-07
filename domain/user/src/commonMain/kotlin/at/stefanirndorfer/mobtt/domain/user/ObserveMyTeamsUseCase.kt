package at.stefanirndorfer.mobtt.domain.user

import at.stefanirndorfer.mobtt.domain.club.DataUnavailable
import at.stefanirndorfer.mobtt.domain.club.GetTeamDetailUseCase
import at.stefanirndorfer.mobtt.domain.club.TeamDetail
import at.stefanirndorfer.mobtt.domain.club.TeamRepository
import at.stefanirndorfer.mobtt.domain.player.Player
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest

/** My Teams: the Teams whose lineup names My Player. Their Leagues fill the League tab. */
sealed interface MyTeams {
    /** No My Player is chosen, including a user who only chose My Club. */
    data object NoMyPlayer : MyTeams

    /** [teams] carry a League each, in the order the Club lists them; empty when no lineup names [player]. */
    data class Found(val player: Player, val teams: List<TeamDetail>) : MyTeams

    /** XTTV could not answer for My Player's Club Teams. A later change of My Player is still followed. */
    data object Unavailable : MyTeams
}

class ObserveMyTeamsUseCase(
    private val observeUserContext: ObserveUserContextUseCase,
    private val teamRepository: TeamRepository,
    private val getTeamDetail: GetTeamDetailUseCase,
) {
    /**
     * Follows My Player, dropping a lookup still running for the previous one. A single Team XTTV cannot answer for, or
     * one without a League, is left out.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<MyTeams> = observeUserContext().mapLatest { context ->
        when (context) {
            is UserContext.MyPlayer -> try {
                MyTeams.Found(context.player, teamsOf(context.player))
            } catch (e: DataUnavailable) {
                MyTeams.Unavailable
            }
            is UserContext.MyClub, UserContext.None -> MyTeams.NoMyPlayer
        }
    }

    private suspend fun teamsOf(player: Player): List<TeamDetail> = teamRepository.lineupEntriesOf(player).mapNotNull { (team, _) ->
        try {
            getTeamDetail(team.id)?.takeIf { it.league != null }
        } catch (e: DataUnavailable) {
            null
        }
    }
}
