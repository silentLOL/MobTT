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
    /// While the Rating history's explanation is open.
    var ratingInfoShown = false
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

/// How much of the Rating history the chart shows.
enum RatingHistorySpan: CaseIterable {
    case lastYear
    case all
}

/// The Rating history section. It starts open, and `history` loads the first time it is open. `since` is the first day (midnight UTC) the chosen `span` covers, nil for the whole history.
struct RatingHistorySectionUi: Equatable {
    var isExpanded = true
    var history = SectionUiState<RatingHistoryUi>.loading
    var span = RatingHistorySpan.lastYear
    var since: Date?

    /// The loaded Rating updates within `span`, oldest first.
    var visibleUpdates: [RatingUpdateUi] {
        guard case .loaded(let loaded) = history else { return [] }
        guard let since else { return loaded.updates }
        return loaded.updates.filter { $0.date >= since }
    }

    /// The Player has Rating updates, just none in the chosen span.
    var hasNoRecentUpdate: Bool {
        guard case .loaded(let loaded) = history else { return false }
        return !loaded.updates.isEmpty && visibleUpdates.isEmpty
    }

    /// A dot per Rating update only while there are few enough to tell apart.
    var showsDots: Bool { visibleUpdates.count <= batChartMaxDots }
}

enum PlayerDetailUiState: Equatable {
    case loading
    /// The Player cannot be resolved, or XTTV cannot be reached.
    case unavailable
    case content(PlayerDetailUi)
}
