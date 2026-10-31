enum LeagueDetailAction {
    case retryClicked
    case onlyTeamFixturesToggled(Bool)
    case teamClicked(teamId: String)
}

enum LeagueDetailEvent {
    case openTeam(teamId: String)
}
