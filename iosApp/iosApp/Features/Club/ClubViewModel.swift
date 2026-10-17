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
    /// Bumped by a retry; the route restarts `observe()` when it changes.
    private(set) var retryCount = 0

    private let loadOverview: (String) async throws -> DataResult

    @ObservationIgnored private let observeUserContext = KoinHelper.shared.observeUserContext()
    @ObservationIgnored private let eventStream = AsyncStream<ClubEvent>.makeStream()

    var events: AsyncStream<ClubEvent> { eventStream.stream }

    init(
        source: ClubSource,
        loadOverview: @escaping (String) async throws -> DataResult = { try await KoinHelper.shared.loadClubOverview(clubId: $0) }
    ) {
        self.source = source
        self.loadOverview = loadOverview
    }

    /// Runs until the calling task is cancelled.
    func observe() async {
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
        case .retryClicked:
            loadTask?.cancel()
            retryCount += 1
        }
    }

    private func load(clubId: String) async {
        uiState = .loading
        do {
            let result = try await loadOverview(clubId)
            guard !Task.isCancelled else { return }
            switch onEnum(of: result) {
            case .success(let success): uiState = (success.value as? ClubOverview).map { .content($0.toUi()) } ?? .error
            case .unavailable: uiState = .error
            }
        } catch {
            // Only cancellation can throw now, and the load that replaced this one owns the state.
        }
    }
}

private extension ClubOverview {
    func toUi() -> ClubOverviewUi {
        ClubOverviewUi(clubName: club.name, associationName: association.name, teams: teams.map { $0.toUi() })
    }
}

private extension TeamOverview {
    func toUi() -> TeamUi {
        TeamUi(
            id: team.id,
            name: team.name,
            leagueName: league?.name,
            standing: standing.map { StandingUi(position: Int($0.position), played: Int($0.played), points: Int($0.points)) },
            nextFixture: nextFixture.toUi()
        )
    }
}
