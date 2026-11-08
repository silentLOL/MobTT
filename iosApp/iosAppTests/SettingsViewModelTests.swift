import Foundation
import Shared
import Testing

@testable import MobTT

@MainActor
struct SettingsViewModelTests {
    private let association = Association(id: "w", name: "WTTV", code: "WTTV")
    private let club = Club(id: "c1", name: "Nussdorf TTC", associationId: "w")

    @Test func usageConsentToggleShowsTheStoredAnswer() async {
        let viewModel = makeViewModel(emitting: [], consents: [.unanswered, .allowed])

        await viewModel.observe()

        #expect(viewModel.uiState.usageConsentAllowed)
    }

    @Test func declinedAndUnansweredBothShowTheToggleOff() async {
        let viewModel = makeViewModel(emitting: [], consents: [.allowed, .declined])

        await viewModel.observe()

        #expect(!viewModel.uiState.usageConsentAllowed)
    }

    @Test func togglingSavesTheAnswer() async {
        var saved: [Bool] = []
        let viewModel = makeViewModel(emitting: [], setUsageConsent: { saved.append($0) })

        viewModel.onAction(.usageConsentToggled(true))
        await waitUntil { !saved.isEmpty }

        #expect(saved == [true])
    }

    @Test func chosenPlayerShowsItsClubAndAssociation() async {
        let player = Player(id: "p1", name: "Anna Gruber", clubId: "c1")
        let context = UserContextMyPlayer(player: player, club: club, association: association)
        let viewModel = makeViewModel(emitting: [DataResultSuccess(value: context)])

        await viewModel.observe()

        #expect(viewModel.uiState == SettingsUiState(
            myPlayerName: "Anna Gruber",
            derivedFrom: .player,
            derivedClubName: "Nussdorf TTC",
            derivedAssociationName: "WTTV"
        ))
    }

    @Test func chosenClubShowsNoPlayerAndItsAssociation() async {
        let context = UserContextMyClub(club: club, association: association)
        let viewModel = makeViewModel(emitting: [DataResultSuccess(value: context)])

        await viewModel.observe()

        #expect(viewModel.uiState == SettingsUiState(
            myClubName: "Nussdorf TTC",
            derivedFrom: .club,
            derivedClubName: "Nussdorf TTC",
            derivedAssociationName: "WTTV"
        ))
    }

    @Test func nothingChosenShowsTheEmptyState() async {
        let viewModel = makeViewModel(emitting: [DataResultSuccess(value: UserContextNone.shared)])

        await viewModel.observe()

        #expect(viewModel.uiState == SettingsUiState())
    }

    @Test func unavailableDataFallsBackToNothingChosen() async {
        let viewModel = makeViewModel(emitting: [DataResultUnavailable(error: DataUnavailable(message: "down", cause: nil))])

        await viewModel.observe()

        #expect(viewModel.uiState == SettingsUiState())
    }

    @Test func pickerOpensForTheChosenKindAndBackClosesIt() {
        let viewModel = makeViewModel(emitting: [])

        viewModel.onAction(.chooseClubClicked)
        #expect(viewModel.uiState.picker?.kind == .club)

        viewModel.onAction(.pickerBackClicked)
        #expect(viewModel.uiState.picker == nil)
    }

    @Test func pickerShowsTheSearchResults() async {
        let viewModel = makeViewModel(emitting: [], clubs: [club])

        viewModel.onAction(.chooseClubClicked)
        await waitUntil { !(viewModel.uiState.picker?.entries.isEmpty ?? true) }

        #expect(viewModel.uiState.picker?.entries == [DirectoryEntryUi(id: "c1", title: "Nussdorf TTC", subtitle: nil, selected: false)])
    }

    @Test func choosingAnEntrySavesItAndClosesThePicker() async {
        var saved: [String] = []
        let viewModel = makeViewModel(emitting: [], clubs: [club], setMyClub: { saved.append($0) })

        viewModel.onAction(.chooseClubClicked)
        viewModel.onAction(.pickerEntryClicked(id: "c1"))
        await waitUntil { viewModel.uiState.picker == nil }

        #expect(saved == ["c1"])
        #expect(viewModel.uiState.picker == nil)
    }

    @Test func aFailedSaveKeepsThePickerOpen() async {
        struct SaveFailed: Error {}
        var attempts = 0
        let viewModel = makeViewModel(emitting: [], clubs: [club], setMyClub: { _ in
            attempts += 1
            throw SaveFailed()
        })

        viewModel.onAction(.chooseClubClicked)
        viewModel.onAction(.pickerEntryClicked(id: "c1"))
        await waitUntil { attempts > 0 }

        #expect(attempts == 1)
        #expect(viewModel.uiState.picker?.kind == .club)
    }

    @Test func playerPickerStartsAtTheVerbandStep() async {
        let viewModel = makeViewModel(emitting: [])

        viewModel.onAction(.choosePlayerClicked)
        await waitUntil { !(viewModel.uiState.picker?.playerDrillDown?.entries.isEmpty ?? true) }

        let drillDown = viewModel.uiState.picker?.playerDrillDown
        #expect(viewModel.uiState.picker?.kind == .player)
        #expect(drillDown?.step == .association)
        #expect(drillDown?.contextName == nil)
        #expect(drillDown?.entries.map(\.title) == ["Wiener Tischtennisverband", "Niederösterreichischer Tischtennisverband"])
    }

    @Test func choosingAVerbandThenALeagueListsItsPlayers() async {
        let fixtures = PlayerDrillDownFixtures()
        let viewModel = makeViewModel(emitting: [], playerLoaders: fixtures.loaders)

        viewModel.onAction(.choosePlayerClicked)
        await waitUntil { !(viewModel.uiState.picker?.playerDrillDown?.entries.isEmpty ?? true) }
        viewModel.onAction(.pickerEntryClicked(id: "w"))
        await waitUntil { viewModel.uiState.picker?.playerDrillDown?.step == .league && !(viewModel.uiState.picker?.playerDrillDown?.entries.isEmpty ?? true) }

        #expect(viewModel.uiState.picker?.playerDrillDown?.contextName == "Wiener Tischtennisverband")
        #expect(viewModel.uiState.picker?.playerDrillDown?.entries.map(\.title) == ["1. Landesliga Herren", "Gebietsliga Nord"])

        viewModel.onAction(.pickerEntryClicked(id: "l1"))
        await waitUntil { viewModel.uiState.picker?.playerDrillDown?.step == .player && !(viewModel.uiState.picker?.playerDrillDown?.entries.isEmpty ?? true) }

        let drillDown = viewModel.uiState.picker?.playerDrillDown
        #expect(drillDown?.contextName == "1. Landesliga Herren")
        #expect(drillDown?.entries == [
            DirectoryEntryUi(id: "p1", title: "Anna Gruber", subtitle: "TTC Olympic", selected: false),
            DirectoryEntryUi(id: "p2", title: "Michael Huber", subtitle: "Nussdorf TTC", selected: false),
        ])
        #expect(fixtures.reads.leagues == ["w"])
        #expect(fixtures.reads.players == ["l1"])
    }

    @Test func typingOnlyFiltersTheStepLocallyWithoutReadingAgain() async {
        let fixtures = PlayerDrillDownFixtures()
        let viewModel = await playerStep(fixtures: fixtures)

        viewModel.onAction(.pickerQueryChanged("HUB"))

        #expect(viewModel.uiState.picker?.playerDrillDown?.entries.map(\.id) == ["p2"])
        #expect(viewModel.uiState.picker?.query == "HUB")
        viewModel.onAction(.pickerQueryChanged("nobody"))
        #expect(viewModel.uiState.picker?.playerDrillDown?.entries.isEmpty == true)
        await Task.yield()
        #expect(fixtures.reads.players == ["l1"])
        #expect(fixtures.reads.associations == 1)
    }

    @Test func backGoesOneStepBackAndClosesFromTheFirstStep() async {
        let viewModel = await playerStep()

        viewModel.onAction(.pickerBackClicked)
        #expect(viewModel.uiState.picker?.playerDrillDown?.step == .league)
        viewModel.onAction(.pickerBackClicked)
        #expect(viewModel.uiState.picker?.playerDrillDown?.step == .association)
        viewModel.onAction(.pickerBackClicked)
        #expect(viewModel.uiState.picker == nil)
    }

    @Test func enteringAStepClearsTheFilter() async {
        let viewModel = makeViewModel(emitting: [])
        viewModel.onAction(.choosePlayerClicked)
        await waitUntil { !(viewModel.uiState.picker?.playerDrillDown?.entries.isEmpty ?? true) }
        viewModel.onAction(.pickerQueryChanged("wiener"))

        viewModel.onAction(.pickerEntryClicked(id: "w"))

        #expect(viewModel.uiState.picker?.query == "")
    }

    @Test func pickingAPlayerSavesItAndClosesThePicker() async {
        var saved: [String] = []
        let viewModel = await playerStep(setMyPlayer: { saved.append($0) })

        viewModel.onAction(.pickerEntryClicked(id: "p2"))
        await waitUntil { viewModel.uiState.picker == nil }

        #expect(saved == ["p2"])
        #expect(viewModel.uiState.picker == nil)
    }

    @Test func theChosenPlayerIsMarkedSelected() async {
        let player = Player(id: "p1", name: "Anna Gruber", clubId: "c1")
        let context = UserContextMyPlayer(player: player, club: club, association: association)
        let viewModel = await playerStep(emitting: [DataResultSuccess(value: context)])
        await viewModel.observe()

        #expect(viewModel.uiState.picker?.playerDrillDown?.entries.map(\.selected) == [true, false])
    }

    /// A Player picker already at the Player step of the first Verband's first League.
    private func playerStep(
        fixtures: PlayerDrillDownFixtures = PlayerDrillDownFixtures(),
        emitting results: [DataResult] = [],
        setMyPlayer: @escaping (String) async throws -> Void = { _ in }
    ) async -> SettingsViewModel {
        let viewModel = makeViewModel(emitting: results, playerLoaders: fixtures.loaders, setMyPlayer: setMyPlayer)
        viewModel.onAction(.choosePlayerClicked)
        await waitUntil { !(viewModel.uiState.picker?.playerDrillDown?.entries.isEmpty ?? true) }
        viewModel.onAction(.pickerEntryClicked(id: "w"))
        await waitUntil { viewModel.uiState.picker?.playerDrillDown?.step == .league && !(viewModel.uiState.picker?.playerDrillDown?.entries.isEmpty ?? true) }
        viewModel.onAction(.pickerEntryClicked(id: "l1"))
        await waitUntil { viewModel.uiState.picker?.playerDrillDown?.step == .player && !(viewModel.uiState.picker?.playerDrillDown?.entries.isEmpty ?? true) }
        return viewModel
    }

    @Test func theCoffeeRowShowsOnlyWhenATipPageIsSet() {
        #expect(!makeViewModel(emitting: []).uiState.showSupportLink)
        #expect(makeViewModel(emitting: [], supportUrl: "https://example.org/coffee").uiState.showSupportLink)
    }

    @Test func tappingTheCoffeeRowOpensTheTipPageAndLogsIt() async {
        let analytics = RecordingAnalyticsTracker()
        let viewModel = makeViewModel(emitting: [], supportUrl: "https://example.org/coffee", analytics: analytics)
        var events = viewModel.events.makeAsyncIterator()

        viewModel.onAction(.supportClicked)

        #expect(await events.next() == .openSupportLink(URL(string: "https://example.org/coffee")!))
        #expect(analytics.events == [LoggedEvent("support_link_opened", [:])])
    }

    @Test func tappingTheLicensesRowAsksToOpenTheLicenses() async {
        let viewModel = makeViewModel(emitting: [])
        var events = viewModel.events.makeAsyncIterator()

        viewModel.onAction(.licensesClicked)

        #expect(await events.next() == .openLicenses)
    }

    @Test func tappingTheCoffeeRowWithoutATipPageDoesNothing() async {
        let analytics = RecordingAnalyticsTracker()
        let viewModel = makeViewModel(emitting: [], analytics: analytics)

        viewModel.onAction(.supportClicked)
        viewModel.onAction(.usageConsentToggled(true))
        await waitUntil { !analytics.events.isEmpty }

        #expect(analytics.events == [LoggedEvent("consent_changed", ["enabled": "true", "source": "settings"])])
    }

    /// SwiftUI cancels a view's `.task` when another screen covers it, and runs it again on return.
    @Test func theCoffeeRowStillOpensTheTipPageAfterTheScreenWasCoveredAndShownAgain() async {
        let viewModel = makeViewModel(emitting: [], supportUrl: "https://example.org/coffee")
        let covered = Task { for await _ in viewModel.events {} }
        covered.cancel()
        await covered.value
        var events = viewModel.events.makeAsyncIterator()

        viewModel.onAction(.supportClicked)

        #expect(await events.next() == .openSupportLink(URL(string: "https://example.org/coffee")!))
    }

    @Test func reportsTheScreenViewAppearanceAndConsentChanges() async {
        let analytics = RecordingAnalyticsTracker()
        let viewModel = makeViewModel(emitting: [], analytics: analytics)

        viewModel.screenShown()
        viewModel.onAction(.appearanceSelected(.dark))
        viewModel.onAction(.usageConsentToggled(true))
        await waitUntil { analytics.events.count == 3 }

        #expect(analytics.events == [
            LoggedEvent("screen_view", ["screen": "settings"]),
            LoggedEvent("setting_changed", ["setting": "appearance", "value": "dark"]),
            LoggedEvent("consent_changed", ["enabled": "true", "source": "settings"]),
        ])
    }

    @Test func savingMyClubReportsTheChoiceFromSettings() async {
        let analytics = RecordingAnalyticsTracker()
        let viewModel = makeViewModel(emitting: [], clubs: [club], analytics: analytics)
        viewModel.onAction(.chooseClubClicked)

        viewModel.onAction(.pickerEntryClicked(id: "c1"))
        await waitUntil { !analytics.events.isEmpty }

        #expect(analytics.events == [LoggedEvent("my_club_selected", ["source": "settings"])])
    }

    @Test func searchReportsOnlyTheQueryLengthOncePausedAndNeverTheText() async throws {
        let analytics = RecordingAnalyticsTracker()
        let viewModel = makeViewModel(emitting: [], analytics: analytics, searchSettle: .milliseconds(20))
        viewModel.onAction(.chooseClubClicked)

        viewModel.onAction(.pickerQueryChanged("n"))
        viewModel.onAction(.pickerQueryChanged("nussdorf"))
        try await Task.sleep(for: .milliseconds(200))

        #expect(analytics.events == [LoggedEvent("search_performed", ["screen": "settings", "query_length": "8"])])
    }

    private func makeViewModel(
        emitting results: [DataResult],
        clubs: [Club] = [],
        playerLoaders: PlayerDrillDownLoaders = PlayerDrillDownFixtures().loaders,
        consents: [UsageConsent] = [],
        setUsageConsent: @escaping (Bool) async throws -> Void = { _ in },
        setMyPlayer: @escaping (String) async throws -> Void = { _ in },
        setMyClub: @escaping (String) async throws -> Void = { _ in },
        supportUrl: String? = nil,
        analytics: any AnalyticsTracker = NoOpAnalyticsTracker(),
        searchSettle: Duration = .seconds(1)
    ) -> SettingsViewModel {
        let stream = AsyncStream<DataResult> { continuation in
            results.forEach { continuation.yield($0) }
            continuation.finish()
        }
        let consentStream = AsyncStream<UsageConsent> { continuation in
            consents.forEach { continuation.yield($0) }
            continuation.finish()
        }
        return SettingsViewModel(
            observeUserContext: stream,
            observeUsageConsent: consentStream,
            setUsageConsent: setUsageConsent,
            setMyPlayer: setMyPlayer,
            setMyClub: setMyClub,
            playerLoaders: playerLoaders,
            searchClubs: { _ in clubs },
            supportUrl: supportUrl,
            analytics: analytics,
            searchSettle: searchSettle
        )
    }
}
