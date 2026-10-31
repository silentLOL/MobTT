import Shared
import Testing

@testable import MobTT

@MainActor
struct PlayerDetailViewModelTests {
    @Test func aResolvedPlayerShowsItsNameClubAndAssociation() async {
        let viewModel = makeViewModel { _ in success }

        viewModel.loadIfNeeded()
        await settle(viewModel)

        #expect(viewModel.uiState == .content(PlayerDetailUi(playerName: "Anna Gruber", clubId: "1", clubName: "TTC Olympic", associationName: "WTTV")))
    }

    @Test func tappingTheClubOrATeamEmitsTheMatchingEvent() async {
        let viewModel = makeViewModel { _ in success }
        viewModel.loadIfNeeded()
        await settle(viewModel)
        var events = viewModel.events.makeAsyncIterator()

        viewModel.onAction(.clubClicked)
        viewModel.onAction(.teamClicked(id: "t1"))

        #expect(await events.next() == .openClub(clubId: "1"))
        #expect(await events.next() == .openTeam(teamId: "t1"))
    }

    @Test func aPlayerThatCannotBeResolvedIsUnavailable() async {
        let viewModel = makeViewModel { _ in DataResultSuccess<PlayerDetail>(value: nil) }

        viewModel.loadIfNeeded()
        await settle(viewModel)

        #expect(viewModel.uiState == .unavailable)
    }

    @Test func dataUnavailableShowsTheUnavailableState() async {
        let viewModel = makeViewModel { _ in unavailable }

        viewModel.loadIfNeeded()
        await settle(viewModel)

        #expect(viewModel.uiState == .unavailable)
    }

    @Test func aFavoritePlayerShowsAsFavorite() async {
        let viewModel = makeViewModel(favorites: [[FavoritePlayer(id: "p1") as AnyHashable]]) { _ in success }

        viewModel.loadIfNeeded()
        await viewModel.observe()
        await settle(viewModel)

        #expect(viewModel.uiState == .content(PlayerDetailUi(playerName: "Anna Gruber", clubId: "1", clubName: "TTC Olympic", associationName: "WTTV", isFavorite: true)))
    }

    @Test func tappingTheStarOfAPlayerThatIsNotFavoriteAddsIt() async {
        var added: [Favorite] = []
        let viewModel = makeViewModel(addFavorite: { added.append($0) }) { _ in success }
        viewModel.loadIfNeeded()
        await settle(viewModel)

        viewModel.onAction(.favoriteClicked)
        for _ in 0..<100 where added.isEmpty {
            await Task.yield()
        }

        #expect(added.compactMap { ($0 as? FavoritePlayer)?.id } == ["p1"])
    }

    @Test func tappingTheStarOfAFavoritePlayerRemovesIt() async {
        var removed: [Favorite] = []
        let viewModel = makeViewModel(
            favorites: [[FavoritePlayer(id: "p1") as AnyHashable]],
            removeFavorite: { removed.append($0) }
        ) { _ in success }
        viewModel.loadIfNeeded()
        await viewModel.observe()
        await settle(viewModel)

        viewModel.onAction(.favoriteClicked)
        for _ in 0..<100 where removed.isEmpty {
            await Task.yield()
        }

        #expect(removed.compactMap { ($0 as? FavoritePlayer)?.id } == ["p1"])
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

    @Test func reportsTheScreenViewOnceHoweverOftenTheViewAppears() {
        let analytics = RecordingAnalyticsTracker()
        let viewModel = makeViewModel(analytics: analytics) { _ in success }

        viewModel.screenShown()
        viewModel.screenShown()

        #expect(analytics.events == [LoggedEvent("screen_view", ["screen": "player_detail"])])
    }

    @Test func togglingTheStarReportsTheFavoriteWithoutAName() async {
        let analytics = RecordingAnalyticsTracker()
        let viewModel = makeViewModel(analytics: analytics) { _ in success }
        viewModel.loadIfNeeded()
        await settle(viewModel)

        viewModel.onAction(.favoriteClicked)
        await waitUntil { !analytics.events.isEmpty }

        #expect(analytics.events == [LoggedEvent("favorite_toggled", ["kind": "player", "id": "p1", "enabled": "true"])])
    }

    @Test func openingTheClubOrATeamReportsWhereFromAndNothingElse() async {
        let analytics = RecordingAnalyticsTracker()
        let viewModel = makeViewModel(analytics: analytics) { _ in success }
        viewModel.loadIfNeeded()
        await settle(viewModel)

        viewModel.onAction(.clubClicked)
        viewModel.onAction(.teamClicked(id: "t1"))

        #expect(analytics.events == [
            LoggedEvent("detail_opened", ["kind": "club", "id": "1", "from": "player_detail"]),
            LoggedEvent("detail_opened", ["kind": "team", "id": "t1", "from": "player_detail"]),
        ])
    }

    private func makeViewModel(
        favorites: [Set<AnyHashable>] = [],
        addFavorite: @escaping (Favorite) async throws -> Void = { _ in },
        removeFavorite: @escaping (Favorite) async throws -> Void = { _ in },
        analytics: any AnalyticsTracker = NoOpAnalyticsTracker(),
        loadDetail: @escaping (String) async throws -> DataResult
    ) -> PlayerDetailViewModel {
        PlayerDetailViewModel(
            playerId: "p1",
            loadDetail: loadDetail,
            observeFavorites: AsyncStream { continuation in
                favorites.forEach { continuation.yield($0) }
                continuation.finish()
            },
            addFavorite: addFavorite,
            removeFavorite: removeFavorite,
            analytics: analytics
        )
    }

    private var success: DataResult {
        let player = Player(id: "p1", name: "Anna Gruber", clubId: "1")
        return DataResultSuccess(value: PlayerDetail(player: player, clubName: "TTC Olympic", associationCode: "WTTV", teams: [], rcPoints: nil, ageGroup: nil))
    }

    private var unavailable: DataResult { DataResultUnavailable(error: DataUnavailable(message: "down", cause: nil)) }

    private func settle(_ viewModel: PlayerDetailViewModel) async {
        for _ in 0..<100 where viewModel.uiState == .loading {
            await Task.yield()
        }
    }
}
