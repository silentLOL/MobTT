import Foundation
import Observation
import Shared

@MainActor
@Observable
final class FixtureDetailViewModel {
    private(set) var uiState: FixtureDetailUiState = .loading

    private let fixtureId: String
    private var loadTask: Task<Void, Never>?
    @ObservationIgnored private let eventStream = AsyncStream<FixtureDetailEvent>.makeStream()

    var events: AsyncStream<FixtureDetailEvent> { eventStream.stream }

    private let loadDetail: (String) async throws -> DataResult
    @ObservationIgnored private let analytics: any AnalyticsTracker
    @ObservationIgnored private let screenViews: ScreenViewReporter

    init(
        fixtureId: String,
        loadDetail: @escaping (String) async throws -> DataResult = { try await KoinHelper.shared.loadFixtureDetail(fixtureId: $0) },
        analytics: any AnalyticsTracker = AppAnalytics.tracker
    ) {
        self.fixtureId = fixtureId
        self.loadDetail = loadDetail
        self.analytics = analytics
        self.screenViews = ScreenViewReporter(tracker: analytics, screen: AnalyticsScreen.fixtureDetail)
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

    func onAction(_ action: FixtureDetailAction) {
        switch action {
        case .retryClicked:
            loadTask?.cancel()
            loadTask = Task { await load() }
        case .teamClicked(let teamId):
            analytics.detailOpened(kind: AnalyticsKind.team, id: teamId, from: AnalyticsScreen.fixtureDetail)
            eventStream.continuation.yield(.openTeam(teamId: teamId))
        }
    }

    private func load() async {
        uiState = .loading
        do {
            let result = try await loadDetail(fixtureId)
            guard !Task.isCancelled else { return }
            switch onEnum(of: result) {
            case .success(let success): uiState = (success.value as? FixtureDetail).map { .content($0.toUi()) } ?? .error
            case .unavailable: uiState = .error
            }
        } catch {
            // Only cancellation can throw now, and the load that replaced this one owns the state.
        }
    }
}

private extension FixtureDetail {
    func toUi() -> FixtureDetailUi {
        let finished = (fixture.status as? FixtureStatusFinished)?.score
        return FixtureDetailUi(
            homeTeamId: fixture.homeTeamId,
            homeTeamName: fixture.homeTeamName,
            awayTeamId: fixture.awayTeamId,
            awayTeamName: fixture.awayTeamName,
            startsAt: Date(timeIntervalSince1970: TimeInterval(fixture.startsAt.toEpochMilliseconds()) / 1000),
            venue: fixture.venue,
            address: address?.formatted(),
            score: finished.map { FixtureScoreUi(home: Int($0.homeMatchesWon), away: Int($0.awayMatchesWon)) },
            matches: matches.map { $0.toUi() }
        )
    }
}

private extension VenueAddress {
    func formatted() -> String? {
        let cityLine = [postalCode, city].compactMap { $0 }.joined(separator: " ")
        let line = [street, cityLine.isEmpty ? nil : cityLine].compactMap { $0 }.joined(separator: ", ")
        return line.isEmpty ? nil : line
    }
}

private extension Match {
    func toUi() -> MatchRowUi {
        MatchRowUi(
            id: Int(number),
            number: Int(number),
            isDoubles: kind == .doubles,
            homePlayers: homePlayers.joined(separator: " / "),
            awayPlayers: awayPlayers.joined(separator: " / "),
            score: score.map { FixtureScoreUi(home: Int($0.homeSetsWon), away: Int($0.awaySetsWon)) }
        )
    }
}
