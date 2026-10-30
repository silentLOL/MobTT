import Foundation

struct StandingRowUi: Identifiable, Equatable {
    let id: String
    let position: Int
    let teamName: String
    let played: Int
    let points: Int
    let isThisTeam: Bool
}

struct LineupPlayerUi: Identifiable, Equatable {
    let id: String
    let name: String
}

struct TeamDetailUi: Equatable {
    let teamName: String
    let clubName: String
    /// Nil when the Team has no League.
    let leagueName: String?
    let standings: [StandingRowUi]
    let lineup: [LineupPlayerUi]
    var isFavorite = false
}

enum TeamDetailUiState: Equatable {
    case loading
    case error
    case content(TeamDetailUi)
}
