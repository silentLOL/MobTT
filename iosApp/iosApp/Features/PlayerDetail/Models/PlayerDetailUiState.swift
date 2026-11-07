import Foundation

struct PlayerTeamUi: Identifiable, Equatable {
    let id: String
    let name: String
}

struct PlayerClubUi: Equatable {
    let clubName: String
    /// Nil when XTTV no longer resolves the Association.
    let associationName: String?
}

struct PlayerTeamsUi: Equatable {
    /// The Teams of the Club whose lineup lists the Player; empty when none does.
    var teams: [PlayerTeamUi] = []
    /// From the Player's lineup entry; nil when no lineup lists them or XTTV gives none.
    var rcPoints: Int?
    var ageGroup: String?
}

/// The Player is known, so the screen draws at once. `club` and `teams` load on their own; a loaded `club` is nil when XTTV no longer resolves the Club. The stat row rides on `teams`.
struct PlayerDetailUi: Equatable {
    let playerName: String
    let clubId: String
    var isFavorite = false
    var club = SectionUiState<PlayerClubUi?>.loading
    var teams = SectionUiState<PlayerTeamsUi>.loading
}

enum PlayerDetailUiState: Equatable {
    case loading
    /// The Player cannot be resolved, or XTTV cannot be reached.
    case unavailable
    case content(PlayerDetailUi)
}
