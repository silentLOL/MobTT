import Foundation

enum PlayersSheetKind: Equatable {
    case association
    case club
}

/// What a picker sheet lists: the entries already filtered by the sheet's query.
enum PlayersSheetContent: Equatable {
    case loading
    case error
    case entries([DirectoryEntryUi])
}

struct PlayersSheetUi: Equatable {
    let kind: PlayersSheetKind
    let query: String
    let content: PlayersSheetContent
}

struct PlayerRowUi: Identifiable, Equatable {
    let id: String
    let name: String
    let rcPoints: Int?
}

/// The Players of the chosen Club, filtered by the search text.
enum PlayersListUi: Equatable {
    case noClub
    case loading
    case error
    case players([PlayerRowUi])
}

struct PlayersUiState: Equatable {
    var associationName: String?
    var clubName: String?
    var query = ""
    var list: PlayersListUi = .noClub
    var sheet: PlayersSheetUi?

    var isClubEnabled: Bool { associationName != nil }
}
