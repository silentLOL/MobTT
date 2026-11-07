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
    private var fixturesTask: Task<Void, Never>?
    private var rankingTask: Task<Void, Never>?
    private var contextTask: Task<Void, Never>?
    private var fixtures = SectionUiState<[FixtureRowUi]>.loading
    private var fixturesExpanded = false
    private var ranking = SectionUiState<[PlayerRankingRowUi]>.loading
    private var rankingExpanded = false
    private var myPlayerId: String?
    @ObservationIgnored private let eventStream = AsyncStream<LeagueDetailEvent>.makeStream()

    var events: AsyncStream<LeagueDetailEvent> { eventStream.stream }

    private let loadDetail: (String) async throws -> DataResult
    private let loadFixtures: (String) async throws -> DataResult
    private let loadRanking: (String) async throws -> DataResult
    @ObservationIgnored private let observeUserContext: any AsyncSequence<DataResult, Never>
    @ObservationIgnored private let rememberPlayer: (Player) async throws -> Void
    /// The Players of the loaded ranking, by ID, so a tapped one can be saved before their screen opens.
    @ObservationIgnored private var rankedPlayers: [String: Player] = [:]
    @ObservationIgnored private let analytics: any AnalyticsTracker
    @ObservationIgnored private let screenViews: ScreenViewReporter
    private let reportsScreenView: Bool

    init(
        leagueId: String,
        highlightTeamId: String? = nil,
        openedFromTeamId: String? = nil,
        loadDetail: @escaping (String) async throws -> DataResult = { try await KoinHelper.shared.loadLeagueDetail(leagueId: $0) },
        loadFixtures: @escaping (String) async throws -> DataResult = { try await KoinHelper.shared.loadLeagueFixtures(leagueId: $0) },
        loadRanking: @escaping (String) async throws -> DataResult = { try await KoinHelper.shared.loadPlayerRanking(leagueId: $0) },
        observeUserContext: any AsyncSequence<DataResult, Never> = KoinHelper.shared.observeUserContext(),
        rememberPlayer: @escaping (Player) async throws -> Void = { try await KoinHelper.shared.getRememberPlayerUseCase().invoke(player: $0) },
        analytics: any AnalyticsTracker = AppAnalytics.tracker,
        reportsScreenView: Bool = true
    ) {
        self.reportsScreenView = reportsScreenView
        self.leagueId = leagueId
        self.highlightTeamId = highlightTeamId
        self.openedFromTeamId = openedFromTeamId
        self.loadDetail = loadDetail
        self.loadFixtures = loadFixtures
        self.loadRanking = loadRanking
        self.observeUserContext = observeUserContext
        self.rememberPlayer = rememberPlayer
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
        contextTask = Task { await observeMyPlayer() }
    }

    func onAction(_ action: LeagueDetailAction) {
        switch action {
        case .retryClicked:
            loadTask?.cancel()
            loadTask = Task { await load() }
            // Only the sections already opened load again; the others still wait for their first opening.
            if fixturesTask != nil {
                startFixtures()
            }
            if rankingTask != nil {
                startRanking()
            }
        case .fixturesToggled:
            fixturesExpanded.toggle()
            if fixturesExpanded && fixturesTask == nil {
                startFixtures()
            }
            applySections()
        case .rankingToggled:
            rankingExpanded.toggle()
            if rankingExpanded && rankingTask == nil {
                startRanking()
            }
            applySections()
        case .teamClicked(let teamId):
            analytics.detailOpened(kind: AnalyticsKind.team, id: teamId, from: AnalyticsScreen.leagueDetail)
            eventStream.continuation.yield(.openTeam(teamId: teamId))
        case .fixtureInfoClicked(let fixtureId):
            analytics.detailOpened(kind: AnalyticsKind.fixture, id: fixtureId, from: AnalyticsScreen.leagueDetail)
            eventStream.continuation.yield(.openFixture(fixtureId: fixtureId))
        case .playerClicked(let playerId):
            analytics.detailOpened(kind: AnalyticsKind.player, id: playerId, from: AnalyticsScreen.leagueDetail)
            openPlayer(playerId)
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
            case .success(let success): uiState = (success.value as? LeagueDetail).map { .content(withSections($0.toUi(highlightTeamId: highlightTeamId, openedFromTeamId: openedFromTeamId, onlyTeamFixtures: onlyTeamFixtures))) } ?? .error
            case .unavailable: uiState = .error
            }
        } catch {
            // Only cancellation can throw now, and the load that replaced this one owns the state.
        }
    }

    private func startFixtures() {
        fixturesTask?.cancel()
        fixturesTask = Task {
            fixtures = .loading
            applySections()
            let highlightTeamId = highlightTeamId
            guard let loaded = await loadSection({ try await self.loadFixtures(self.leagueId) }, map: { (value: [Fixture]) in
                value.map { $0.toUi(highlightTeamId: highlightTeamId) }
            }) else { return }
            fixtures = loaded
            applySections()
        }
    }

    /// XTTV cannot look a Player up by ID, so the ranked Player is saved before their screen opens, as the Team and Club screens do; otherwise the Player screen would find nothing.
    private func openPlayer(_ playerId: String) {
        let player = rankedPlayers[playerId]
        Task {
            if let player {
                try? await rememberPlayer(player)
            }
            eventStream.continuation.yield(.openPlayer(playerId: playerId))
        }
    }

    private func startRanking() {
        rankingTask?.cancel()
        rankingTask = Task {
            ranking = .loading
            applySections()
            guard let loaded = await loadSection({ try await self.loadRanking(self.leagueId) }, map: { (value: [PlayerRankingRow]) in
                rankedPlayers = Dictionary(value.map { ($0.ranked.player.id, $0.ranked.player) }, uniquingKeysWith: { first, _ in first })
                return value.map { $0.toUi() }
            }) else { return }
            ranking = loaded
            applySections()
        }
    }

    /// Nil when the load was cancelled, so the load that replaced it owns the section.
    private func loadSection<Item, Value: Equatable>(
        _ load: () async throws -> DataResult,
        map: ([Item]) -> Value
    ) async -> SectionUiState<Value>? {
        do {
            let result = try await load()
            guard !Task.isCancelled else { return nil }
            switch onEnum(of: result) {
            case .success(let success): return (success.value as? [Item]).map { .loaded(map($0)) } ?? .unavailable
            case .unavailable: return .unavailable
            }
        } catch {
            // Only cancellation can throw now.
            return nil
        }
    }

    private func observeMyPlayer() async {
        for await result in observeUserContext {
            guard case .success(let success) = onEnum(of: result) else { continue }
            myPlayerId = (success.value as? UserContextMyPlayer)?.player.id
            applySections()
        }
    }

    private func withSections(_ detail: LeagueDetailUi) -> LeagueDetailUi {
        var detail = detail
        detail.fixtures = fixtures
        detail.fixturesExpanded = fixturesExpanded
        detail.ranking = ranking
        detail.rankingExpanded = rankingExpanded
        detail.myPlayerId = myPlayerId
        return detail
    }

    private func applySections() {
        guard case .content(let detail) = uiState else { return }
        uiState = .content(withSections(detail))
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
