import Observation
import Shared

struct PlayerDrillDownLoaders {
    let associations: () async -> [Association]
    let leagues: (_ associationId: String) async -> [League]
    let players: (_ leagueId: String) async -> [PlayerSearchResult]

    /// A failed read is an empty list, as for the other pickers.
    static let live = PlayerDrillDownLoaders(
        associations: { (try? await KoinHelper.shared.getListAssociationsUseCase().invoke()) ?? [] },
        leagues: { (try? await KoinHelper.shared.getListLeaguesUseCase().invoke(associationId: $0)) ?? [] },
        players: { (try? await KoinHelper.shared.getListLeaguePlayersUseCase().invoke(leagueId: $0)) ?? [] }
    )
}

/// What tapping an entry did: went a step deeper, or picked the Player with this id.
enum PlayerDrillDownSelection: Equatable {
    case descended
    case player(id: String)
    case ignored
}

/// XTTV cannot search Players by name, so they are found Verband, then League, then Player. Each step's list is
/// read once, when the step is entered; typing only filters it on the device.
@MainActor
@Observable
final class PlayerDrillDown {
    private struct Choice {
        let id: String
        let name: String
    }

    private var association: Choice?
    private var league: Choice?
    private var loaded: [DirectoryEntryUi] = []
    private(set) var query = ""
    @ObservationIgnored private var loadTask: Task<Void, Never>?
    @ObservationIgnored private let loaders: PlayerDrillDownLoaders

    init(loaders: PlayerDrillDownLoaders) {
        self.loaders = loaders
    }

    var step: PlayerDrillDownStep {
        if league != nil { .player } else if association != nil { .league } else { .association }
    }

    /// [isSelected] marks the Player that is already chosen; Verbände and Leagues are never selected.
    func ui(isSelected: (String) -> Bool = { _ in false }) -> PlayerDrillDownUi {
        let matching = loaded.filter { query.isEmpty || $0.title.localizedCaseInsensitiveContains(query) }
        let entries = step == .player
            ? matching.map { DirectoryEntryUi(id: $0.id, title: $0.title, subtitle: $0.subtitle, selected: isSelected($0.id)) }
            : matching
        return PlayerDrillDownUi(step: step, contextName: (league ?? association)?.name, query: query, entries: entries)
    }

    func start() {
        association = nil
        league = nil
        enterStep()
    }

    func setQuery(_ query: String) {
        self.query = query
    }

    func select(_ id: String) -> PlayerDrillDownSelection {
        guard let entry = loaded.first(where: { $0.id == id }) else { return .ignored }
        switch step {
        case .association:
            association = Choice(id: entry.id, name: entry.title)
        case .league:
            league = Choice(id: entry.id, name: entry.title)
        case .player:
            return .player(id: entry.id)
        }
        enterStep()
        return .descended
    }

    /// Goes one step back; false when already at the first step, so the caller closes the picker.
    func back() -> Bool {
        switch step {
        case .association:
            return false
        case .league:
            association = nil
        case .player:
            league = nil
        }
        enterStep()
        return true
    }

    func cancel() {
        loadTask?.cancel()
    }

    private func enterStep() {
        loadTask?.cancel()
        query = ""
        loaded = []
        let loaders = loaders
        let step = step
        let association = association
        let league = league
        loadTask = Task {
            let entries: [DirectoryEntryUi]
            switch step {
            case .association:
                entries = await loaders.associations().map { DirectoryEntryUi(id: $0.id, title: $0.name, subtitle: nil, selected: false) }
            case .league:
                entries = await loaders.leagues(association?.id ?? "").map { DirectoryEntryUi(id: $0.id, title: $0.name, subtitle: nil, selected: false) }
            case .player:
                entries = await loaders.players(league?.id ?? "").map {
                    DirectoryEntryUi(id: $0.player.id, title: $0.player.name, subtitle: $0.clubName, selected: false)
                }
            }
            guard !Task.isCancelled else { return }
            loaded = entries
        }
    }
}
