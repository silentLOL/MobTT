enum LeagueTabAction {
    case retryClicked
    case choosePlayerClicked
    case myPlayerClicked(playerId: String)
}

enum LeagueTabEvent {
    case openChoosePlayer
    case openPlayer(playerId: String)
}
