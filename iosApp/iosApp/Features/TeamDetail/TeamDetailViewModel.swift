import Foundation
import Observation
import Shared

@MainActor
@Observable
final class TeamDetailViewModel {
    private(set) var uiState: TeamDetailUiState = .loading

    @ObservationIgnored private let eventStream = AsyncStream<TeamDetailEvent>.makeStream()
    var events: AsyncStream<TeamDetailEvent> { eventStream.stream }

    private let teamId: String
    private var loadTask: Task<Void, Never>?
    @ObservationIgnored private var favorites: Set<AnyHashable> = []

    private let loadDetail: (String) async throws -> DataResult
    @ObservationIgnored private let observeFavorites: any AsyncSequence<Set<AnyHashable>, Never>
    @ObservationIgnored private let addFavorite: (Favorite) async throws -> Void
    @ObservationIgnored private let removeFavorite: (Favorite) async throws -> Void

    init(
        teamId: String,
        loadDetail: @escaping (String) async throws -> DataResult = { try await KoinHelper.shared.loadTeamDetail(teamId: $0) },
        observeFavorites: any AsyncSequence<Set<AnyHashable>, Never> = KoinHelper.shared.getObserveFavoritesUseCase().invoke(),
        addFavorite: @escaping (Favorite) async throws -> Void = { try await KoinHelper.shared.getAddFavoriteUseCase().invoke(favorite: $0) },
        removeFavorite: @escaping (Favorite) async throws -> Void = { try await KoinHelper.shared.getRemoveFavoriteUseCase().invoke(favorite: $0) }
    ) {
        self.teamId = teamId
        self.loadDetail = loadDetail
        self.observeFavorites = observeFavorites
        self.addFavorite = addFavorite
        self.removeFavorite = removeFavorite
    }

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

    func onAction(_ action: TeamDetailAction) {
        switch action {
        case .retryClicked:
            loadTask?.cancel()
            loadTask = Task { await load() }
        case .favoriteClicked:
            toggleFavorite()
        case .leagueClicked(let leagueId):
            eventStream.continuation.yield(.openLeague(leagueId: leagueId, teamId: teamId))
        }
    }

    private func refreshFavorite() {
        guard case .content(var detail) = uiState else { return }
        let isFavorite = isFavoriteTeam
        guard detail.isFavorite != isFavorite else { return }
        detail.isFavorite = isFavorite
        uiState = .content(detail)
    }

    private var isFavoriteTeam: Bool {
        favorites.contains { ($0 as? FavoriteTeam)?.id == teamId }
    }

    private func toggleFavorite() {
        guard case .content(let detail) = uiState else { return }
        let favorite = FavoriteTeam(id: teamId)
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
            let result = try await loadDetail(teamId)
            guard !Task.isCancelled else { return }
            switch onEnum(of: result) {
            case .success(let success): uiState = (success.value as? TeamDetail).map { .content($0.toUi(isFavorite: isFavoriteTeam)) } ?? .error
            case .unavailable: uiState = .error
            }
        } catch {
            // Only cancellation can throw now, and the load that replaced this one owns the state.
        }
    }
}

private extension TeamDetail {
    func toUi(isFavorite: Bool) -> TeamDetailUi {
        TeamDetailUi(
            teamName: team.name,
            clubName: clubName,
            league: league.map {
                TeamLeagueUi(
                    id: $0.id,
                    name: $0.name,
                    standing: standing.map { StandingUi(position: Int($0.position), played: Int($0.played), points: Int($0.points)) },
                    nextFixture: nextFixture.toUi()
                )
            },
            lineup: lineup.map { LineupPlayerUi(id: $0.playerId, name: $0.name) },
            isFavorite: isFavorite
        )
    }
}
