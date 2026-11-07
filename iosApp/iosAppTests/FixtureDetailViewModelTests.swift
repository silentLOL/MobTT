import Shared
import Testing

@testable import MobTT

@MainActor
struct FixtureDetailViewModelTests {
    @Test func dataUnavailableShowsTheErrorState() async {
        let viewModel = makeViewModel { _ in DataResultUnavailable(error: DataUnavailable(message: "down", cause: nil)) }

        viewModel.loadIfNeeded()
        await settle(viewModel)

        #expect(viewModel.uiState == .error)
    }

    @Test func anUnknownFixtureShowsTheErrorState() async {
        let viewModel = makeViewModel { _ in DataResultSuccess<FixtureDetail>(value: nil) }

        viewModel.loadIfNeeded()
        await settle(viewModel)

        #expect(viewModel.uiState == .error)
    }

    @Test func aPlayedFixtureCarriesItsScoreAndMatches() async {
        let viewModel = makeViewModel { _ in
            DataResultSuccess(value: FixtureDetail(
                fixture: fixture(status: FixtureStatusFinished(score: FixtureScore(homeMatchesWon: 6, awayMatchesWon: 4))),
                address: VenueAddress(street: "Hauptstraße 12", postalCode: "1010", city: "Wien"),
                matches: [
                    Match(number: 1, kind: .singles, homePlayers: ["Anna"], awayPlayers: ["Lukas"], score: MatchScore(homeSetsWon: 3, awaySetsWon: 1)),
                    Match(number: 2, kind: .doubles, homePlayers: ["Anna", "Michael"], awayPlayers: ["Lukas", "Sophie"], score: nil),
                ]
            ))
        }

        viewModel.loadIfNeeded()
        await settle(viewModel)

        guard case .content(let detail) = viewModel.uiState else {
            Issue.record("Expected content")
            return
        }
        #expect(detail.score == FixtureScoreUi(home: 6, away: 4))
        #expect(detail.address == "Hauptstraße 12, 1010 Wien")
        #expect(detail.matches.map(\.number) == [1, 2])
        #expect(detail.matches[0].score == FixtureScoreUi(home: 3, away: 1))
        #expect(detail.matches[1].isDoubles)
        #expect(detail.matches[1].homePlayers == "Anna / Michael")
        #expect(detail.matches[1].score == nil)
    }

    @Test func aFixtureNotYetPlayedHasNoScoreAndNoMatches() async {
        let viewModel = makeViewModel { _ in
            DataResultSuccess(value: FixtureDetail(fixture: fixture(status: FixtureStatusScheduled.shared), address: nil, matches: []))
        }

        viewModel.loadIfNeeded()
        await settle(viewModel)

        guard case .content(let detail) = viewModel.uiState else {
            Issue.record("Expected content")
            return
        }
        #expect(detail.score == nil)
        #expect(detail.address == nil)
        #expect(detail.matches.isEmpty)
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

    @Test func tappingATeamSendsAnOpenTeamEvent() async {
        let viewModel = makeViewModel { _ in DataResultSuccess<FixtureDetail>(value: nil) }
        var iterator = viewModel.events.makeAsyncIterator()

        viewModel.onAction(.teamClicked(teamId: "t2"))

        guard case .openTeam(let teamId) = await iterator.next() else {
            Issue.record("Expected an openTeam event")
            return
        }
        #expect(teamId == "t2")
    }

    @Test func reportsTheScreenViewAndTheTeamOpened() {
        let analytics = RecordingAnalyticsTracker()
        let viewModel = makeViewModel(analytics: analytics) { _ in DataResultUnavailable(error: DataUnavailable(message: "down", cause: nil)) }

        viewModel.screenShown()
        viewModel.onAction(.teamClicked(teamId: "t1"))

        #expect(analytics.events == [
            LoggedEvent("screen_view", ["screen": "fixture_detail"]),
            LoggedEvent("detail_opened", ["kind": "team", "id": "t1", "from": "fixture_detail"]),
        ])
    }

    @Test func withSharingOnTheFixtureCanBeShared() async {
        let viewModel = makeViewModel(sharingEnabled: true) { _ in
            DataResultSuccess(value: FixtureDetail(fixture: fixture(status: FixtureStatusScheduled.shared), address: nil, matches: []))
        }

        viewModel.loadIfNeeded()
        await settle(viewModel)

        guard case .content(let detail) = viewModel.uiState else {
            Issue.record("Expected content")
            return
        }
        #expect(detail.canShare)
    }

    @Test func withSharingOffTheFixtureCannotBeShared() async {
        let viewModel = makeViewModel(sharingEnabled: false) { _ in
            DataResultSuccess(value: FixtureDetail(fixture: fixture(status: FixtureStatusScheduled.shared), address: nil, matches: []))
        }

        viewModel.loadIfNeeded()
        await settle(viewModel)

        guard case .content(let detail) = viewModel.uiState else {
            Issue.record("Expected content")
            return
        }
        #expect(!detail.canShare)
    }

    @Test func sharingSendsTheFixtureWithItsLinkAndReportsItsStatus() async {
        let analytics = RecordingAnalyticsTracker()
        let finished = FixtureStatusFinished(score: FixtureScore(homeMatchesWon: 6, awayMatchesWon: 4))
        let viewModel = makeViewModel(analytics: analytics, sharingEnabled: true) { _ in
            DataResultSuccess(value: FixtureDetail(fixture: fixture(status: finished), address: nil, matches: []))
        }
        viewModel.loadIfNeeded()
        await settle(viewModel)
        var iterator = viewModel.events.makeAsyncIterator()

        viewModel.onAction(.shareClicked)

        guard case .share(let detail, let link) = await iterator.next() else {
            Issue.record("Expected a share event")
            return
        }
        #expect(detail.homeTeamName == "Olympic 1")
        #expect(link == "https://links.mobtt.example/f/f1")
        #expect(analytics.events.last == LoggedEvent("fixture_share_tapped", ["status": "finished"]))
    }

    @Test func sharingAScheduledFixtureReportsItAsScheduled() async {
        let analytics = RecordingAnalyticsTracker()
        let viewModel = makeViewModel(analytics: analytics, sharingEnabled: true) { _ in
            DataResultSuccess(value: FixtureDetail(fixture: fixture(status: FixtureStatusScheduled.shared), address: nil, matches: []))
        }
        viewModel.loadIfNeeded()
        await settle(viewModel)

        viewModel.onAction(.shareClicked)

        #expect(analytics.events.last == LoggedEvent("fixture_share_tapped", ["status": "scheduled"]))
    }

    @Test func tappingTheVenueOpensItsSearchTextAndReportsTheFixtureStatus() async {
        let analytics = RecordingAnalyticsTracker()
        let finished = FixtureStatusFinished(score: FixtureScore(homeMatchesWon: 6, awayMatchesWon: 4))
        let viewModel = makeViewModel(analytics: analytics) { _ in
            DataResultSuccess(value: FixtureDetail(
                fixture: fixture(status: finished, venue: "Sporthalle Olympic"),
                address: VenueAddress(street: nil, postalCode: nil, city: "Wien"),
                matches: []
            ))
        }
        viewModel.loadIfNeeded()
        await settle(viewModel)
        var iterator = viewModel.events.makeAsyncIterator()

        viewModel.onAction(.venueClicked)

        guard case .openVenue(let searchText) = await iterator.next() else {
            Issue.record("Expected an open venue event")
            return
        }
        #expect(searchText == "Sporthalle Olympic, Wien")
        #expect(analytics.events.last == LoggedEvent("fixture_venue_tapped", ["status": "finished"]))
    }

    @Test func withoutAVenueTappingReportsNothing() async {
        let analytics = RecordingAnalyticsTracker()
        let viewModel = makeViewModel(analytics: analytics) { _ in
            DataResultSuccess(value: FixtureDetail(fixture: fixture(status: FixtureStatusScheduled.shared), address: nil, matches: []))
        }
        viewModel.loadIfNeeded()
        await settle(viewModel)
        let logged = analytics.events.count

        viewModel.onAction(.venueClicked)

        #expect(analytics.events.count == logged)
    }

    @Test func withoutALinksDomainTheFixtureIsSharedWithoutALink() async {
        let viewModel = makeViewModel(sharingEnabled: true, linkTo: { _ in nil }) { _ in
            DataResultSuccess(value: FixtureDetail(fixture: fixture(status: FixtureStatusScheduled.shared), address: nil, matches: []))
        }
        viewModel.loadIfNeeded()
        await settle(viewModel)
        var iterator = viewModel.events.makeAsyncIterator()

        viewModel.onAction(.shareClicked)

        guard case .share(_, let link) = await iterator.next() else {
            Issue.record("Expected a share event")
            return
        }
        #expect(link == nil)
    }

    private func fixture(status: FixtureStatus, venue: String? = nil) -> Fixture {
        Fixture(
            id: "f1",
            homeTeamId: "t1",
            homeTeamName: "Olympic 1",
            awayTeamId: "t2",
            awayTeamName: "Olympic 2",
            startsAt: KotlinInstant.companion.fromEpochMilliseconds(epochMilliseconds: 1_800_000_000_000),
            venue: venue,
            status: status
        )
    }

    private func makeViewModel(
        analytics: any AnalyticsTracker = NoOpAnalyticsTracker(),
        sharingEnabled: Bool = false,
        linkTo: @escaping (String) -> String? = { "https://links.mobtt.example/f/\($0)" },
        loadDetail: @escaping (String) async throws -> DataResult
    ) -> FixtureDetailViewModel {
        FixtureDetailViewModel(
            fixtureId: "f1",
            loadDetail: loadDetail,
            sharingEnabled: sharingEnabled,
            linkTo: linkTo,
            analytics: analytics
        )
    }

    private func settle(_ viewModel: FixtureDetailViewModel) async {
        for _ in 0..<100 where viewModel.uiState == .loading {
            await Task.yield()
        }
    }
}
