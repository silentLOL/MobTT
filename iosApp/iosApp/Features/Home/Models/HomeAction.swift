enum HomeAction {
    case chooseClicked
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
    case openSettings
    case openClub(clubId: String)
    case openTeam(teamId: String)
    case openPlayer(playerId: String)
}
