import Observation
import Shared

@MainActor
@Observable
final class HomeViewModel {
    private var selection: HomeSelectionUi = .none
    private var hasSelection = false
    private var hasFavorites = false
    private var favorites: [HomeFavoriteUi] = []
    private var favoriteKeys: Set<String> = []
    private var pickerQuery: String?
    private var browsingPlayers = false
    private let playerDrillDown: PlayerDrillDown
    private var pickerClubs: [Club] = []
    private var searchTask: Task<Void, Never>?

    @ObservationIgnored private let observeUserContext: any AsyncSequence<DataResult, Never>
    @ObservationIgnored private let observeHomeFavorites: any AsyncSequence<DataResult, Never>
    @ObservationIgnored private let observeFavorites: any AsyncSequence<Set<AnyHashable>, Never>
    @ObservationIgnored private let addFavorite: (Favorite) async throws -> Void
    @ObservationIgnored private let removeFavorite: (Favorite) async throws -> Void
    @ObservationIgnored private let searchClubs: (String) async -> [Club]
    @ObservationIgnored private let eventStream = AsyncStream<HomeEvent>.makeStream()

    init(
        observeUserContext: any AsyncSequence<DataResult, Never> = KoinHelper.shared.observeUserContext(),
        observeHomeFavorites: any AsyncSequence<DataResult, Never> = KoinHelper.shared.observeHomeFavorites(),
        observeFavorites: any AsyncSequence<Set<AnyHashable>, Never> = KoinHelper.shared.getObserveFavoritesUseCase().invoke(),
        addFavorite: @escaping (Favorite) async throws -> Void = { try await KoinHelper.shared.getAddFavoriteUseCase().invoke(favorite: $0) },
        removeFavorite: @escaping (Favorite) async throws -> Void = { try await KoinHelper.shared.getRemoveFavoriteUseCase().invoke(favorite: $0) },
        playerLoaders: PlayerDrillDownLoaders = .live,
        searchClubs: @escaping (String) async -> [Club] = { (try? await KoinHelper.shared.getSearchClubsUseCase().invoke(query: $0)) ?? [] }
    ) {
        self.observeUserContext = observeUserContext
        self.observeHomeFavorites = observeHomeFavorites
        self.observeFavorites = observeFavorites
        self.addFavorite = addFavorite
        self.removeFavorite = removeFavorite
        self.playerDrillDown = PlayerDrillDown(loaders: playerLoaders)
        self.searchClubs = searchClubs
    }

    var events: AsyncStream<HomeEvent> { eventStream.stream }

    var uiState: HomeUiState {
        HomeUiState(isLoading: !(hasSelection && hasFavorites), selection: selection, favorites: favorites, picker: pickerQuery.map(pickerUi))
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
        case .heroPlayerClicked(let id):
            eventStream.continuation.yield(.openPlayer(playerId: id))
        case .heroClubClicked(let id):
            eventStream.continuation.yield(.openClub(clubId: id))
        case .favoriteClubClicked(let id):
            eventStream.continuation.yield(.openClub(clubId: id))
        case .favoriteTeamClicked(let id):
            eventStream.continuation.yield(.openTeam(teamId: id))
        case .favoritePlayerClicked(let id):
            eventStream.continuation.yield(.openPlayer(playerId: id))
        case .addFavoriteClicked:
            browsingPlayers = false
            pickerClubs = []
            updatePickerQuery("")
        case .browsePlayersClicked:
            browsingPlayers = true
            playerDrillDown.start()
        case .pickerDrillDownEntryClicked(let id):
            if case .player(let playerId) = playerDrillDown.select(id) {
                toggle(kind: .player, entityId: playerId)
            }
        case .pickerDrillDownBackClicked:
            if !playerDrillDown.back() {
                playerDrillDown.cancel()
                browsingPlayers = false
            }
        case .pickerQueryChanged(let query):
            if browsingPlayers {
                playerDrillDown.setQuery(query)
            } else {
                updatePickerQuery(query)
            }
        case .pickerClubClicked(let id):
            toggle(kind: .club, entityId: id)
        case .pickerDismissed:
            searchTask?.cancel()
            playerDrillDown.cancel()
            browsingPlayers = false
            pickerQuery = nil
        }
    }

    private func observeSelection() async {
        for await result in observeUserContext {
            switch onEnum(of: result) {
            case .success(let success): selection = (success.value as? UserContext)?.toSelectionUi() ?? .none
            case .unavailable: selection = .unavailable
            }
            hasSelection = true
        }
    }

    private func observeResolved() async {
        for await result in observeHomeFavorites {
            switch onEnum(of: result) {
            case .success(let success): favorites = (success.value as? [ResolvedFavorite])?.map { $0.toUi() } ?? []
            case .unavailable: favorites = []
            }
            hasFavorites = true
        }
    }

    private func observeKeys() async {
        for await set in observeFavorites {
            favoriteKeys = Set(set.compactMap { ($0 as? Favorite)?.key })
        }
    }

    private func toggle(kind: FavoriteKind, entityId: String) {
        let isFavorite = favoriteKeys.contains(kind.key(for: entityId))
        let favorite = kind.toDomain(entityId)
        Task {
            if isFavorite {
                try? await removeFavorite(favorite)
            } else {
                try? await addFavorite(favorite)
            }
        }
    }

    private func updatePickerQuery(_ query: String) {
        pickerQuery = query
        searchTask?.cancel()
        searchTask = Task {
            let clubs = await searchClubs(query)
            guard !Task.isCancelled else { return }
            // Results for an earlier query are kept until the new ones arrive, so the list does not flicker empty while typing.
            pickerClubs = clubs
        }
    }

    private func pickerUi(for query: String) -> FavoritePickerUi {
        FavoritePickerUi(
            query: query,
            clubs: pickerClubs.map {
                DirectoryEntryUi(
                    id: $0.id,
                    title: $0.name,
                    subtitle: nil,
                    selected: favoriteKeys.contains(FavoriteKind.club.key(for: $0.id))
                )
            },
            playerDrillDown: browsingPlayers ? playerDrillDown.ui { favoriteKeys.contains(FavoriteKind.player.key(for: $0)) } : nil
        )
    }
}

private extension FavoriteKind {
    func toDomain(_ entityId: String) -> Favorite {
        switch self {
        case .player: FavoritePlayer(id: entityId)
        case .club: FavoriteClub(id: entityId)
        case .team: FavoriteTeam(id: entityId)
        }
    }
}

private extension Favorite {
    var key: String {
        switch onEnum(of: self) {
        case .player(let favorite): FavoriteKind.player.key(for: favorite.id)
        case .club(let favorite): FavoriteKind.club.key(for: favorite.id)
        case .team(let favorite): FavoriteKind.team.key(for: favorite.id)
        }
    }
}

private extension ResolvedFavorite {
    func toUi() -> HomeFavoriteUi {
        switch onEnum(of: favorite) {
        case .player(let player): HomeFavoriteUi(kind: .player, entityId: player.id, name: name, detail: detail)
        case .club(let club): HomeFavoriteUi(kind: .club, entityId: club.id, name: name, detail: detail)
        case .team(let team): HomeFavoriteUi(kind: .team, entityId: team.id, name: name, detail: detail)
        }
    }
}

private extension UserContext {
    func toSelectionUi() -> HomeSelectionUi {
        switch onEnum(of: self) {
        case .none: .none
        case .myPlayer(let context):
            .player(name: context.player.name, clubName: context.club.name, associationName: context.association.code, playerId: context.player.id)
        case .myClub(let context):
            .club(name: context.club.name, associationName: context.association.code, clubId: context.club.id)
        }
    }
}
