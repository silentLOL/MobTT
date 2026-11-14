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
        ratingHistory: RatingHistorySectionUi(history: .loaded(expectedHistory), since: yearBeforeNow)
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

    @Test func aPlayerWithARatingsCentralEntryShowsTheRatingHistoryLoaded() async {
        var historyReads: [String] = []
        let viewModel = makeViewModel(loadRatingHistory: { historyReads.append($0); return history })

        viewModel.loadIfNeeded()
        await waitUntil { viewModel.ratingHistory?.history == .loaded(expectedHistory) }

        #expect(viewModel.ratingHistory == RatingHistorySectionUi(history: .loaded(expectedHistory), since: yearBeforeNow))
        #expect(historyReads == ["163468"])
    }

    @Test func theRatingsCentralEntryIsLookedUpInTheLeaguesOfThePlayersTeams() async {
        var lookups: [[String]] = []
        let viewModel = makeViewModel(loadRatingsCentralId: { _, leagues in lookups.append(leagues); return DataResultSuccess(value: "163468" as NSString) })

        viewModel.loadIfNeeded()
        await waitUntil { viewModel.ratingHistory != nil }

        #expect(lookups == [["l1"]])
    }

    @Test func anUnreachableRatingHistoryIsUnavailableAndRetryReloadsIt() async {
        var fails = true
        let viewModel = makeViewModel(loadRatingHistory: { _ in fails ? unavailable : history })
        viewModel.loadIfNeeded()
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

    @Test func theRatingHistoryStartsOnTheLast12Months() async {
        let viewModel = makeViewModel(loadRatingHistory: { _ in historyOf(old, recent) })

        await open(viewModel)

        #expect(viewModel.ratingHistory?.span == .lastYear)
        #expect(viewModel.ratingHistory?.visibleUpdates == [recent.ui])
    }

    @Test func choosingAllShowsTheWholeRatingHistory() async {
        let viewModel = makeViewModel(loadRatingHistory: { _ in historyOf(old, recent) })
        await open(viewModel)

        viewModel.onAction(.ratingHistorySpanChosen(.all))

        #expect(viewModel.ratingHistory?.visibleUpdates == [old.ui, recent.ui])
    }

    @Test func aRatingUpdateExactly12MonthsAgoStillCounts() async {
        let edge = Fixture(epochDay: 20_399, points: 400)
        let viewModel = makeViewModel(loadRatingHistory: { _ in historyOf(edge) })

        await open(viewModel)

        #expect(viewModel.ratingHistory?.visibleUpdates == [edge.ui])
    }

    @Test func aPlayerWithoutARecentRatingUpdateIsToldSoInTheLast12Months() async {
        let viewModel = makeViewModel(loadRatingHistory: { _ in historyOf(Fixture(epochDay: 19_783, points: 331)) })

        await open(viewModel)

        #expect(viewModel.ratingHistory?.hasNoRecentUpdate == true)
        #expect(viewModel.ratingHistory?.visibleUpdates == [])
    }

    @Test func anEmptyRatingHistoryIsNotMistakenForAnOldOne() async {
        let viewModel = makeViewModel(loadRatingHistory: { _ in historyOf() })

        await open(viewModel)

        #expect(viewModel.ratingHistory?.hasNoRecentUpdate == false)
    }

    @Test func dotsAreDrawnUpTo40RatingUpdatesInTheVisibleSpan() async {
        // Weekly from 2025-09-14: 52 of 60 fall in the last 12 months, and 40 of the last 40.
        let weekly = (0..<60).map { Fixture(epochDay: 20_345 + $0 * 7, points: 500 + $0) }
        let many = makeViewModel(loadRatingHistory: { _ in historyOf(weekly) })
        let fewer = makeViewModel(loadRatingHistory: { _ in historyOf(Array(weekly.suffix(40))) })

        await open(many)
        await open(fewer)

        #expect(many.ratingHistory?.visibleUpdates.count == 52)
        #expect(many.ratingHistory?.showsDots == false)
        #expect(fewer.ratingHistory?.showsDots == true)
    }

    @Test func theInfoButtonOpensTheExplanationAndReportsIt() async {
        let analytics = RecordingAnalyticsTracker()
        let viewModel = makeViewModel(analytics: analytics)
        viewModel.loadIfNeeded()
        await settle(viewModel)

        viewModel.onAction(.ratingInfoClicked)

        #expect(viewModel.ratingInfoShown)
        #expect(analytics.events == [LoggedEvent("rating_info_opened", [:])])
    }

    @Test func dismissingTheExplanationClosesIt() async {
        let viewModel = makeViewModel()
        viewModel.loadIfNeeded()
        await settle(viewModel)
        viewModel.onAction(.ratingInfoClicked)

        viewModel.onAction(.ratingInfoDismissed)

        #expect(!viewModel.ratingInfoShown)
    }

    @Test func theExplanationsLinkOpensRatingsCentralsHowItWorksPage() async {
        let viewModel = makeViewModel()
        viewModel.loadIfNeeded()
        await settle(viewModel)
        var events = viewModel.events.makeAsyncIterator()

        viewModel.onAction(.ratingInfoLinkClicked)

        #expect(await events.next() == .openWebPage(url: URL(string: "https://www.ratingscentral.com/HowItWorks.php")!))
    }

    /// Loads the screen until the Rating history has an answer.
    private func open(_ viewModel: PlayerDetailViewModel) async {
        viewModel.loadIfNeeded()
        await waitUntil { viewModel.ratingHistory != nil && viewModel.ratingHistory?.history != .loading }
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
            now: { now },
            ratingExplanationUrl: URL(string: "https://www.ratingscentral.com/HowItWorks.php")!,
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

/// 2026-11-07, the day the tests pretend it is; its year-earlier day is 2025-11-07 (epoch day 20_399).
private let now = Date(timeIntervalSince1970: 20_764 * 86_400 + 10 * 3_600)
private let yearBeforeNow = Date(timeIntervalSince1970: 20_399 * 86_400)

private struct Fixture {
    let epochDay: Int
    let points: Int

    var update: RatingUpdate {
        let date = Date(timeIntervalSince1970: Double(epochDay) * 86_400)
        let parts = Calendar(identifier: .gregorian).dateComponents(in: TimeZone(identifier: "UTC")!, from: date)
        return RatingUpdate(
            date: Kotlinx_datetimeLocalDate(year: Int32(parts.year!), month: Int32(parts.month!), day: Int32(parts.day!)),
            name: "Week",
            before: Rating(points: Int32(points - 10), deviation: 70),
            after: Rating(points: Int32(points), deviation: 70),
            pointChange: 10
        )
    }

    var ui: RatingUpdateUi {
        RatingUpdateUi(date: Date(timeIntervalSince1970: Double(epochDay) * 86_400), name: "Week", points: points, deviation: 70, pointChange: 10)
    }
}

/// 2025-10-19 and 2026-10-25.
private let old = Fixture(epochDay: 20_380, points: 331)
private let recent = Fixture(epochDay: 20_751, points: 519)

private func historyOf(_ fixtures: Fixture...) -> DataResult { historyOf(fixtures) }

private func historyOf(_ fixtures: [Fixture]) -> DataResult {
    DataResultSuccess(value: RatingHistory(updates: fixtures.map(\.update)))
}

private var unavailable: DataResult { DataResultUnavailable(error: DataUnavailable(message: "down", cause: nil)) }

/// A load that never answers while the test runs.
private func never() async throws -> DataResult {
    try await Task.sleep(for: .seconds(60))
    return unavailable
}

private extension PlayerDetailViewModel {
    var ratingInfoShown: Bool {
        guard case .content(let detail) = uiState else { return false }
        return detail.ratingInfoShown
    }

    var ratingHistory: RatingHistorySectionUi? {
        guard case .content(let detail) = uiState else { return nil }
        return detail.ratingHistory
    }
}
