enum FixtureDetailAction {
    case retryClicked
    case teamClicked(teamId: String)
}

enum FixtureDetailEvent {
    case openTeam(teamId: String)
}
