import Foundation
import Shared
import Testing

@testable import MobTT

@MainActor
struct LeagueDetailViewModelTests {
    @Test func dataUnavailableShowsTheErrorState() async {
        let viewModel = makeViewModel { _ in DataResultUnavailable(error: DataUnavailable(message: "down", cause: nil)) }

        viewModel.loadIfNeeded()
        await settle(viewModel)

        #expect(viewModel.uiState == .error)
    }

    @Test func retryLoadsAgain() async {
        var calls = 0
        let viewModel = makeViewModel { _ in
            calls += 1
            return DataResultUnavailable(error: DataUnavailable(message: "down", cause: nil))
        }

        viewModel.loadIfNeeded()
        await settle(viewModel)
        viewModel.onAction(.retryClicked)
        for _ in 0..<100 where calls < 2 {
            await Task.yield()
        }
        await settle(viewModel)

        #expect(calls == 2)
        #expect(viewModel.uiState == .error)
    }

    @Test func aLeagueWithoutATableIsContentNotAnError() async {
        let viewModel = makeViewModel { _ in
            DataResultSuccess(value: LeagueDetail(league: League(id: "l1", name: "Liga"), standings: []))
        }

        viewModel.loadIfNeeded()
        await settle(viewModel)

        guard case .content(let detail) = viewModel.uiState else {
            Issue.record("Expected content")
            return
        }
        #expect(detail.leagueName == "Liga")
        #expect(detail.standings.isEmpty)
    }

    @Test func fixturesAndRankingStartCollapsedAndAreNotLoaded() async {
        var fixtureLoads = 0
        var rankingLoads = 0
        let viewModel = makeViewModel(
            loadRanking: { _ in
                rankingLoads += 1
                return DataResultSuccess(value: NSArray())
            },
            loadFixtures: { _ in
                fixtureLoads += 1
                return DataResultSuccess(value: NSArray())
            }
        ) { _ in self.twoTeamLeague() }
        viewModel.loadIfNeeded()
        await settle(viewModel)

        guard case .content(let detail) = viewModel.uiState else {
            Issue.record("Expected content")
            return
        }
        #expect(!detail.fixturesExpanded)
        #expect(!detail.rankingExpanded)
        #expect(fixtureLoads == 0)
        #expect(rankingLoads == 0)
    }

    @Test func openingTheFixturesLoadsThemOnceAndKeepsThemWhenClosed() async {
        var fixtureLoads = 0
        let viewModel = makeViewModel(loadFixtures: { _ in
            fixtureLoads += 1
            return DataResultSuccess(value: [self.fixture("mine", home: "t1", away: "t2")] as NSArray)
        }) { _ in self.twoTeamLeague() }
        viewModel.loadIfNeeded()
        await settle(viewModel)

        viewModel.onAction(.fixturesToggled)
        await settleFixtures(viewModel)
        viewModel.onAction(.fixturesToggled)
        viewModel.onAction(.fixturesToggled)

        guard case .content(let detail) = viewModel.uiState else {
            Issue.record("Expected content")
            return
        }
        #expect(detail.fixturesExpanded)
        #expect(detail.visibleFixtures.map(\.id) == ["mine"])
        #expect(fixtureLoads == 1)
    }

    @Test func fixturesXttvCannotAnswerForLeaveTheRestOfTheLeague() async {
        let viewModel = makeViewModel(loadFixtures: { _ in
            DataResultUnavailable(error: DataUnavailable(message: "down", cause: nil))
        }) { _ in self.twoTeamLeague() }
        viewModel.loadIfNeeded()
        await settle(viewModel)

        viewModel.onAction(.fixturesToggled)
        await settleFixtures(viewModel)

        guard case .content(let detail) = viewModel.uiState else {
            Issue.record("Expected content")
            return
        }
        #expect(detail.fixtures == .unavailable)
        #expect(detail.standings.count == 2)
    }

    @Test func theTeamFilterNarrowsTheFixturesToTheHighlightedTeam() async {
        let viewModel = makeViewModel(highlightTeamId: "t1", loadFixtures: { _ in
            DataResultSuccess(value: [self.fixture("mine", home: "t1", away: "t2"), self.fixture("other", home: "t2", away: "t3")] as NSArray)
        }) { _ in
            DataResultSuccess(value: LeagueDetail(league: League(id: "l1", name: "Liga"), standings: []))
        }
        viewModel.loadIfNeeded()
        await settle(viewModel)
        viewModel.onAction(.fixturesToggled)
        await settleFixtures(viewModel)

        viewModel.onAction(.onlyTeamFixturesToggled(true))

        guard case .content(let detail) = viewModel.uiState else {
            Issue.record("Expected content")
            return
        }
        #expect(detail.canFilterToTeam)
        #expect(detail.visibleFixtures.map(\.id) == ["mine"])
    }

    @Test func withoutAHighlightedTeamTheFilterIsNotOffered() async {
        let viewModel = makeViewModel { _ in
            DataResultSuccess(value: LeagueDetail(league: League(id: "l1", name: "Liga"), standings: []))
        }
        viewModel.loadIfNeeded()
        await settle(viewModel)

        guard case .content(let detail) = viewModel.uiState else {
            Issue.record("Expected content")
            return
        }
        #expect(!detail.canFilterToTeam)
    }

    @Test func everyStandingsRowOpensItsTeamExceptTheTeamTheUserCameFrom() async {
        let viewModel = makeViewModel(highlightTeamId: "t1", openedFromTeamId: "t1") { _ in self.twoTeamLeague() }
        viewModel.loadIfNeeded()
        await settle(viewModel)

        guard case .content(let detail) = viewModel.uiState else {
            Issue.record("Expected content")
            return
        }
        #expect(detail.standings.map(\.opensTeam) == [false, true])
    }

    @Test func aHighlightedTeamTheUserDidNotComeFromStillOpens() async {
        let viewModel = makeViewModel(highlightTeamId: "t1", openedFromTeamId: nil) { _ in self.twoTeamLeague() }
        viewModel.loadIfNeeded()
        await settle(viewModel)

        guard case .content(let detail) = viewModel.uiState else {
            Issue.record("Expected content")
            return
        }
        #expect(detail.standings.map(\.isThisTeam) == [true, false])
        #expect(detail.standings.map(\.opensTeam) == [true, true])
    }

    @Test func theRankingFollowsTheLeagueWithTeamNames() async {
        let viewModel = makeViewModel(loadRanking: { _ in
            DataResultSuccess(value: [
                PlayerRankingRow(ranked: self.ranked("p1", "Anna", rank: 1), teamName: "One"),
                PlayerRankingRow(ranked: self.ranked("p2", "Bea", rank: nil), teamName: nil),
            ] as NSArray)
        }) { _ in self.twoTeamLeague() }
        viewModel.loadIfNeeded()
        await settle(viewModel)
        viewModel.onAction(.rankingToggled)
        await settleRanking(viewModel)

        guard case .content(let detail) = viewModel.uiState, case .loaded(let rows) = detail.ranking else {
            Issue.record("Expected a loaded ranking")
            return
        }
        #expect(rows.map(\.name) == ["Anna", "Bea"])
        #expect(rows.map(\.rank) == [1, nil])
        #expect(rows.map(\.teamName) == ["One", nil])
    }

    @Test func aRankingXttvCannotAnswerForLeavesTheRestOfTheLeague() async {
        let viewModel = makeViewModel(loadRanking: { _ in
            DataResultUnavailable(error: DataUnavailable(message: "down", cause: nil))
        }) { _ in self.twoTeamLeague() }
        viewModel.loadIfNeeded()
        await settle(viewModel)
        viewModel.onAction(.rankingToggled)
        await settleRanking(viewModel)

        guard case .content(let detail) = viewModel.uiState else {
            Issue.record("Expected content")
            return
        }
        #expect(detail.ranking == .unavailable)
        #expect(detail.standings.count == 2)
    }

    @Test func myPlayerIsMarkedInTheRanking() async {
        let context = UserContextMyPlayer(
            player: Player(id: "p1", name: "Anna", clubId: "c1"),
            club: Club(id: "c1", name: "Olympic", associationId: "a1"),
            association: Association(id: "a1", name: "WTTV", code: "WTTV")
        )
        let viewModel = makeViewModel(
            observeUserContext: AsyncStream { $0.yield(DataResultSuccess(value: context)) },
            loadRanking: { _ in DataResultSuccess(value: [PlayerRankingRow(ranked: self.ranked("p1", "Anna", rank: 1), teamName: "One")] as NSArray) }
        ) { _ in self.twoTeamLeague() }
        viewModel.loadIfNeeded()
        await settle(viewModel)
        viewModel.onAction(.rankingToggled)
        await settleRanking(viewModel)
        for _ in 0..<100 {
            if case .content(let detail) = viewModel.uiState, detail.myPlayerId != nil { break }
            await Task.yield()
        }

        guard case .content(let detail) = viewModel.uiState, case .loaded(let rows) = detail.ranking else {
            Issue.record("Expected a loaded ranking")
            return
        }
        #expect(rows.map { detail.isMyPlayer($0.playerId) } == [true])
    }

    @Test func tappingAPlayerOpensThemAndReportsIt() async {
        let analytics = RecordingAnalyticsTracker()
        let viewModel = makeViewModel(analytics: analytics) { _ in self.twoTeamLeague() }
        var iterator = viewModel.events.makeAsyncIterator()

        viewModel.onAction(.playerClicked(playerId: "p1"))

        guard case .openPlayer(let playerId) = await iterator.next() else {
            Issue.record("Expected an openPlayer event")
            return
        }
        #expect(playerId == "p1")
        #expect(analytics.events == [LoggedEvent("detail_opened", ["kind": "player", "id": "p1", "from": "league_detail"])])
    }

    @Test func aPlayerTappedInTheRankingIsSavedBeforeTheirScreenOpens() async {
        var saved: [Player] = []
        let viewModel = makeViewModel(
            rememberPlayer: { saved.append($0) },
            loadRanking: { _ in
                DataResultSuccess(value: [
                    PlayerRankingRow(ranked: self.ranked("p1", "Anna", rank: 1), teamName: "One"),
                    PlayerRankingRow(ranked: self.ranked("p2", "Bea", rank: 2), teamName: "One"),
                ] as NSArray)
            }
        ) { _ in self.twoTeamLeague() }
        viewModel.loadIfNeeded()
        await settle(viewModel)
        viewModel.onAction(.rankingToggled)
        await settleRanking(viewModel)
        var iterator = viewModel.events.makeAsyncIterator()

        viewModel.onAction(.playerClicked(playerId: "p2"))
        let event = await iterator.next()

        // The Player screen resolves the Player as soon as it opens, so they must be saved by then.
        #expect(saved.map(\.id) == ["p2"])
        #expect(saved.first?.clubId == "c1")
        #expect(event == .openPlayer(playerId: "p2"))
    }

    @Test func aTeamHighlightedFromTheLeagueTabIsMyTeam() async {
        let viewModel = makeViewModel(highlightTeamId: "t1", openedFromTeamId: nil) { _ in self.twoTeamLeague() }
        viewModel.loadIfNeeded()
        await settle(viewModel)

        guard case .content(let detail) = viewModel.uiState else {
            Issue.record("Expected content")
            return
        }
        #expect(detail.highlightIsMyTeam)
    }

    @Test func aTeamTheUserCameFromIsNotCalledMyTeam() async {
        let viewModel = makeViewModel(highlightTeamId: "t1", openedFromTeamId: "t1") { _ in self.twoTeamLeague() }
        viewModel.loadIfNeeded()
        await settle(viewModel)

        guard case .content(let detail) = viewModel.uiState else {
            Issue.record("Expected content")
            return
        }
        #expect(!detail.highlightIsMyTeam)
    }

    @Test func insideTheLeagueTabTheScreenViewIsLeftToTheTab() {
        let analytics = RecordingAnalyticsTracker()
        let viewModel = makeViewModel(analytics: analytics, reportsScreenView: false) { _ in self.twoTeamLeague() }

        viewModel.screenShown()

        #expect(analytics.events.isEmpty)
    }

    @Test func tappingATeamSendsAnOpenTeamEvent() async {
        let viewModel = makeViewModel { _ in
            DataResultSuccess(value: LeagueDetail(league: League(id: "l1", name: "Liga"), standings: []))
        }
        var iterator = viewModel.events.makeAsyncIterator()

        viewModel.onAction(.teamClicked(teamId: "t2"))

        guard case .openTeam(let teamId) = await iterator.next() else {
            Issue.record("Expected an openTeam event")
            return
        }
        #expect(teamId == "t2")
    }

    @Test func tappingTheInfoButtonSendsAnOpenFixtureEventAndReportsIt() async {
        let analytics = RecordingAnalyticsTracker()
        let viewModel = makeViewModel(analytics: analytics) { _ in
            DataResultSuccess(value: LeagueDetail(league: League(id: "l1", name: "Liga"), standings: []))
        }
        var iterator = viewModel.events.makeAsyncIterator()

        viewModel.onAction(.fixtureInfoClicked(fixtureId: "f1"))

        guard case .openFixture(let fixtureId) = await iterator.next() else {
            Issue.record("Expected an openFixture event")
            return
        }
        #expect(fixtureId == "f1")
        #expect(analytics.events == [LoggedEvent("detail_opened", ["kind": "fixture", "id": "f1", "from": "league_detail"])])
    }

    private func fixture(_ id: String, home: String, away: String) -> Fixture {
        Fixture(
            id: id,
            homeTeamId: home,
            homeTeamName: home,
            awayTeamId: away,
            awayTeamName: away,
            startsAt: KotlinInstant.companion.fromEpochMilliseconds(epochMilliseconds: 1_800_000_000_000),
            venue: nil,
            status: FixtureStatusScheduled.shared
        )
    }

    @Test func reportsTheScreenViewAndTheTeamOpened() {
        let analytics = RecordingAnalyticsTracker()
        let viewModel = makeViewModel(analytics: analytics) { _ in DataResultUnavailable(error: DataUnavailable(message: "down", cause: nil)) }

        viewModel.screenShown()
        viewModel.onAction(.teamClicked(teamId: "t1"))

        #expect(analytics.events == [
            LoggedEvent("screen_view", ["screen": "league_detail"]),
            LoggedEvent("detail_opened", ["kind": "team", "id": "t1", "from": "league_detail"]),
        ])
    }

    private func twoTeamLeague() -> DataResult {
        DataResultSuccess(value: LeagueDetail(
            league: League(id: "l1", name: "Liga"),
            standings: [
                Standing(position: 1, teamId: "t1", teamName: "One", played: 2, points: 4),
                Standing(position: 2, teamId: "t2", teamName: "Two", played: 2, points: 2),
            ]
        ))
    }

    private func ranked(_ id: String, _ name: String, rank: Int?) -> RankedPlayer {
        RankedPlayer(
            player: Player(id: id, name: name, clubId: "c1"),
            rank: rank.map { KotlinInt(int: Int32($0)) },
            teamId: "t1",
            gamesPlayed: 4,
            matchesWon: 6,
            matchesLost: 2,
            ratingsCentralId: nil
        )
    }

    private func makeViewModel(
        highlightTeamId: String? = nil,
        openedFromTeamId: String? = nil,
        analytics: any AnalyticsTracker = NoOpAnalyticsTracker(),
        reportsScreenView: Bool = true,
        observeUserContext: AsyncStream<DataResult> = AsyncStream { $0.finish() },
        rememberPlayer: @escaping (Player) async throws -> Void = { _ in },
        loadRanking: @escaping (String) async throws -> DataResult = { _ in DataResultSuccess(value: NSArray()) },
        loadFixtures: @escaping (String) async throws -> DataResult = { _ in DataResultSuccess(value: NSArray()) },
        loadDetail: @escaping (String) async throws -> DataResult
    ) -> LeagueDetailViewModel {
        LeagueDetailViewModel(
            leagueId: "l1",
            highlightTeamId: highlightTeamId,
            openedFromTeamId: openedFromTeamId,
            loadDetail: loadDetail,
            loadFixtures: loadFixtures,
            loadRanking: loadRanking,
            observeUserContext: observeUserContext,
            rememberPlayer: rememberPlayer,
            analytics: analytics,
            reportsScreenView: reportsScreenView
        )
    }

    private func settleFixtures(_ viewModel: LeagueDetailViewModel) async {
        for _ in 0..<200 {
            if case .content(let detail) = viewModel.uiState, detail.fixtures != .loading { return }
            await Task.yield()
        }
    }

    private func settleRanking(_ viewModel: LeagueDetailViewModel) async {
        for _ in 0..<200 {
            if case .content(let detail) = viewModel.uiState, detail.ranking != .loading { return }
            await Task.yield()
        }
    }

    private func settle(_ viewModel: LeagueDetailViewModel) async {
        for _ in 0..<100 where viewModel.uiState == .loading {
            await Task.yield()
        }
    }
}
