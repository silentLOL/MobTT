enum ClubAction {
    case chooseClicked
    case retryClicked
    case teamClicked(teamId: String)
}

enum ClubEvent {
    case openSettings
    case openTeam(teamId: String)
}
