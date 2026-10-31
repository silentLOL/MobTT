import Foundation

enum ClubSource: Hashable {
    /// The Club derived from My Player or My Club.
    case mine
    case specific(clubId: String)
}

struct StandingUi: Equatable {
    let position: Int
    let played: Int
    let points: Int
}

/// One competition's next Fixture of a Team; `leagueName` tells them apart when a Team plays in several.
struct TeamFixtureUi: Equatable {
    let leagueName: String?
    let nextFixture: NextFixtureUi
}

struct TeamUi: Identifiable, Equatable {
    /// The id of the League Team, which is the one that opens.
    let id: String
    let name: String
    let leagueName: String?
    let standing: StandingUi?
    let fixtures: [TeamFixtureUi]
}

struct ClubOverviewUi: Equatable {
    let clubName: String
    let associationName: String
    let teams: [TeamUi]
    /// Every Player of the Club, highest RC first.
    var players: [LineupPlayerUi] = []
    var isFavorite = false
}

enum ClubUiState: Equatable {
    case loading
    /// Only reachable from `ClubSource.mine`: the user has chosen neither My Player nor My Club.
    case noClub
    case error
    case content(ClubOverviewUi)
}
