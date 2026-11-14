import Foundation
import Shared
import Testing

@testable import MobTT

@MainActor
struct PlayersViewModelTests {
    private let associations = [Association(id: "a1", name: "Wiener Tischtennis-Verband", code: "WTTV"), Association(id: "a2", name: "Kärntner Tischtennisverband", code: "KTTV")]
    private let clubs = [Club(id: "c1", name: "Alpha TTC", associationId: "a1"), Club(id: "c2", name: "Zebra TTC", associationId: "a1")]
    private let lineup = [LineupPlayer(playerId: "p1", name: "Anna Gruber", rcPoints: 1500, ageGroup: nil), LineupPlayer(playerId: "p2", name: "Zoe Zeller", rcPoints: nil, ageGroup: nil)]

    @Test func startsWithNothingChosenAndTheClubRowDisabled() {
        let state = makeViewModel().uiState

        #expect(state.associationName == nil)
        #expect(!state.isClubEnabled)
        #expect(state.list == .noClub)
        #expect(state.sheet == nil)
    }

    @Test func theClubRowDoesNothingWithoutAnAssociation() {
        let viewModel = makeViewModel()

        viewModel.onAction(.clubRowClicked)

        #expect(viewModel.uiState.sheet == nil)
    }

    @Test func theAssociationSheetListsAssociationsAndFiltersThem() async {
        let viewModel = makeViewModel()

        viewModel.onAction(.associationRowClicked)
        await waitUntil { viewModel.uiState.sheet?.content != .loading }
        viewModel.onAction(.sheetQueryChanged("kttv"))

        #expect(viewModel.uiState.sheet?.kind == .association)
        #expect(entryIds(viewModel) == ["a2"])
    }

    @Test func aFailedSheetLoadShowsTheErrorAndRetryRecovers() async {
        var failing = true
        let viewModel = makeViewModel(loadAssociations: { failing ? self.unavailable : self.success(self.associations) })

        viewModel.onAction(.associationRowClicked)
        await waitUntil { viewModel.uiState.sheet?.content != .loading }
        #expect(viewModel.uiState.sheet?.content == .error)

        failing = false
        viewModel.onAction(.sheetRetryClicked)
        await waitUntil { viewModel.uiState.sheet?.content != .loading }

        #expect(entryIds(viewModel) == ["a1", "a2"])
    }

    @Test func choosingAnAssociationClosesTheSheetAndEnablesTheClubRow() async {
        let viewModel = await chooseAssociation(makeViewModel())

        #expect(viewModel.uiState.associationName == "WTTV")
        #expect(viewModel.uiState.isClubEnabled)
        #expect(viewModel.uiState.sheet == nil)
    }

    @Test func theClubSheetListsOnlyTheChosenAssociationsClubs() async {
        var asked: [String] = []
        let viewModel = await chooseAssociation(makeViewModel(loadClubs: { asked.append($0); return self.success(self.clubs) }))

        viewModel.onAction(.clubRowClicked)
        await waitUntil { viewModel.uiState.sheet?.content != .loading }

        #expect(asked == ["a1"])
        #expect(entryIds(viewModel) == ["c1", "c2"])
    }

    @Test func choosingAClubLoadsItsPlayersAndTypingFiltersThem() async {
        let viewModel = await chooseClub(await chooseAssociation(makeViewModel()))
        await waitUntil { viewModel.uiState.list != .loading }
        #expect(playerIds(viewModel) == ["p1", "p2"])

        viewModel.onAction(.queryChanged("zel"))

        #expect(playerIds(viewModel) == ["p2"])
        #expect(viewModel.uiState.clubName == "Alpha TTC")
    }

    @Test func unavailablePlayersShowTheErrorAndRetryReloads() async {
        var failing = true
        let viewModel = await chooseClub(await chooseAssociation(makeViewModel(loadPlayers: { _ in failing ? self.unavailable : self.success(self.lineup) })))
        await waitUntil { viewModel.uiState.list != .loading }
        #expect(viewModel.uiState.list == .error)

        failing = false
        viewModel.onAction(.retryClicked)
        await waitUntil { viewModel.uiState.list != .loading }

        #expect(playerIds(viewModel) == ["p1", "p2"])
    }

    @Test func changingTheAssociationClearsTheClubAndTheSearch() async {
        let viewModel = await chooseClub(await chooseAssociation(makeViewModel()))
        await waitUntil { viewModel.uiState.list != .loading }
        viewModel.onAction(.queryChanged("zel"))

        viewModel.onAction(.associationRowClicked)
        viewModel.onAction(.sheetEntryClicked(id: "a2"))

        let state = viewModel.uiState
        #expect(state.associationName == "KTTV")
        #expect(state.clubName == nil)
        #expect(state.query == "")
        #expect(state.list == .noClub)
    }

    @Test func changingTheClubClearsTheSearch() async {
        let viewModel = await chooseClub(await chooseAssociation(makeViewModel()))
        await waitUntil { viewModel.uiState.list != .loading }
        viewModel.onAction(.queryChanged("zel"))

        viewModel.onAction(.clubRowClicked)
        viewModel.onAction(.sheetEntryClicked(id: "c2"))

        #expect(viewModel.uiState.clubName == "Zebra TTC")
        #expect(viewModel.uiState.query == "")
    }

    @Test func tappingAPlayerRemembersThemWithTheirClubAndOpensThem() async {
        var remembered: [Player] = []
        let viewModel = await chooseClub(await chooseAssociation(makeViewModel(rememberPlayer: { remembered.append($0) })))
        await waitUntil { viewModel.uiState.list != .loading }
        var iterator = viewModel.events.makeAsyncIterator()

        viewModel.onAction(.playerClicked(playerId: "p1", name: "Anna Gruber"))
        let event = await iterator.next()

        #expect(remembered.map { "\($0.id)/\($0.name)/\($0.clubId)" } == ["p1/Anna Gruber/c1"])
        guard case .openPlayer(let playerId)? = event else {
            Issue.record("expected openPlayer")
            return
        }
        #expect(playerId == "p1")
    }

    @Test func inMyPlayerModeTappingAPlayerSavesThemAsMyPlayerAndFinishes() async {
        var saved: [String] = []
        var remembered: [String] = []
        let viewModel = await chooseClub(await chooseAssociation(makeViewModel(
            mode: .myPlayer,
            rememberPlayer: { remembered.append($0.id) },
            setMyPlayer: { saved.append($0) }
        )))
        await waitUntil { viewModel.uiState.list != .loading }
        var iterator = viewModel.events.makeAsyncIterator()

        viewModel.onAction(.playerClicked(playerId: "p2", name: "Zoe Zeller"))
        let event = await iterator.next()

        #expect(remembered == ["p2"])
        #expect(saved == ["p2"])
        #expect(event == .done)
    }

    @Test func inMyClubModeTheFlowEndsAtTheClubWithoutLoadingPlayers() async {
        var saved: [String] = []
        var playersLoaded = false
        let viewModel = await chooseAssociation(makeViewModel(
            mode: .myClub,
            loadPlayers: { _ in playersLoaded = true; return self.success(self.lineup) },
            setMyClub: { saved.append($0) }
        ))
        var iterator = viewModel.events.makeAsyncIterator()

        let finished = await chooseClub(viewModel)
        let event = await iterator.next()

        #expect(!finished.uiState.showsPlayers)
        #expect(finished.uiState.clubName == "Alpha TTC")
        #expect(saved == ["c1"])
        #expect(event == .done)
        #expect(!playersLoaded)
    }

    @Test func inPickClubModeTheClubIsHandedBackAndNothingIsSaved() async {
        var saved: [String] = []
        let viewModel = await chooseAssociation(makeViewModel(mode: .pickClub, setMyClub: { saved.append($0) }))
        var iterator = viewModel.events.makeAsyncIterator()

        _ = await chooseClub(viewModel)
        let event = await iterator.next()

        #expect(event == .clubChosen(clubId: "c1"))
        #expect(saved.isEmpty)
    }

    private func chooseAssociation(_ viewModel: PlayersViewModel) async -> PlayersViewModel {
        viewModel.onAction(.associationRowClicked)
        await waitUntil { viewModel.uiState.sheet?.content != .loading }
        viewModel.onAction(.sheetEntryClicked(id: "a1"))
        return viewModel
    }

    private func chooseClub(_ viewModel: PlayersViewModel) async -> PlayersViewModel {
        viewModel.onAction(.clubRowClicked)
        await waitUntil { viewModel.uiState.sheet?.content != .loading }
        viewModel.onAction(.sheetEntryClicked(id: "c1"))
        return viewModel
    }

    private func entryIds(_ viewModel: PlayersViewModel) -> [String] {
        guard case .entries(let entries)? = viewModel.uiState.sheet?.content else { return [] }
        return entries.map(\.id)
    }

    private func playerIds(_ viewModel: PlayersViewModel) -> [String] {
        guard case .players(let players) = viewModel.uiState.list else { return [] }
        return players.map(\.id)
    }

    @Test func browsingReportsTheScreenAndThePlayerOpened() async {
        let analytics = RecordingAnalyticsTracker()
        let viewModel = await chooseClub(await chooseAssociation(makeViewModel(analytics: analytics)))
        await waitUntil { viewModel.uiState.list != .loading }

        viewModel.screenShown()
        viewModel.onAction(.playerClicked(playerId: "p1", name: "Anna Gruber"))
        await waitUntil { analytics.events.count == 2 }

        #expect(analytics.events == [
            LoggedEvent("screen_view", ["screen": "players"]),
            LoggedEvent("detail_opened", ["kind": "player", "id": "p1", "from": "players"]),
        ])
    }

    @Test func choosingMyPlayerReportsTheChoiceFromHome() async {
        let analytics = RecordingAnalyticsTracker()
        let viewModel = await chooseClub(await chooseAssociation(makeViewModel(mode: .myPlayer, analytics: analytics)))
        await waitUntil { viewModel.uiState.list != .loading }

        viewModel.screenShown()
        viewModel.onAction(.playerClicked(playerId: "p2", name: "Zoe Zeller"))
        await waitUntil { analytics.events.count == 2 }

        #expect(analytics.events == [
            LoggedEvent("screen_view", ["screen": "choose_player"]),
            LoggedEvent("my_player_selected", ["source": "home"]),
        ])
    }

    private func makeViewModel(
        mode: PlayersMode = .browse,
        loadAssociations: (() async throws -> DataResult)? = nil,
        loadClubs: ((String) async throws -> DataResult)? = nil,
        loadPlayers: ((String) async throws -> DataResult)? = nil,
        rememberPlayer: @escaping (Player) async throws -> Void = { _ in },
        setMyPlayer: @escaping (String) async throws -> Void = { _ in },
        setMyClub: @escaping (String) async throws -> Void = { _ in },
        analytics: any AnalyticsTracker = NoOpAnalyticsTracker()
    ) -> PlayersViewModel {
        PlayersViewModel(
            mode: mode,
            loadAssociations: loadAssociations ?? { self.success(self.associations) },
            loadClubs: loadClubs ?? { _ in self.success(self.clubs) },
            loadPlayers: loadPlayers ?? { _ in self.success(self.lineup) },
            rememberPlayer: rememberPlayer,
            setMyPlayer: setMyPlayer,
            setMyClub: setMyClub,
            analytics: analytics
        )
    }

    private func success(_ value: [Any]) -> DataResult { DataResultSuccess(value: value as NSArray) }
    private var unavailable: DataResult { DataResultUnavailable(error: DataUnavailable(message: "down", cause: nil)) }
}
