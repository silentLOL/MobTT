enum LeagueTabUiState: Equatable {
    case loading
    /// No My Player is chosen, which includes a user who only chose My Club.
    case noMyPlayer
    /// My Player is chosen but no Team's lineup names them.
    case noTeam(playerId: String, playerName: String)
    case unavailable
    /// The League of My Player's Team, shown as the tab's root with that Team highlighted.
    case league(leagueId: String, teamId: String)
}
