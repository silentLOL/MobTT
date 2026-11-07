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
    /// Nil while hidden: until the Teams name the Player's Ratings Central entry, for good without one, and in builds that may not show Rating histories.
    var ratingHistory: RatingHistorySectionUi?
}

/// The Rating after one Rating update, so Ratings Central's unrated default before the first one never appears.
struct RatingUpdateUi: Equatable {
    let date: Date
    let name: String
    let points: Int
    let deviation: Int
    let pointChange: Int
}

/// A Player's Rating updates, oldest first.
struct RatingHistoryUi: Equatable {
    let updates: [RatingUpdateUi]
}

/// The collapsed Rating history section. `history` loads the first time the section is opened.
struct RatingHistorySectionUi: Equatable {
    var isExpanded = false
    var history = SectionUiState<RatingHistoryUi>.loading
}

enum PlayerDetailUiState: Equatable {
    case loading
    /// The Player cannot be resolved, or XTTV cannot be reached.
    case unavailable
    case content(PlayerDetailUi)
}
