import Foundation

struct FixtureScoreUi: Equatable {
    let home: Int
    let away: Int
}

struct FixtureRowUi: Identifiable, Equatable {
    let id: String
    let homeTeamName: String
    let awayTeamName: String
    let startsAt: Date
    let venue: String?
    /// Nil until the Fixture is finished.
    let score: FixtureScoreUi?
}

struct LeagueDetailUi: Equatable {
    let leagueName: String
    let standings: [StandingRowUi]
    let fixtures: [FixtureRowUi]
}

enum LeagueDetailUiState: Equatable {
    case loading
    case error
    case content(LeagueDetailUi)
}
