import Foundation
import Observation
import Shared

@MainActor
@Observable
final class LeagueDetailViewModel {
    private(set) var uiState: LeagueDetailUiState = .loading

    private let leagueId: String
    private let highlightTeamId: String?
    private var onlyTeamFixtures = false
    private var loadTask: Task<Void, Never>?
    @ObservationIgnored private let eventStream = AsyncStream<LeagueDetailEvent>.makeStream()

    var events: AsyncStream<LeagueDetailEvent> { eventStream.stream }

    private let loadDetail: (String) async throws -> DataResult
    @ObservationIgnored private let analytics: any AnalyticsTracker
    @ObservationIgnored private let screenViews: ScreenViewReporter

    init(
        leagueId: String,
        highlightTeamId: String? = nil,
        loadDetail: @escaping (String) async throws -> DataResult = { try await KoinHelper.shared.loadLeagueDetail(leagueId: $0) },
        analytics: any AnalyticsTracker = AppAnalytics.tracker
    ) {
        self.leagueId = leagueId
        self.highlightTeamId = highlightTeamId
        self.loadDetail = loadDetail
        self.analytics = analytics
        self.screenViews = ScreenViewReporter(tracker: analytics, screen: AnalyticsScreen.leagueDetail)
    }

    /// Reports the screen view, once per screen.
    func screenShown() {
        screenViews.report()
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
        case .teamClicked(let teamId):
            analytics.detailOpened(kind: AnalyticsKind.team, id: teamId, from: AnalyticsScreen.leagueDetail)
            eventStream.continuation.yield(.openTeam(teamId: teamId))
        case .onlyTeamFixturesToggled(let enabled):
            onlyTeamFixtures = enabled
            guard case .content(var detail) = uiState else { return }
            detail.onlyTeamFixtures = enabled
            uiState = .content(detail)
        }
    }

    private func load() async {
        uiState = .loading
        do {
            let result = try await loadDetail(leagueId)
            guard !Task.isCancelled else { return }
            switch onEnum(of: result) {
            case .success(let success): uiState = (success.value as? LeagueDetail).map { .content($0.toUi(highlightTeamId: highlightTeamId, onlyTeamFixtures: onlyTeamFixtures)) } ?? .error
            case .unavailable: uiState = .error
            }
        } catch {
            // Only cancellation can throw now, and the load that replaced this one owns the state.
        }
    }
}

private extension LeagueDetail {
    func toUi(highlightTeamId: String?, onlyTeamFixtures: Bool) -> LeagueDetailUi {
        LeagueDetailUi(
            leagueName: league.name,
            standings: standings.map {
                StandingRowUi(
                    id: $0.teamId,
                    position: Int($0.position),
                    teamName: $0.teamName,
                    played: Int($0.played),
                    points: Int($0.points),
                    isThisTeam: $0.teamId == highlightTeamId
                )
            },
            fixtures: fixtures.map { $0.toUi(highlightTeamId: highlightTeamId) },
            canFilterToTeam: highlightTeamId != nil,
            highlightTeamId: highlightTeamId,
            onlyTeamFixtures: onlyTeamFixtures && highlightTeamId != nil
        )
    }
}

private extension Fixture {
    func toUi(highlightTeamId: String?) -> FixtureRowUi {
        let score = (status as? FixtureStatusFinished)?.score
        return FixtureRowUi(
            id: id,
            homeTeamId: homeTeamId,
            homeTeamName: homeTeamName,
            awayTeamId: awayTeamId,
            awayTeamName: awayTeamName,
            startsAt: Date(timeIntervalSince1970: TimeInterval(startsAt.toEpochMilliseconds()) / 1000),
            venue: venue,
            score: score.map { FixtureScoreUi(home: Int($0.homeMatchesWon), away: Int($0.awayMatchesWon)) },
            isThisTeam: highlightTeamId != nil && (homeTeamId == highlightTeamId || awayTeamId == highlightTeamId)
        )
    }
}
