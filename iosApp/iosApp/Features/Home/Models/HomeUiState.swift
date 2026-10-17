enum HomeSelectionUi: Equatable {
    case none
    case player(name: String, clubName: String, associationName: String)
    case club(name: String, associationName: String)
}

enum FavoriteKind: String {
    case player
    case club

    func key(for entityId: String) -> String { "\(rawValue)-\(entityId)" }
}

struct HomeFavoriteUi: Identifiable, Equatable {
    let kind: FavoriteKind
    let entityId: String
    let name: String
    let detail: String?

    var id: String { kind.key(for: entityId) }
}

/// Entries are marked selected when they are already a Favorite.
struct FavoritePickerUi: Equatable {
    let query: String
    let players: [DirectoryEntryUi]
    let clubs: [DirectoryEntryUi]
}

struct HomeUiState: Equatable {
    var selection: HomeSelectionUi = .none
    var favorites: [HomeFavoriteUi] = []
    var picker: FavoritePickerUi?
}
