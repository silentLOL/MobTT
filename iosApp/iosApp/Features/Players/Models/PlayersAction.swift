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

enum PlayersEvent {
    case openPlayer(playerId: String)
}
