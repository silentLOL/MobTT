import Foundation

enum PlayerDetailAction {
    case favoriteClicked
    case clubClicked
    case teamClicked(id: String)
    case clubRetryClicked
    case teamsRetryClicked
    case ratingHistoryToggled
    case ratingHistoryRetryClicked
    case ratingHistorySpanChosen(RatingHistorySpan)
    case ratingInfoClicked
    case ratingInfoDismissed
    case ratingInfoLinkClicked
}

enum PlayerDetailEvent: Equatable {
    case openClub(clubId: String)
    case openTeam(teamId: String)
    /// Opened in the in-app browser.
    case openWebPage(url: URL)
}
