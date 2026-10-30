import Foundation

struct PlayerDetailUi: Equatable {
    let playerName: String
    /// Nil when XTTV no longer resolves the Club.
    let clubName: String?
    /// Nil when XTTV no longer resolves the Association.
    let associationName: String?
    var isFavorite = false
}

enum PlayerDetailUiState: Equatable {
    case loading
    /// The Player cannot be resolved, or XTTV cannot be reached.
    case unavailable
    case content(PlayerDetailUi)
}
