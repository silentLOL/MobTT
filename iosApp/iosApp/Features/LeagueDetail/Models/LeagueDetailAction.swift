enum LeagueDetailAction {
    case retryClicked
    case onlyTeamFixturesToggled(Bool)
    case teamClicked(teamId: String)
    case fixtureInfoClicked(fixtureId: String)
    case playerClicked(playerId: String)
}

enum LeagueDetailEvent {
    case openTeam(teamId: String)
    case openFixture(fixtureId: String)
    case openPlayer(playerId: String)
}
