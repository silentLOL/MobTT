enum HomeAction {
    case chooseClicked
    case addFavoriteClicked
    case removeFavoriteClicked(HomeFavoriteUi)
    case pickerQueryChanged(String)
    case pickerPlayerClicked(id: String)
    case pickerClubClicked(id: String)
    case pickerDismissed
}

enum HomeEvent {
    case openSettings
}
