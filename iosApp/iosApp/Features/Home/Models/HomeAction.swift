enum HomeAction {
    case chooseClicked
    case choosePlayerClicked
    case chooseClubClicked
    case chooseDismissed
    case addFavoriteClicked
    case heroPlayerClicked(id: String)
    case heroClubClicked(id: String)
    case favoriteClubClicked(id: String)
    case favoriteTeamClicked(id: String)
    case favoritePlayerClicked(id: String)
    case pickerQueryChanged(String)
    case browsePlayersClicked
    case pickerDrillDownEntryClicked(id: String)
    case pickerDrillDownBackClicked
    case pickerClubClicked(id: String)
    case pickerDismissed
}

enum HomeEvent {
    /// The user wants to choose My Player: open the Players flow in its choosing mode.
    case chooseMyPlayer
    /// The user wants to choose My Club: open the Association, Club flow.
    case chooseMyClub
    case openClub(clubId: String)
    case openTeam(teamId: String)
    case openPlayer(playerId: String)
}
