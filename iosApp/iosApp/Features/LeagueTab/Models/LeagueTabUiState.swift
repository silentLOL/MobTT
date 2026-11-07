/// One of My Player's Teams with its League card.
struct LeagueTeamCardUi: Identifiable, Equatable {
    var id: String { teamId }
    let teamId: String
    let teamName: String
    let league: TeamLeagueUi
}

enum LeagueTabUiState: Equatable {
    case loading
    /// No My Player is chosen, which includes a user who only chose My Club.
    case noMyPlayer
    /// My Player is chosen but no Team's lineup names them.
    case noTeam(playerId: String, playerName: String)
    case unavailable
    /// The League of My Player's only Team, shown as the tab's root with that Team highlighted.
    case league(leagueId: String, teamId: String)
    /// My Player plays in two or more Teams: one card each.
    case teams([LeagueTeamCardUi])
}
