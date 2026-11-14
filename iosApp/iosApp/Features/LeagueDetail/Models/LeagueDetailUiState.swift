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

struct LeagueDetailUi: Equatable {
    let leagueName: String
    let standings: [StandingRowUi]
    /// Fixtures and ranking start collapsed and load the first time they are opened.
    var fixtures = SectionUiState<[FixtureRowUi]>.loading
    var fixturesExpanded = false
    /// The Fixtures can only be narrowed to a Team when one is highlighted.
    var canFilterToTeam = false
    /// A Team highlighted without coming from its own screen is one of My Teams: only the League tab opens a League that way. It is then called "my Team" rather than "this Team".
    var highlightIsMyTeam = false
    var onlyTeamFixtures = false
    var ranking = SectionUiState<[PlayerRankingRowUi]>.loading
    var rankingExpanded = false
    /// Marks My Player in the ranking.
    var myPlayerId: String?

    func isMyPlayer(_ playerId: String) -> Bool { playerId == myPlayerId }

    /// Empty until the Fixtures are loaded.
    var visibleFixtures: [FixtureRowUi] {
        guard case .loaded(let loaded) = fixtures else { return [] }
        return onlyTeamFixtures ? loaded.filter(\.isThisTeam) : loaded
    }
}

enum LeagueDetailUiState: Equatable {
    case loading
    case error
    case content(LeagueDetailUi)
}
