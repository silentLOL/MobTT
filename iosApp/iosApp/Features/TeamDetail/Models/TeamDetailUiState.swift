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
    /// The Player's RC value; nil when XTTV gives none.
    var rcPoints: Int?
}

/// The League at a glance; opens the League with its full table and Fixtures.
struct TeamLeagueUi: Equatable {
    let id: String
    let name: String
    /// Nil when XTTV has no table row for the Team.
    let standing: StandingUi?
    let nextFixture: NextFixtureUi
}

struct TeamDetailUi: Equatable {
    let teamName: String
    let clubName: String
    /// Nil when the Team has no League.
    let league: TeamLeagueUi?
    let lineup: [LineupPlayerUi]
    var isFavorite = false
}

enum TeamDetailUiState: Equatable {
    case loading
    case error
    case content(TeamDetailUi)
}
