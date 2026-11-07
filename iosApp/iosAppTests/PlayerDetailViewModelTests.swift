import Foundation
import Shared
import Testing

@testable import MobTT

@MainActor
struct PlayerDetailViewModelTests {
    private let anna = PlayerDetailUi(
        playerName: "Anna Gruber",
        clubId: "1",
        club: .loaded(PlayerClubUi(clubName: "TTC Olympic", associationName: "WTTV")),
        teams: .loaded(PlayerTeamsUi(teams: [PlayerTeamUi(id: "t1", name: "TTC Olympic 1")], rcPoints: 1992, ageGroup: "U171")),
        ratingHistory: RatingHistorySectionUi()
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

    @Test func aPlayerWithARatingsCentralEntryShowsTheRatingHistoryCollapsedWithoutAskingForIt() async {
        var historyReads: [String] = []
        let viewModel = makeViewModel(loadRatingHistory: { historyReads.append($0); return history })

        viewModel.loadIfNeeded()
        await waitUntil { viewModel.ratingHistory != nil }

        #expect(viewModel.ratingHistory == RatingHistorySectionUi())
        #expect(historyReads.isEmpty)
    }

    @Test func theRatingsCentralEntryIsLookedUpInTheLeaguesOfThePlayersTeams() async {
        var lookups: [[String]] = []
        let viewModel = makeViewModel(loadRatingsCentralId: { _, leagues in lookups.append(leagues); return DataResultSuccess(value: "163468" as NSString) })

        viewModel.loadIfNeeded()
        await waitUntil { viewModel.ratingHistory != nil }

        #expect(lookups == [["l1"]])
    }

    @Test func openingTheRatingHistoryLoadsIt() async {
        var historyReads: [String] = []
        let viewModel = makeViewModel(loadRatingHistory: { historyReads.append($0); return history })
        viewModel.loadIfNeeded()
        await waitUntil { viewModel.ratingHistory != nil }

        viewModel.onAction(.ratingHistoryToggled)
        await waitUntil { viewModel.ratingHistory?.history != .loading }

        #expect(viewModel.ratingHistory == RatingHistorySectionUi(isExpanded: true, history: .loaded(expectedHistory)))
        #expect(historyReads == ["163468"])
    }

    @Test func closingAndReopeningTheRatingHistoryKeepsItWithoutAskingAgain() async {
        var historyReads = 0
        let viewModel = makeViewModel(loadRatingHistory: { _ in historyReads += 1; return history })
        viewModel.loadIfNeeded()
        await waitUntil { viewModel.ratingHistory != nil }
        viewModel.onAction(.ratingHistoryToggled)
        await waitUntil { viewModel.ratingHistory?.history != .loading }

        viewModel.onAction(.ratingHistoryToggled)
        viewModel.onAction(.ratingHistoryToggled)
        for _ in 0..<20 { await Task.yield() }

        #expect(viewModel.ratingHistory?.history == .loaded(expectedHistory))
        #expect(historyReads == 1)
    }

    @Test func anUnreachableRatingHistoryIsUnavailableAndRetryReloadsIt() async {
        var fails = true
        let viewModel = makeViewModel(loadRatingHistory: { _ in fails ? unavailable : history })
        viewModel.loadIfNeeded()
        await waitUntil { viewModel.ratingHistory != nil }
        viewModel.onAction(.ratingHistoryToggled)
        await waitUntil { viewModel.ratingHistory?.history == .unavailable }

        fails = false
        viewModel.onAction(.ratingHistoryRetryClicked)
        await waitUntil { viewModel.ratingHistory?.history == .loaded(expectedHistory) }

        #expect(viewModel.ratingHistory?.history == .loaded(expectedHistory))
    }

    @Test func aPlayerWithoutARatingsCentralEntryHasNoRatingHistory() async {
        let viewModel = makeViewModel(loadRatingsCentralId: { _, _ in DataResultSuccess<NSString>(value: nil) })

        viewModel.loadIfNeeded()
        await settle(viewModel)
        for _ in 0..<20 { await Task.yield() }

        #expect(viewModel.ratingHistory == nil)
    }

    @Test func theRatingHistoryWaitsForTheTeams() async {
        var lookups = 0
        let viewModel = makeViewModel(loadTeams: { _ in try await never() }, loadRatingsCentralId: { _, _ in lookups += 1; return DataResultSuccess(value: "1" as NSString) })

        viewModel.loadIfNeeded()
        for _ in 0..<50 { await Task.yield() }

        #expect(viewModel.ratingHistory == nil)
        #expect(lookups == 0)
    }

    @Test func withRatingHistoriesSwitchedOffNothingIsShownOrAsked() async {
        var lookups = 0
        let viewModel = makeViewModel(ratingHistoryEnabled: false, loadRatingsCentralId: { _, _ in lookups += 1; return DataResultSuccess(value: "1" as NSString) })

        viewModel.loadIfNeeded()
        await settle(viewModel)
        for _ in 0..<20 { await Task.yield() }

        #expect(viewModel.ratingHistory == nil)
        #expect(lookups == 0)
    }

    @Test func openingTheRatingHistoryIsReportedEachTimeButClosingIsNot() async {
        let analytics = RecordingAnalyticsTracker()
        let viewModel = makeViewModel(analytics: analytics)
        viewModel.loadIfNeeded()
        await waitUntil { viewModel.ratingHistory != nil }

        viewModel.onAction(.ratingHistoryToggled)
        viewModel.onAction(.ratingHistoryToggled)
        viewModel.onAction(.ratingHistoryToggled)

        #expect(analytics.events == [LoggedEvent("rating_history_opened", [:]), LoggedEvent("rating_history_opened", [:])])
    }

    private func makeViewModel(
        loadPlayer: @escaping (String) async throws -> DataResult = { _ in DataResultSuccess(value: Player(id: "p1", name: "Anna Gruber", clubId: "1")) },
        loadClub: @escaping (Player) async throws -> DataResult = { _ in DataResultSuccess(value: PlayerClub(clubName: "TTC Olympic", associationCode: "WTTV")) },
        loadTeams: @escaping (Player) async throws -> DataResult = { _ in
            DataResultSuccess(value: PlayerTeams(teams: [Team(id: "t1", name: "TTC Olympic 1", clubId: "1", leagueId: "l1")], rcPoints: 1992, ageGroup: "U171"))
        },
        ratingHistoryEnabled: Bool = true,
        loadRatingsCentralId: @escaping (String, [String]) async throws -> DataResult = { _, _ in DataResultSuccess(value: "163468" as NSString) },
        loadRatingHistory: @escaping (String) async throws -> DataResult = { _ in history },
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
            ratingHistoryEnabled: ratingHistoryEnabled,
            loadRatingsCentralId: loadRatingsCentralId,
            loadRatingHistory: loadRatingHistory,
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
        // The Ratings Central entry is looked up once the Teams are in.
        for _ in 0..<20 { await Task.yield() }
    }
}

private var club: DataResult { DataResultSuccess(value: PlayerClub(clubName: "TTC Olympic", associationCode: "WTTV")) }

private var teams: DataResult {
    DataResultSuccess(value: PlayerTeams(teams: [Team(id: "t1", name: "TTC Olympic 1", clubId: "1", leagueId: "l1")], rcPoints: 1992, ageGroup: "U171"))
}

private var history: DataResult {
    DataResultSuccess(value: RatingHistory(updates: [
        RatingUpdate(date: Kotlinx_datetimeLocalDate(year: 2026, month: 10, day: 25), name: "OÖTTV KW 43", before: Rating(points: 447, deviation: 66), after: Rating(points: 519, deviation: 86), pointChange: 72),
    ]))
}

private let expectedHistory = RatingHistoryUi(updates: [
    RatingUpdateUi(date: Date(timeIntervalSince1970: 20_751 * 86_400), name: "OÖTTV KW 43", points: 519, deviation: 86, pointChange: 72),
])

private var unavailable: DataResult { DataResultUnavailable(error: DataUnavailable(message: "down", cause: nil)) }

/// A load that never answers while the test runs.
private func never() async throws -> DataResult {
    try await Task.sleep(for: .seconds(60))
    return unavailable
}

private extension PlayerDetailViewModel {
    var ratingHistory: RatingHistorySectionUi? {
        guard case .content(let detail) = uiState else { return nil }
        return detail.ratingHistory
    }
}
