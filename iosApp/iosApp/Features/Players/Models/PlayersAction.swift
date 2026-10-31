enum PlayersAction {
    case associationRowClicked
    case clubRowClicked
    case sheetQueryChanged(String)
    case sheetEntryClicked(id: String)
    case sheetRetryClicked
    case sheetDismissed
    case queryChanged(String)
    case playerClicked(playerId: String, name: String)
    case retryClicked
}

enum PlayersEvent: Equatable {
    case openPlayer(playerId: String)
    /// A Club was picked in `PlayersMode.pickClub`; nothing was saved.
    case clubChosen(clubId: String)
    /// `PlayersMode.myPlayer` or `.myClub` saved the choice.
    case done
}

/// What the screen is for. `browse` is the Players tab; the others are the same picking flow put to another use.
enum PlayersMode: Hashable {
    /// Find a Player and open them.
    case browse
    /// Find a Player and save them as My Player.
    case myPlayer
    /// Find a Club and save it as My Club; the flow ends at the Club.
    case myClub
    /// Find a Club and hand it to the caller; the flow ends at the Club.
    case pickClub

    var showsPlayers: Bool { self == .browse || self == .myPlayer }
}
