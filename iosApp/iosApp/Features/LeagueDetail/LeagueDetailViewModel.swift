import Foundation
import Observation
import Shared

@MainActor
@Observable
final class LeagueDetailViewModel {
    private(set) var uiState: LeagueDetailUiState = .loading

    private let leagueId: String
    private let highlightTeamId: String?
    private let openedFromTeamId: String?
    private var onlyTeamFixtures = false
    private var loadTask: Task<Void, Never>?
    private var rankingTask: Task<Void, Never>?
    private var contextTask: Task<Void, Never>?
    private var ranking = PlayerRankingUiState.loading
    private var myPlayerId: String?
    @ObservationIgnored private let eventStream = AsyncStream<LeagueDetailEvent>.makeStream()

    var events: AsyncStream<LeagueDetailEvent> { eventStream.stream }

    private let loadDetail: (String) async throws -> DataResult
    private let loadRanking: (String) async throws -> DataResult
    @ObservationIgnored private let observeUserContext: any AsyncSequence<DataResult, Never>
    @ObservationIgnored private let analytics: any AnalyticsTracker
    @ObservationIgnored private let screenViews: ScreenViewReporter
    private let reportsScreenView: Bool

    init(
        leagueId: String,
        highlightTeamId: String? = nil,
        openedFromTeamId: String? = nil,
        loadDetail: @escaping (String) async throws -> DataResult = { try await KoinHelper.shared.loadLeagueDetail(leagueId: $0) },
        loadRanking: @escaping (String) async throws -> DataResult = { try await KoinHelper.shared.loadPlayerRanking(leagueId: $0) },
        observeUserContext: any AsyncSequence<DataResult, Never> = KoinHelper.shared.observeUserContext(),
        analytics: any AnalyticsTracker = AppAnalytics.tracker,
        reportsScreenView: Bool = true
    ) {
        self.reportsScreenView = reportsScreenView
        self.leagueId = leagueId
        self.highlightTeamId = highlightTeamId
        self.openedFromTeamId = openedFromTeamId
        self.loadDetail = loadDetail
        self.loadRanking = loadRanking
        self.observeUserContext = observeUserContext
        self.analytics = analytics
        self.screenViews = ScreenViewReporter(tracker: analytics, screen: AnalyticsScreen.leagueDetail)
    }

    /// Reports the screen view, once per screen; inside the League tab the tab reports it instead.
    func screenShown() {
        guard reportsScreenView else { return }
        screenViews.report()
    }

    /// Starts the first load once. It runs outside the view's task, so leaving and returning to the screen neither reloads nor strands it on `.loading`.
    func loadIfNeeded() {
        guard loadTask == nil else { return }
        loadTask = Task { await load() }
        rankingTask = Task { await loadPlayerRanking() }
        contextTask = Task { await observeMyPlayer() }
    }

    func onAction(_ action: LeagueDetailAction) {
        switch action {
        case .retryClicked:
            loadTask?.cancel()
            loadTask = Task { await load() }
            rankingTask?.cancel()
            rankingTask = Task { await loadPlayerRanking() }
        case .teamClicked(let teamId):
            analytics.detailOpened(kind: AnalyticsKind.team, id: teamId, from: AnalyticsScreen.leagueDetail)
            eventStream.continuation.yield(.openTeam(teamId: teamId))
        case .fixtureInfoClicked(let fixtureId):
            analytics.detailOpened(kind: AnalyticsKind.fixture, id: fixtureId, from: AnalyticsScreen.leagueDetail)
            eventStream.continuation.yield(.openFixture(fixtureId: fixtureId))
        case .playerClicked(let playerId):
            analytics.detailOpened(kind: AnalyticsKind.player, id: playerId, from: AnalyticsScreen.leagueDetail)
            eventStream.continuation.yield(.openPlayer(playerId: playerId))
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
            case .success(let success): uiState = (success.value as? LeagueDetail).map { .content(withRanking($0.toUi(highlightTeamId: highlightTeamId, openedFromTeamId: openedFromTeamId, onlyTeamFixtures: onlyTeamFixtures))) } ?? .error
            case .unavailable: uiState = .error
            }
        } catch {
            // Only cancellation can throw now, and the load that replaced this one owns the state.
        }
    }

    private func loadPlayerRanking() async {
        ranking = .loading
        applyRanking()
        do {
            let result = try await loadRanking(leagueId)
            guard !Task.isCancelled else { return }
            switch onEnum(of: result) {
            case .success(let success):
                ranking = (success.value as? [PlayerRankingRow]).map { .loaded($0.map { $0.toUi() }) } ?? .unavailable
            case .unavailable:
                ranking = .unavailable
            }
            applyRanking()
        } catch {
            // Only cancellation can throw now, and the load that replaced this one owns the state.
        }
    }

    private func observeMyPlayer() async {
        for await result in observeUserContext {
            guard case .success(let success) = onEnum(of: result) else { continue }
            myPlayerId = (success.value as? UserContextMyPlayer)?.player.id
            applyRanking()
        }
    }

    private func withRanking(_ detail: LeagueDetailUi) -> LeagueDetailUi {
        var detail = detail
        detail.ranking = ranking
        detail.myPlayerId = myPlayerId
        return detail
    }

    private func applyRanking() {
        guard case .content(let detail) = uiState else { return }
        uiState = .content(withRanking(detail))
    }
}

private extension PlayerRankingRow {
    func toUi() -> PlayerRankingRowUi {
        PlayerRankingRowUi(
            playerId: ranked.player.id,
            teamId: ranked.teamId,
            name: ranked.player.name,
            rank: ranked.rank?.intValue,
            teamName: teamName,
            matchesWon: Int(ranked.matchesWon),
            matchesLost: Int(ranked.matchesLost),
            gamesPlayed: Int(ranked.gamesPlayed)
        )
    }
}

private extension LeagueDetail {
    func toUi(highlightTeamId: String?, openedFromTeamId: String?, onlyTeamFixtures: Bool) -> LeagueDetailUi {
        LeagueDetailUi(
            leagueName: league.name,
            standings: standings.map {
                StandingRowUi(
                    id: $0.teamId,
                    position: Int($0.position),
                    teamName: $0.teamName,
                    played: Int($0.played),
                    points: Int($0.points),
                    isThisTeam: $0.teamId == highlightTeamId,
                    opensTeam: $0.teamId != openedFromTeamId
                )
            },
            fixtures: fixtures.map { $0.toUi(highlightTeamId: highlightTeamId) },
            canFilterToTeam: highlightTeamId != nil,
            highlightIsMyTeam: highlightTeamId != nil && openedFromTeamId == nil,
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
