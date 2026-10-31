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

    private func makeViewModel(loadDetail: @escaping (String) async throws -> DataResult) -> LeagueDetailViewModel {
        LeagueDetailViewModel(leagueId: "l1", loadDetail: loadDetail)
    }

    private func settle(_ viewModel: LeagueDetailViewModel) async {
        for _ in 0..<100 where viewModel.uiState == .loading {
            await Task.yield()
        }
    }
}
