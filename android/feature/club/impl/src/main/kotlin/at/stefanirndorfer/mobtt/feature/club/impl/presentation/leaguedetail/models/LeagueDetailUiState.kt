package at.stefanirndorfer.mobtt.feature.club.impl.presentation.leaguedetail.models

import at.stefanirndorfer.mobtt.domain.club.Fixture
import at.stefanirndorfer.mobtt.domain.club.LeagueDetail
import at.stefanirndorfer.mobtt.domain.user.PlayerRankingRow

sealed interface LeagueDetailUiState {
    data object Loading : LeagueDetailUiState

    data object Error : LeagueDetailUiState

    /**
     * [highlightTeamId] is the Team marked in the table and the Fixtures, if any. Only then can the Fixtures be
     * narrowed to that Team with [onlyTeamFixtures]. [openedFromTeamId] is the Team whose screen the user came from;
     * its row does not open it again. [myPlayerId] marks My Player in the [ranking]. [fixtures] and [ranking] start
     * collapsed and load the first time they are expanded.
     */
    data class Content(
        val detail: LeagueDetail,
        val highlightTeamId: String? = null,
        val onlyTeamFixtures: Boolean = false,
        val openedFromTeamId: String? = highlightTeamId,
        val fixtures: SectionUi<List<Fixture>> = SectionUi.Loading,
        val fixturesExpanded: Boolean = false,
        val ranking: SectionUi<List<PlayerRankingRow>> = SectionUi.Loading,
        val rankingExpanded: Boolean = false,
        val myPlayerId: String? = null,
    ) : LeagueDetailUiState {
        fun isMyPlayer(playerId: String): Boolean = playerId == myPlayerId

        /**
         * A Team highlighted without coming from its own screen is one of My Teams: only the League tab opens a League
         * that way. It is then called "my Team" rather than "this Team".
         */
        val highlightIsMyTeam: Boolean
            get() = highlightTeamId != null && openedFromTeamId == null

        fun isThisTeam(teamId: String): Boolean = teamId == highlightTeamId

        fun opensTeam(teamId: String): Boolean = teamId != openedFromTeamId

        fun isTeamFixture(fixture: Fixture): Boolean = isThisTeam(fixture.homeTeamId) || isThisTeam(fixture.awayTeamId)

        /** Empty until the Fixtures are loaded. */
        val visibleFixtures: List<Fixture>
            get() {
                val loaded = (fixtures as? SectionUi.Loaded)?.value.orEmpty()
                return if (onlyTeamFixtures) {
                    loaded.filter(::isTeamFixture)
                } else {
                    loaded
                }
            }
    }
}
