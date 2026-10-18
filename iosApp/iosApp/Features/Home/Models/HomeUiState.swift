enum HomeSelectionUi: Equatable {
    case none
    /// XTTV could not be reached to resolve the choice, so neither the prompt nor a name is shown.
    case unavailable
    case player(name: String, clubName: String, associationName: String)
    case club(name: String, associationName: String)
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

struct HomeFixtureUi: Identifiable, Equatable {
    let teamId: String
    let teamName: String
    let nextFixture: NextFixtureUi

    var id: String { teamId }
}

enum HomeFixturesUi: Equatable {
    /// Loading, no selection, or a Club without Teams: the section is not drawn.
    case hidden
    case unavailable
    case loaded([HomeFixtureUi])
}

struct HomeUiState: Equatable {
    var selection: HomeSelectionUi = .none
    var favorites: [HomeFavoriteUi] = []
    var picker: FavoritePickerUi?
    var fixtures: HomeFixturesUi = .hidden
}
