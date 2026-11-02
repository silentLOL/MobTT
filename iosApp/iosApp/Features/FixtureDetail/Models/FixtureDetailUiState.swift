import Foundation

struct MatchRowUi: Identifiable, Equatable {
    let id: Int
    let number: Int
    let isDoubles: Bool
    let homePlayers: String
    let awayPlayers: String
    /// Sets won; nil while the Match has no result.
    let score: FixtureScoreUi?
}

struct FixtureDetailUi: Equatable {
    let homeTeamId: String
    let homeTeamName: String
    let awayTeamId: String
    let awayTeamName: String
    let startsAt: Date
    let venue: String?
    /// Street, postal code and city, as far as XTTV gives them.
    let address: String?
    /// Matches won; nil until the Fixture is played.
    let score: FixtureScoreUi?
    let matches: [MatchRowUi]
}

enum FixtureDetailUiState: Equatable {
    case loading
    case error
    case content(FixtureDetailUi)
}
