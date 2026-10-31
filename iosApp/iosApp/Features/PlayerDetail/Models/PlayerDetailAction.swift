enum PlayerDetailAction {
    case favoriteClicked
    case clubClicked
    case teamClicked(id: String)
}

enum PlayerDetailEvent: Equatable {
    case openClub(clubId: String)
    case openTeam(teamId: String)
}
