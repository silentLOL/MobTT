import Observation
import Shared

@MainActor
@Observable
final class HomeViewModel {
    private var selection: HomeSelectionUi = .none
    private var favorites: [HomeFavoriteUi] = []
    private var favoriteKeys: Set<String> = []
    private var pickerQuery: String?
    private var pickerPlayers: [PlayerSearchResult] = []
    private var pickerClubs: [Club] = []
    private var searchTask: Task<Void, Never>?

    @ObservationIgnored private let observeUserContext = KoinHelper.shared.getObserveUserContextUseCase()
    @ObservationIgnored private let observeResolvedFavorites = KoinHelper.shared.getObserveResolvedFavoritesUseCase()
    @ObservationIgnored private let observeFavorites = KoinHelper.shared.getObserveFavoritesUseCase()
    @ObservationIgnored private let addFavorite = KoinHelper.shared.getAddFavoriteUseCase()
    @ObservationIgnored private let removeFavorite = KoinHelper.shared.getRemoveFavoriteUseCase()
    @ObservationIgnored private let searchPlayers = KoinHelper.shared.getSearchPlayersUseCase()
    @ObservationIgnored private let searchClubs = KoinHelper.shared.getSearchClubsUseCase()
    @ObservationIgnored private let eventStream = AsyncStream<HomeEvent>.makeStream()

    var events: AsyncStream<HomeEvent> { eventStream.stream }

    var uiState: HomeUiState {
        HomeUiState(selection: selection, favorites: favorites, picker: pickerQuery.map(pickerUi))
    }

    /// Runs until the calling task is cancelled.
    func observe() async {
        await withTaskGroup(of: Void.self) { group in
            group.addTask { @MainActor in await self.observeSelection() }
            group.addTask { @MainActor in await self.observeResolved() }
            group.addTask { @MainActor in await self.observeKeys() }
        }
    }

    func onAction(_ action: HomeAction) {
        switch action {
        case .chooseClicked:
            eventStream.continuation.yield(.openSettings)
        case .addFavoriteClicked:
            updatePickerQuery("")
        case .removeFavoriteClicked(let favorite):
            Task { try? await removeFavorite.invoke(favorite: favorite.toDomain()) }
        case .pickerQueryChanged(let query):
            updatePickerQuery(query)
        case .pickerPlayerClicked(let id):
            toggle(HomeFavoriteUi(kind: .player, entityId: id, name: "", detail: nil))
        case .pickerClubClicked(let id):
            toggle(HomeFavoriteUi(kind: .club, entityId: id, name: "", detail: nil))
        case .pickerDismissed:
            searchTask?.cancel()
            pickerQuery = nil
        }
    }

    private func observeSelection() async {
        for await context in observeUserContext.invoke() {
            selection = context.toSelectionUi()
        }
    }

    private func observeResolved() async {
        for await resolved in observeResolvedFavorites.invoke() {
            favorites = resolved.compactMap { $0.toUi() }
        }
    }

    private func observeKeys() async {
        for await set in observeFavorites.invoke() {
            favoriteKeys = Set(set.compactMap { ($0 as? Favorite)?.key })
        }
    }

    private func toggle(_ favorite: HomeFavoriteUi) {
        let isFavorite = favoriteKeys.contains(favorite.id)
        Task {
            if isFavorite {
                try? await removeFavorite.invoke(favorite: favorite.toDomain())
            } else {
                try? await addFavorite.invoke(favorite: favorite.toDomain())
            }
        }
    }

    private func updatePickerQuery(_ query: String) {
        pickerQuery = query
        searchTask?.cancel()
        searchTask = Task {
            let players = (try? await searchPlayers.invoke(query: query)) ?? []
            let clubs = (try? await searchClubs.invoke(query: query)) ?? []
            guard !Task.isCancelled else { return }
            // Results for an earlier query are kept until the new ones arrive, so the list does not flicker empty while typing.
            pickerPlayers = players
            pickerClubs = clubs
        }
    }

    private func pickerUi(for query: String) -> FavoritePickerUi {
        FavoritePickerUi(
            query: query,
            players: pickerPlayers.map {
                DirectoryEntryUi(
                    id: $0.player.id,
                    title: $0.player.name,
                    subtitle: $0.clubName,
                    selected: favoriteKeys.contains("\(FavoriteKind.player.rawValue)-\($0.player.id)")
                )
            },
            clubs: pickerClubs.map {
                DirectoryEntryUi(
                    id: $0.id,
                    title: $0.name,
                    subtitle: nil,
                    selected: favoriteKeys.contains("\(FavoriteKind.club.rawValue)-\($0.id)")
                )
            }
        )
    }
}

private extension HomeFavoriteUi {
    func toDomain() -> Favorite {
        switch kind {
        case .player: FavoritePlayer(id: entityId)
        case .club: FavoriteClub(id: entityId)
        }
    }
}

private extension Favorite {
    var key: String? {
        switch onEnum(of: self) {
        case .player(let favorite): "\(FavoriteKind.player.rawValue)-\(favorite.id)"
        case .club(let favorite): "\(FavoriteKind.club.rawValue)-\(favorite.id)"
        case .team: nil
        }
    }
}

private extension ResolvedFavorite {
    func toUi() -> HomeFavoriteUi? {
        switch onEnum(of: favorite) {
        case .player(let player): HomeFavoriteUi(kind: .player, entityId: player.id, name: name, detail: detail)
        case .club(let club): HomeFavoriteUi(kind: .club, entityId: club.id, name: name, detail: detail)
        case .team: nil
        }
    }
}

private extension UserContext {
    func toSelectionUi() -> HomeSelectionUi {
        switch onEnum(of: self) {
        case .none: .none
        case .myPlayer(let context):
            .player(name: context.player.name, clubName: context.club.name, associationName: context.association.name)
        case .myClub(let context):
            .club(name: context.club.name, associationName: context.association.name)
        }
    }
}
