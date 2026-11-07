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
    /// True when the highlighted Team plays in this Fixture.
    var isThisTeam = false
}

/// One row of the League's Player ranking. `rank` is nil for a Player below the League's minimum number of games.
struct PlayerRankingRowUi: Identifiable, Equatable {
    /// The Player and Team together: a Player listed for two Teams of the League has a row for each.
    var id: String { "\(playerId)/\(teamId)" }
    let playerId: String
    let teamId: String
    let name: String
    let rank: Int?
    /// Nil when the Team is missing from the League's standings.
    let teamName: String?
    let matchesWon: Int
    let matchesLost: Int
    let gamesPlayed: Int
}

/// The League's Player ranking, loaded on its own so the table and the Fixtures never wait for it.
enum PlayerRankingUiState: Equatable {
    case loading
    case unavailable
    case loaded([PlayerRankingRowUi])
}

struct LeagueDetailUi: Equatable {
    let leagueName: String
    let standings: [StandingRowUi]
    let fixtures: [FixtureRowUi]
    /// The Fixtures can only be narrowed to a Team when one is highlighted.
    var canFilterToTeam = false
    var onlyTeamFixtures = false
    var ranking = PlayerRankingUiState.loading
    /// Marks My Player in the ranking.
    var myPlayerId: String?

    func isMyPlayer(_ playerId: String) -> Bool { playerId == myPlayerId }

    var visibleFixtures: [FixtureRowUi] {
        onlyTeamFixtures ? fixtures.filter(\.isThisTeam) : fixtures
    }
}

enum LeagueDetailUiState: Equatable {
    case loading
    case error
    case content(LeagueDetailUi)
}
