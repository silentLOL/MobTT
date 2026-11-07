package at.stefanirndorfer.mobtt.feature.club.impl.presentation.leaguedetail.models

import at.stefanirndorfer.mobtt.domain.club.Fixture
import at.stefanirndorfer.mobtt.domain.club.LeagueDetail

sealed interface LeagueDetailUiState {
    data object Loading : LeagueDetailUiState

    data object Error : LeagueDetailUiState

    /**
     * [highlightTeamId] is the Team marked in the table and the Fixtures, if any. Only then can the Fixtures be
     * narrowed to that Team with [onlyTeamFixtures]. [openedFromTeamId] is the Team whose screen the user came from;
     * its row does not open it again.
     */
    data class Content(
        val detail: LeagueDetail,
        val highlightTeamId: String? = null,
        val onlyTeamFixtures: Boolean = false,
        val openedFromTeamId: String? = highlightTeamId,
    ) : LeagueDetailUiState {
        fun isThisTeam(teamId: String): Boolean = teamId == highlightTeamId

        fun opensTeam(teamId: String): Boolean = teamId != openedFromTeamId

        fun isTeamFixture(fixture: Fixture): Boolean = isThisTeam(fixture.homeTeamId) || isThisTeam(fixture.awayTeamId)

        val visibleFixtures: List<Fixture>
            get() = if (onlyTeamFixtures) {
                detail.fixtures.filter(::isTeamFixture)
            } else {
                detail.fixtures
            }
    }
}
