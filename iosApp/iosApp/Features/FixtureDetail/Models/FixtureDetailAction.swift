enum FixtureDetailAction {
    case retryClicked
    case teamClicked(teamId: String)
    case shareClicked
}

enum FixtureDetailEvent {
    case openTeam(teamId: String)
    /// Opens the share sheet for `detail`, ending with its Fixture `link`.
    case share(detail: FixtureDetailUi, link: String)
}
