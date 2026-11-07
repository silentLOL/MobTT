enum PlayerDetailAction {
    case favoriteClicked
    case clubClicked
    case teamClicked(id: String)
    case clubRetryClicked
    case teamsRetryClicked
    case ratingHistoryToggled
    case ratingHistoryRetryClicked
    case ratingHistorySpanChosen(RatingHistorySpan)
}

enum PlayerDetailEvent: Equatable {
    case openClub(clubId: String)
    case openTeam(teamId: String)
}
