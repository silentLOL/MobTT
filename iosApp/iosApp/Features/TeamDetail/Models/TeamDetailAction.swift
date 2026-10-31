enum TeamDetailAction {
    case retryClicked
    case favoriteClicked
    case leagueClicked(leagueId: String)
}

enum TeamDetailEvent {
    case openLeague(leagueId: String)
}
