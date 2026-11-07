package at.stefanirndorfer.mobtt.feature.club.impl.presentation.leaguetab.models

sealed interface LeagueTabUiState {
    data object Loading : LeagueTabUiState

    /** No My Player is chosen, which includes a user who only chose My Club. */
    data object NoMyPlayer : LeagueTabUiState

    /** My Player is chosen but no Team's lineup names them. */
    data class NoTeam(val playerId: String, val playerName: String) : LeagueTabUiState

    data object Unavailable : LeagueTabUiState

    /** The League of My Player's Team [teamId], shown as the tab's root with that Team highlighted. */
    data class League(val leagueId: String, val teamId: String) : LeagueTabUiState
}
