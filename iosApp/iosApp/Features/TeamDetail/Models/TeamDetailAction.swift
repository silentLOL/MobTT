enum TeamDetailAction {
    case retryClicked
    case favoriteClicked
    case leagueClicked(leagueId: String)
    case playerClicked(playerId: String, name: String)
}

enum TeamDetailEvent {
    case openLeague(leagueId: String, teamId: String)
    case openPlayer(playerId: String)
}
