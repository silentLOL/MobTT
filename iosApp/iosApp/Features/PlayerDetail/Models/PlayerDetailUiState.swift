import Foundation

struct PlayerTeamUi: Identifiable, Equatable {
    let id: String
    let name: String
}

struct PlayerDetailUi: Equatable {
    let playerName: String
    let clubId: String
    /// Nil when XTTV no longer resolves the Club.
    let clubName: String?
    /// Nil when XTTV no longer resolves the Association.
    let associationName: String?
    /// The Teams of the Club whose lineup lists the Player; empty when none does or XTTV cannot say.
    var teams: [PlayerTeamUi] = []
    /// From the Player's lineup entry; nil when no lineup lists them or XTTV gives none.
    var rcPoints: Int?
    var ageGroup: String?
    var isFavorite = false
}

enum PlayerDetailUiState: Equatable {
    case loading
    /// The Player cannot be resolved, or XTTV cannot be reached.
    case unavailable
    case content(PlayerDetailUi)
}
