import Foundation

struct FixtureScoreUi: Equatable {
    let home: Int
    let away: Int
}

struct FixtureRowUi: Identifiable, Equatable {
    let id: String
    let homeTeamId: String
    let homeTeamName: String
    let awayTeamId: String
    let awayTeamName: String
    let startsAt: Date
    let venue: String?
    /// Nil until the Fixture is finished.
    let score: FixtureScoreUi?
    /// True when the Team the user came from plays in this Fixture.
    var isThisTeam = false
}

struct LeagueDetailUi: Equatable {
    let leagueName: String
    let standings: [StandingRowUi]
    let fixtures: [FixtureRowUi]
    /// The Fixtures can only be narrowed to a Team when the user came from one.
    var canFilterToTeam = false
    /// The Team the user came from, if any; its name does not open itself.
    var highlightTeamId: String?
    var onlyTeamFixtures = false

    var visibleFixtures: [FixtureRowUi] {
        onlyTeamFixtures ? fixtures.filter(\.isThisTeam) : fixtures
    }
}

enum LeagueDetailUiState: Equatable {
    case loading
    case error
    case content(LeagueDetailUi)
}
