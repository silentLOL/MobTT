import Foundation
import Observation
import Shared

@MainActor
@Observable
final class LeagueDetailViewModel {
    private(set) var uiState: LeagueDetailUiState = .loading

    private let leagueId: String
    private var loadTask: Task<Void, Never>?

    private let loadDetail: (String) async throws -> DataResult

    init(
        leagueId: String,
        loadDetail: @escaping (String) async throws -> DataResult = { try await KoinHelper.shared.loadLeagueDetail(leagueId: $0) }
    ) {
        self.leagueId = leagueId
        self.loadDetail = loadDetail
    }

    /// Starts the first load once. It runs outside the view's task, so leaving and returning to the screen neither reloads nor strands it on `.loading`.
    func loadIfNeeded() {
        guard loadTask == nil else { return }
        loadTask = Task { await load() }
    }

    func onAction(_ action: LeagueDetailAction) {
        switch action {
        case .retryClicked:
            loadTask?.cancel()
            loadTask = Task { await load() }
        }
    }

    private func load() async {
        uiState = .loading
        do {
            let result = try await loadDetail(leagueId)
            guard !Task.isCancelled else { return }
            switch onEnum(of: result) {
            case .success(let success): uiState = (success.value as? LeagueDetail).map { .content($0.toUi()) } ?? .error
            case .unavailable: uiState = .error
            }
        } catch {
            // Only cancellation can throw now, and the load that replaced this one owns the state.
        }
    }
}

private extension LeagueDetail {
    func toUi() -> LeagueDetailUi {
        LeagueDetailUi(
            leagueName: league.name,
            standings: standings.map {
                StandingRowUi(
                    id: $0.teamId,
                    position: Int($0.position),
                    teamName: $0.teamName,
                    played: Int($0.played),
                    points: Int($0.points),
                    isThisTeam: false
                )
            },
            fixtures: fixtures.map { $0.toUi() }
        )
    }
}

private extension Fixture {
    func toUi() -> FixtureRowUi {
        let score = (status as? FixtureStatusFinished)?.score
        return FixtureRowUi(
            id: id,
            homeTeamName: homeTeamName,
            awayTeamName: awayTeamName,
            startsAt: Date(timeIntervalSince1970: TimeInterval(startsAt.toEpochMilliseconds()) / 1000),
            venue: venue,
            score: score.map { FixtureScoreUi(home: Int($0.homeMatchesWon), away: Int($0.awayMatchesWon)) }
        )
    }
}
