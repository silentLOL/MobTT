import Shared
import Testing

@testable import MobTT

@MainActor
struct TeamDetailViewModelTests {
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

    @Test func aTeamThatIsNotFavoriteShowsAsNotFavorite() async {
        let viewModel = makeViewModel(favorites: [[FavoriteClub(id: "7") as AnyHashable]]) { _ in success }

        viewModel.loadIfNeeded()
        await viewModel.observe()
        await settle(viewModel)

        #expect(viewModel.uiState == .content(TeamDetailUi(teamName: "Olympic 1", clubName: "TTC Olympic", league: nil, lineup: [])))
    }

    @Test func aFavoriteTeamShowsAsFavorite() async {
        let viewModel = makeViewModel(favorites: [[FavoriteTeam(id: "7") as AnyHashable]]) { _ in success }

        viewModel.loadIfNeeded()
        await viewModel.observe()
        await settle(viewModel)

        #expect(viewModel.uiState == .content(TeamDetailUi(teamName: "Olympic 1", clubName: "TTC Olympic", league: nil, lineup: [], isFavorite: true)))
    }

    @Test func tappingTheStarOfATeamThatIsNotFavoriteAddsIt() async {
        var added: [Favorite] = []
        let viewModel = makeViewModel(addFavorite: { added.append($0) }) { _ in success }
        viewModel.loadIfNeeded()
        await settle(viewModel)

        viewModel.onAction(.favoriteClicked)
        for _ in 0..<100 where added.isEmpty {
            await Task.yield()
        }

        #expect(added.compactMap { ($0 as? FavoriteTeam)?.id } == ["7"])
    }

    @Test func tappingTheStarOfAFavoriteTeamRemovesIt() async {
        var removed: [Favorite] = []
        let viewModel = makeViewModel(
            favorites: [[FavoriteTeam(id: "7") as AnyHashable]],
            removeFavorite: { removed.append($0) }
        ) { _ in success }
        viewModel.loadIfNeeded()
        await viewModel.observe()
        await settle(viewModel)

        viewModel.onAction(.favoriteClicked)
        for _ in 0..<100 where removed.isEmpty {
            await Task.yield()
        }

        #expect(removed.compactMap { ($0 as? FavoriteTeam)?.id } == ["7"])
    }

    @Test func tappingTheStarWithoutContentChangesNothing() async {
        var changes = 0
        let viewModel = makeViewModel(addFavorite: { _ in changes += 1 }, removeFavorite: { _ in changes += 1 }) { _ in unavailable }
        viewModel.loadIfNeeded()
        await settle(viewModel)

        viewModel.onAction(.favoriteClicked)
        for _ in 0..<20 { await Task.yield() }

        #expect(changes == 0)
    }

    @Test func tappingTheLeagueCardSendsAnOpenLeagueEvent() async {
        let viewModel = makeViewModel { _ in success }
        var iterator = viewModel.events.makeAsyncIterator()

        viewModel.onAction(.leagueClicked(leagueId: "l1"))

        guard case .openLeague(let leagueId) = await iterator.next() else {
            Issue.record("Expected an openLeague event")
            return
        }
        #expect(leagueId == "l1")
    }

    private func makeViewModel(
        favorites: [Set<AnyHashable>] = [],
        addFavorite: @escaping (Favorite) async throws -> Void = { _ in },
        removeFavorite: @escaping (Favorite) async throws -> Void = { _ in },
        loadDetail: @escaping (String) async throws -> DataResult
    ) -> TeamDetailViewModel {
        TeamDetailViewModel(
            teamId: "7",
            loadDetail: loadDetail,
            observeFavorites: AsyncStream { continuation in
                favorites.forEach { continuation.yield($0) }
                continuation.finish()
            },
            addFavorite: addFavorite,
            removeFavorite: removeFavorite
        )
    }

    private var success: DataResult {
        let team = Team(id: "7", name: "Olympic 1", clubId: "1", leagueId: "l1")
        return DataResultSuccess(value: TeamDetail(team: team, clubName: "TTC Olympic", league: nil, standing: nil, nextFixture: NextFixtureFreeWeek.shared, lineup: []))
    }

    private var unavailable: DataResult { DataResultUnavailable(error: DataUnavailable(message: "down", cause: nil)) }

    private func settle(_ viewModel: TeamDetailViewModel) async {
        for _ in 0..<100 where viewModel.uiState == .loading {
            await Task.yield()
        }
    }
}
