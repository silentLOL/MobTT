import Shared
import Testing

@testable import MobTT

@MainActor
struct PlayerDetailViewModelTests {
    private let anna = PlayerDetailUi(
        playerName: "Anna Gruber",
        clubId: "1",
        club: .loaded(PlayerClubUi(clubName: "TTC Olympic", associationName: "WTTV")),
        teams: .loaded(PlayerTeamsUi(teams: [PlayerTeamUi(id: "t1", name: "TTC Olympic 1")], rcPoints: 1992, ageGroup: "U171"))
    )

    @Test func aResolvedPlayerShowsItsNameClubAssociationAndTeams() async {
        let viewModel = makeViewModel()

        viewModel.loadIfNeeded()
        await settle(viewModel)

        #expect(viewModel.uiState == .content(anna))
    }

    @Test func theScreenShowsThePlayerBeforeItsSectionsLoad() async {
        let viewModel = makeViewModel(loadClub: { _ in try await never() }, loadTeams: { _ in try await never() })

        viewModel.loadIfNeeded()
        await waitUntil { viewModel.uiState != .loading }

        #expect(viewModel.uiState == .content(PlayerDetailUi(playerName: "Anna Gruber", clubId: "1")))
    }

    @Test func aPlayerThatCannotBeResolvedIsUnavailable() async {
        let viewModel = makeViewModel(loadPlayer: { _ in DataResultSuccess<Player>(value: nil) })

        viewModel.loadIfNeeded()
        await settle(viewModel)

        #expect(viewModel.uiState == .unavailable)
    }

    @Test func dataUnavailableShowsTheUnavailableState() async {
        let viewModel = makeViewModel(loadPlayer: { _ in unavailable })

        viewModel.loadIfNeeded()
        await settle(viewModel)

        #expect(viewModel.uiState == .unavailable)
    }

    @Test func anUnreachableClubLeavesTheTeamsLoaded() async {
        let viewModel = makeViewModel(loadClub: { _ in unavailable })

        viewModel.loadIfNeeded()
        await settle(viewModel)

        var expected = anna
        expected.club = .unavailable
        #expect(viewModel.uiState == .content(expected))
    }

    @Test func aClubXttvNoLongerResolvesLoadsAsNone() async {
        let viewModel = makeViewModel(loadClub: { _ in DataResultSuccess<PlayerClub>(value: nil) })

        viewModel.loadIfNeeded()
        await settle(viewModel)

        var expected = anna
        expected.club = .loaded(nil)
        #expect(viewModel.uiState == .content(expected))
    }

    @Test func retryingTheClubReloadsOnlyTheClub() async {
        var clubFails = true
        var teamLoads = 0
        let viewModel = makeViewModel(
            loadClub: { _ in clubFails ? unavailable : club },
            loadTeams: { _ in
                teamLoads += 1
                return teams
            }
        )
        viewModel.loadIfNeeded()
        await settle(viewModel)

        clubFails = false
        viewModel.onAction(.clubRetryClicked)
        await waitUntil { viewModel.uiState == .content(anna) }

        #expect(viewModel.uiState == .content(anna))
        #expect(teamLoads == 1)
    }

    @Test func retryingTheTeamsReloadsThem() async {
        var teamsFail = true
        let viewModel = makeViewModel(loadTeams: { _ in teamsFail ? unavailable : teams })
        viewModel.loadIfNeeded()
        await settle(viewModel)

        teamsFail = false
        viewModel.onAction(.teamsRetryClicked)
        await waitUntil { viewModel.uiState == .content(anna) }

        #expect(viewModel.uiState == .content(anna))
    }

    @Test func tappingTheClubOrATeamEmitsTheMatchingEvent() async {
        let viewModel = makeViewModel()
        viewModel.loadIfNeeded()
        await settle(viewModel)
        var events = viewModel.events.makeAsyncIterator()

        viewModel.onAction(.clubClicked)
        viewModel.onAction(.teamClicked(id: "t1"))

        #expect(await events.next() == .openClub(clubId: "1"))
        #expect(await events.next() == .openTeam(teamId: "t1"))
    }

    @Test func aFavoritePlayerShowsAsFavorite() async {
        let viewModel = makeViewModel(favorites: [[FavoritePlayer(id: "p1") as AnyHashable]])

        viewModel.loadIfNeeded()
        await viewModel.observe()
        await settle(viewModel)

        var expected = anna
        expected.isFavorite = true
        #expect(viewModel.uiState == .content(expected))
    }

    @Test func tappingTheStarOfAPlayerThatIsNotFavoriteAddsIt() async {
        var added: [Favorite] = []
        let viewModel = makeViewModel(addFavorite: { added.append($0) })
        viewModel.loadIfNeeded()
        await settle(viewModel)

        viewModel.onAction(.favoriteClicked)
        await waitUntil { !added.isEmpty }

        #expect(added.compactMap { ($0 as? FavoritePlayer)?.id } == ["p1"])
    }

    @Test func tappingTheStarOfAFavoritePlayerRemovesIt() async {
        var removed: [Favorite] = []
        let viewModel = makeViewModel(favorites: [[FavoritePlayer(id: "p1") as AnyHashable]], removeFavorite: { removed.append($0) })
        viewModel.loadIfNeeded()
        await viewModel.observe()
        await settle(viewModel)

        viewModel.onAction(.favoriteClicked)
        await waitUntil { !removed.isEmpty }

        #expect(removed.compactMap { ($0 as? FavoritePlayer)?.id } == ["p1"])
    }

    @Test func tappingTheStarWithoutContentChangesNothing() async {
        var changes = 0
        let viewModel = makeViewModel(loadPlayer: { _ in unavailable }, addFavorite: { _ in changes += 1 }, removeFavorite: { _ in changes += 1 })
        viewModel.loadIfNeeded()
        await settle(viewModel)

        viewModel.onAction(.favoriteClicked)
        for _ in 0..<20 { await Task.yield() }

        #expect(changes == 0)
    }

    @Test func reportsTheScreenViewOnceHoweverOftenTheViewAppears() {
        let analytics = RecordingAnalyticsTracker()
        let viewModel = makeViewModel(analytics: analytics)

        viewModel.screenShown()
        viewModel.screenShown()

        #expect(analytics.events == [LoggedEvent("screen_view", ["screen": "player_detail"])])
    }

    @Test func togglingTheStarReportsTheFavoriteWithoutAName() async {
        let analytics = RecordingAnalyticsTracker()
        let viewModel = makeViewModel(analytics: analytics)
        viewModel.loadIfNeeded()
        await settle(viewModel)

        viewModel.onAction(.favoriteClicked)
        await waitUntil { !analytics.events.isEmpty }

        #expect(analytics.events == [LoggedEvent("favorite_toggled", ["kind": "player", "id": "p1", "enabled": "true"])])
    }

    @Test func openingTheClubOrATeamReportsWhereFromAndNothingElse() async {
        let analytics = RecordingAnalyticsTracker()
        let viewModel = makeViewModel(analytics: analytics)
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
        loadPlayer: @escaping (String) async throws -> DataResult = { _ in DataResultSuccess(value: Player(id: "p1", name: "Anna Gruber", clubId: "1")) },
        loadClub: @escaping (Player) async throws -> DataResult = { _ in DataResultSuccess(value: PlayerClub(clubName: "TTC Olympic", associationCode: "WTTV")) },
        loadTeams: @escaping (Player) async throws -> DataResult = { _ in
            DataResultSuccess(value: PlayerTeams(teams: [Team(id: "t1", name: "TTC Olympic 1", clubId: "1", leagueId: "l1")], rcPoints: 1992, ageGroup: "U171"))
        },
        favorites: [Set<AnyHashable>] = [],
        addFavorite: @escaping (Favorite) async throws -> Void = { _ in },
        removeFavorite: @escaping (Favorite) async throws -> Void = { _ in },
        analytics: any AnalyticsTracker = NoOpAnalyticsTracker()
    ) -> PlayerDetailViewModel {
        PlayerDetailViewModel(
            playerId: "p1",
            loadPlayer: loadPlayer,
            loadClub: loadClub,
            loadTeams: loadTeams,
            observeFavorites: AsyncStream { continuation in
                favorites.forEach { continuation.yield($0) }
                continuation.finish()
            },
            addFavorite: addFavorite,
            removeFavorite: removeFavorite,
            analytics: analytics
        )
    }

    private func settle(_ viewModel: PlayerDetailViewModel) async {
        await waitUntil {
            guard case .content(let detail) = viewModel.uiState else { return viewModel.uiState == .unavailable }
            return detail.club != .loading && detail.teams != .loading
        }
    }
}

private var club: DataResult { DataResultSuccess(value: PlayerClub(clubName: "TTC Olympic", associationCode: "WTTV")) }

private var teams: DataResult {
    DataResultSuccess(value: PlayerTeams(teams: [Team(id: "t1", name: "TTC Olympic 1", clubId: "1", leagueId: "l1")], rcPoints: 1992, ageGroup: "U171"))
}

private var unavailable: DataResult { DataResultUnavailable(error: DataUnavailable(message: "down", cause: nil)) }

/// A load that never answers while the test runs.
private func never() async throws -> DataResult {
    try await Task.sleep(for: .seconds(60))
    return unavailable
}
