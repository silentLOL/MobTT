enum FixtureLinkAction {
    /// The app was opened with `url`; `coldStart` when the link started it rather than reaching it while running.
    case linkReceived(url: String, coldStart: Bool)
}

enum FixtureLinkEvent {
    case openFixture(fixtureId: String)
}
