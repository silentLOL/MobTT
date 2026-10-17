enum HomeAction {
    case chooseClicked
    case addFavoriteClicked
    case viewClubClicked
    case favoriteClubClicked(id: String)
    case removeFavoriteClicked(HomeFavoriteUi)
    case pickerQueryChanged(String)
    case pickerPlayerClicked(id: String)
    case pickerClubClicked(id: String)
    case pickerDismissed
}

enum HomeEvent {
    case openSettings
    case openMyClub
    case openClub(clubId: String)
}
