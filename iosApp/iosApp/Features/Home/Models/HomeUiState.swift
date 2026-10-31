enum HomeSelectionUi: Equatable {
    case none
    /// XTTV could not be reached to resolve the choice, so neither the prompt nor a name is shown.
    case unavailable
    case player(name: String, clubName: String, associationName: String, playerId: String)
    case club(name: String, associationName: String, clubId: String)
}

extension HomeSelectionUi {
    var isChosen: Bool {
        switch self {
        case .player, .club: true
        case .none, .unavailable: false
        }
    }
}

enum FavoriteKind: String {
    case player
    case club
    case team

    func key(for entityId: String) -> String { "\(rawValue)-\(entityId)" }
}

struct HomeFavoriteUi: Identifiable, Equatable {
    let kind: FavoriteKind
    let entityId: String
    let name: String
    let detail: String?

    var id: String { kind.key(for: entityId) }
}

/// Entries are marked selected when they are already a Favorite. Clubs are searched; Players are browsed Verband,
/// League, Player, and while [playerDrillDown] is set it replaces the search.
struct FavoritePickerUi: Equatable {
    let query: String
    let clubs: [DirectoryEntryUi]
    var playerDrillDown: PlayerDrillDownUi?
}

struct HomeUiState: Equatable {
    /// True until the Selection and Favorites have loaded for the first time.
    var isLoading = false
    var selection: HomeSelectionUi = .none
    var favorites: [HomeFavoriteUi] = []
    var picker: FavoritePickerUi?
}
