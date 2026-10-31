import Foundation
import Observation
import Shared

@MainActor
@Observable
final class PlayerDetailViewModel {
    private(set) var uiState: PlayerDetailUiState = .loading

    private let playerId: String
    private var loadTask: Task<Void, Never>?
    @ObservationIgnored private var favorites: Set<AnyHashable> = []

    @ObservationIgnored private let eventStream = AsyncStream<PlayerDetailEvent>.makeStream()
    private let loadDetail: (String) async throws -> DataResult
    @ObservationIgnored private let observeFavorites: any AsyncSequence<Set<AnyHashable>, Never>
    @ObservationIgnored private let addFavorite: (Favorite) async throws -> Void
    @ObservationIgnored private let removeFavorite: (Favorite) async throws -> Void

    init(
        playerId: String,
        loadDetail: @escaping (String) async throws -> DataResult = { try await KoinHelper.shared.loadPlayerDetail(playerId: $0) },
        observeFavorites: any AsyncSequence<Set<AnyHashable>, Never> = KoinHelper.shared.getObserveFavoritesUseCase().invoke(),
        addFavorite: @escaping (Favorite) async throws -> Void = { try await KoinHelper.shared.getAddFavoriteUseCase().invoke(favorite: $0) },
        removeFavorite: @escaping (Favorite) async throws -> Void = { try await KoinHelper.shared.getRemoveFavoriteUseCase().invoke(favorite: $0) }
    ) {
        self.playerId = playerId
        self.loadDetail = loadDetail
        self.observeFavorites = observeFavorites
        self.addFavorite = addFavorite
        self.removeFavorite = removeFavorite
    }

    var events: AsyncStream<PlayerDetailEvent> { eventStream.stream }

    /// Follows the Favorites until the calling task is cancelled. The load itself runs from `loadIfNeeded()`.
    func observe() async {
        for await set in observeFavorites {
            favorites = set
            refreshFavorite()
        }
    }

    /// Starts the first load once. It runs outside the view's task, so leaving and returning to the screen neither reloads nor strands it on `.loading`.
    func loadIfNeeded() {
        guard loadTask == nil else { return }
        loadTask = Task { await load() }
    }

    func onAction(_ action: PlayerDetailAction) {
        switch action {
        case .favoriteClicked:
            toggleFavorite()
        case .clubClicked:
            guard case .content(let detail) = uiState else { return }
            eventStream.continuation.yield(.openClub(clubId: detail.clubId))
        case .teamClicked(let id):
            eventStream.continuation.yield(.openTeam(teamId: id))
        }
    }

    private func refreshFavorite() {
        guard case .content(var detail) = uiState else { return }
        let isFavorite = isFavoritePlayer
        guard detail.isFavorite != isFavorite else { return }
        detail.isFavorite = isFavorite
        uiState = .content(detail)
    }

    private var isFavoritePlayer: Bool {
        favorites.contains { ($0 as? FavoritePlayer)?.id == playerId }
    }

    private func toggleFavorite() {
        guard case .content(let detail) = uiState else { return }
        let favorite = FavoritePlayer(id: playerId)
        Task {
            if detail.isFavorite {
                try? await removeFavorite(favorite)
            } else {
                try? await addFavorite(favorite)
            }
        }
    }

    private func load() async {
        uiState = .loading
        do {
            let result = try await loadDetail(playerId)
            guard !Task.isCancelled else { return }
            switch onEnum(of: result) {
            case .success(let success): uiState = (success.value as? PlayerDetail).map { .content($0.toUi(isFavorite: isFavoritePlayer)) } ?? .unavailable
            case .unavailable: uiState = .unavailable
            }
        } catch {
            // Only cancellation can throw now, and the load that replaced this one owns the state.
        }
    }
}

private extension PlayerDetail {
    func toUi(isFavorite: Bool) -> PlayerDetailUi {
        PlayerDetailUi(
            playerName: player.name,
            clubId: player.clubId,
            clubName: clubName,
            associationName: associationCode,
            teams: teams.map { PlayerTeamUi(id: $0.id, name: $0.name) },
            rcPoints: rcPoints?.intValue,
            ageGroup: ageGroup,
            isFavorite: isFavorite
        )
    }
}
