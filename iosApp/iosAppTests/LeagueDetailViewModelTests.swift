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

    @Test func aLeagueWithoutTableOrFixturesIsContentNotAnError() async {
        let viewModel = makeViewModel { _ in
            DataResultSuccess(value: LeagueDetail(league: League(id: "l1", name: "Liga"), standings: [], fixtures: []))
        }

        viewModel.loadIfNeeded()
        await settle(viewModel)

        #expect(viewModel.uiState == .content(LeagueDetailUi(leagueName: "Liga", standings: [], fixtures: [])))
    }

    @Test func theTeamFilterNarrowsTheFixturesToTheHighlightedTeam() async {
        let viewModel = makeViewModel(highlightTeamId: "t1") { _ in
            DataResultSuccess(value: LeagueDetail(
                league: League(id: "l1", name: "Liga"),
                standings: [],
                fixtures: [fixture("mine", home: "t1", away: "t2"), fixture("other", home: "t2", away: "t3")]
            ))
        }
        viewModel.loadIfNeeded()
        await settle(viewModel)

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
            DataResultSuccess(value: LeagueDetail(league: League(id: "l1", name: "Liga"), standings: [], fixtures: []))
        }
        viewModel.loadIfNeeded()
        await settle(viewModel)

        guard case .content(let detail) = viewModel.uiState else {
            Issue.record("Expected content")
            return
        }
        #expect(!detail.canFilterToTeam)
    }

    @Test func tappingATeamSendsAnOpenTeamEvent() async {
        let viewModel = makeViewModel { _ in
            DataResultSuccess(value: LeagueDetail(league: League(id: "l1", name: "Liga"), standings: [], fixtures: []))
        }
        var iterator = viewModel.events.makeAsyncIterator()

        viewModel.onAction(.teamClicked(teamId: "t2"))

        guard case .openTeam(let teamId) = await iterator.next() else {
            Issue.record("Expected an openTeam event")
            return
        }
        #expect(teamId == "t2")
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

    private func makeViewModel(highlightTeamId: String? = nil, loadDetail: @escaping (String) async throws -> DataResult) -> LeagueDetailViewModel {
        LeagueDetailViewModel(leagueId: "l1", highlightTeamId: highlightTeamId, loadDetail: loadDetail)
    }

    private func settle(_ viewModel: LeagueDetailViewModel) async {
        for _ in 0..<100 where viewModel.uiState == .loading {
            await Task.yield()
        }
    }
}
