enum ClubAction {
    case chooseClicked
    case retryClicked
    case favoriteClicked
    case teamClicked(teamId: String)
    case playerClicked(playerId: String, name: String)
}

enum ClubEvent {
    case openSettings
    case openTeam(teamId: String)
    case openPlayer(playerId: String)
}
