import Foundation
import Observation
import Shared

@MainActor
@Observable
final class ClubViewModel {
    private(set) var uiState: ClubUiState = .loading

    private let source: ClubSource
    private var clubId: String?
    private var loadTask: Task<Void, Never>?
    @ObservationIgnored private var favorites: Set<AnyHashable> = []
    /// Bumped by a retry; the route restarts `observe()` when it changes.
    private(set) var retryCount = 0

    private let loadOverview: (String) async throws -> DataResult

    @ObservationIgnored private let observeUserContext: any AsyncSequence<DataResult, Never>
    @ObservationIgnored private let observeFavorites: any AsyncSequence<Set<AnyHashable>, Never>
    @ObservationIgnored private let addFavorite: (Favorite) async throws -> Void
    @ObservationIgnored private let removeFavorite: (Favorite) async throws -> Void
    @ObservationIgnored private let rememberPlayer: (Player) async throws -> Void
    @ObservationIgnored private let eventStream = AsyncStream<ClubEvent>.makeStream()

    var events: AsyncStream<ClubEvent> { eventStream.stream }

    init(
        source: ClubSource,
        observeUserContext: any AsyncSequence<DataResult, Never> = KoinHelper.shared.observeUserContext(),
        loadOverview: @escaping (String) async throws -> DataResult = { try await KoinHelper.shared.loadClubOverview(clubId: $0) },
        observeFavorites: any AsyncSequence<Set<AnyHashable>, Never> = KoinHelper.shared.getObserveFavoritesUseCase().invoke(),
        addFavorite: @escaping (Favorite) async throws -> Void = { try await KoinHelper.shared.getAddFavoriteUseCase().invoke(favorite: $0) },
        removeFavorite: @escaping (Favorite) async throws -> Void = { try await KoinHelper.shared.getRemoveFavoriteUseCase().invoke(favorite: $0) },
        rememberPlayer: @escaping (Player) async throws -> Void = { try await KoinHelper.shared.getRememberPlayerUseCase().invoke(player: $0) }
    ) {
        self.source = source
        self.observeUserContext = observeUserContext
        self.loadOverview = loadOverview
        self.observeFavorites = observeFavorites
        self.addFavorite = addFavorite
        self.removeFavorite = removeFavorite
        self.rememberPlayer = rememberPlayer
    }

    /// Runs until the calling task is cancelled.
    func observe() async {
        await withTaskGroup(of: Void.self) { group in
            group.addTask { @MainActor in await self.observeClub() }
            group.addTask { @MainActor in await self.observeFavoriteState() }
        }
    }

    private func observeClub() async {
        switch source {
        case .specific(let clubId):
            self.clubId = clubId
            await load(clubId: clubId)
        case .mine:
            uiState = .loading
            for await result in observeUserContext {
                loadTask?.cancel()
                switch onEnum(of: result) {
                case .unavailable:
                    clubId = nil
                    uiState = .error
                case .success(let success):
                    clubId = (success.value as? UserContext)?.club?.id
                    if let clubId {
                        loadTask = Task { await load(clubId: clubId) }
                    } else {
                        uiState = .noClub
                    }
                }
            }
        }
    }

    func onAction(_ action: ClubAction) {
        switch action {
        case .chooseClicked:
            eventStream.continuation.yield(.openSettings)
        case .teamClicked(let teamId):
            eventStream.continuation.yield(.openTeam(teamId: teamId))
        case .playerClicked(let playerId, let name):
            openPlayer(playerId: playerId, name: name)
        case .retryClicked:
            loadTask?.cancel()
            retryCount += 1
        case .favoriteClicked:
            toggleFavorite()
        }
    }

    private func openPlayer(playerId: String, name: String) {
        guard let clubId else { return }
        Task {
            try? await rememberPlayer(Player(id: playerId, name: name, clubId: clubId))
            eventStream.continuation.yield(.openPlayer(playerId: playerId))
        }
    }

    private func observeFavoriteState() async {
        for await set in observeFavorites {
            favorites = set
            refreshFavorite()
        }
    }

    private func refreshFavorite() {
        guard case .content(var overview) = uiState, let clubId else { return }
        let isFavorite = isFavoriteClub(clubId)
        guard overview.isFavorite != isFavorite else { return }
        overview.isFavorite = isFavorite
        uiState = .content(overview)
    }

    private func isFavoriteClub(_ clubId: String) -> Bool {
        favorites.contains { ($0 as? FavoriteClub)?.id == clubId }
    }

    private func toggleFavorite() {
        guard case .content(let overview) = uiState, let clubId else { return }
        let favorite = FavoriteClub(id: clubId)
        Task {
            if overview.isFavorite {
                try? await removeFavorite(favorite)
            } else {
                try? await addFavorite(favorite)
            }
        }
    }

    private func load(clubId: String) async {
        uiState = .loading
        do {
            let result = try await loadOverview(clubId)
            guard !Task.isCancelled else { return }
            switch onEnum(of: result) {
            case .success(let success): uiState = (success.value as? ClubOverview).map { .content($0.toUi(isFavorite: isFavoriteClub(clubId))) } ?? .error
            case .unavailable: uiState = .error
            }
        } catch {
            // Only cancellation can throw now, and the load that replaced this one owns the state.
        }
    }
}

private extension ClubOverview {
    func toUi(isFavorite: Bool) -> ClubOverviewUi {
        ClubOverviewUi(clubName: club.name, associationName: association.code, teams: teamGroups.map { $0.toUi() }, players: players.map { LineupPlayerUi(id: $0.playerId, name: $0.name, rcPoints: $0.rcPoints?.intValue) }, isFavorite: isFavorite)
    }
}

private extension TeamGroup {
    func toUi() -> TeamUi {
        TeamUi(
            id: primary.team.id,
            name: primary.team.name,
            leagueName: primary.league?.name,
            standing: primary.standing.map { StandingUi(position: Int($0.position), played: Int($0.played), points: Int($0.points)) },
            fixtures: teams.map { TeamFixtureUi(leagueName: $0.league?.name, nextFixture: $0.nextFixture.toUi()) }
        )
    }
}
