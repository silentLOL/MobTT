enum FixtureDetailAction {
    case retryClicked
    case teamClicked(teamId: String)
    case shareClicked
    case venueClicked
}

enum FixtureDetailEvent {
    case openTeam(teamId: String)
    /// Opens a maps app searching for `searchText`, the Venue's name and address.
    case openVenue(searchText: String)
    /// Opens the share sheet for `detail`, ending with its Fixture `link`, or without one while there is no links domain.
    case share(detail: FixtureDetailUi, link: String?)
}
