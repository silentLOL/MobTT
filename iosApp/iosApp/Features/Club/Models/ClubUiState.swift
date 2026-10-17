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

enum NextFixtureUi: Equatable {
    case scheduled(opponentName: String, startsAt: Date, venue: String?, isHome: Bool)
    case freeWeek
}

struct TeamUi: Identifiable, Equatable {
    let id: String
    let name: String
    let leagueName: String?
    let standing: StandingUi?
    let nextFixture: NextFixtureUi
}

struct ClubOverviewUi: Equatable {
    let clubName: String
    let associationName: String
    let teams: [TeamUi]
}

enum ClubUiState: Equatable {
    case loading
    /// Only reachable from `ClubSource.mine`: the user has chosen neither My Player nor My Club.
    case noClub
    case error
    case content(ClubOverviewUi)
}
