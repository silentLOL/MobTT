import Foundation
import Observation
import Shared

@MainActor
@Observable
final class TeamDetailViewModel {
    private(set) var uiState: TeamDetailUiState = .loading

    private let teamId: String
    private var loadTask: Task<Void, Never>?

    private let loadDetail: (String) async throws -> DataResult

    init(
        teamId: String,
        loadDetail: @escaping (String) async throws -> DataResult = { try await KoinHelper.shared.loadTeamDetail(teamId: $0) }
    ) {
        self.teamId = teamId
        self.loadDetail = loadDetail
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
        }
    }

    private func load() async {
        uiState = .loading
        do {
            let result = try await loadDetail(teamId)
            guard !Task.isCancelled else { return }
            switch onEnum(of: result) {
            case .success(let success): uiState = (success.value as? TeamDetail).map { .content($0.toUi()) } ?? .error
            case .unavailable: uiState = .error
            }
        } catch {
            // Only cancellation can throw now, and the load that replaced this one owns the state.
        }
    }
}

private extension TeamDetail {
    func toUi() -> TeamDetailUi {
        TeamDetailUi(
            teamName: team.name,
            clubName: clubName,
            leagueName: league?.name,
            standings: standings.map {
                StandingRowUi(
                    id: $0.teamId,
                    position: Int($0.position),
                    teamName: $0.teamName,
                    played: Int($0.played),
                    points: Int($0.points),
                    isThisTeam: isThisTeam(standing: $0)
                )
            },
            lineup: lineup.map { LineupPlayerUi(id: $0.playerId, name: $0.name) }
        )
    }
}
