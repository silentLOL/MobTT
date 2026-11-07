enum LeagueTabAction {
    case retryClicked
    case choosePlayerClicked
    case myPlayerClicked(playerId: String)
    case teamCardClicked(leagueId: String, teamId: String)
}

enum LeagueTabEvent {
    case openChoosePlayer
    case openPlayer(playerId: String)
    case openLeague(leagueId: String, teamId: String)
}
