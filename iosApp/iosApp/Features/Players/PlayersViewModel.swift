import Foundation
import Observation
import Shared

/// XTTV cannot search Players by name, so they are found Verband, then Club, then by typing: each list is read when
/// its step is entered and typing only filters it on the device.
@MainActor
@Observable
final class PlayersViewModel {
    private let mode: PlayersMode

    private struct Choice {
        let id: String
        let name: String
    }

    private enum Loaded<Value> {
        case idle
        case loading
        case failed
        case ready(Value)
    }

    private var association: Choice?
    private var club: Choice?
    private var query = ""
    private var sheetKind: PlayersSheetKind?
    private var sheetQuery = ""
    private var associations: Loaded<[Choice]> = .idle
    private var clubs: Loaded<[Choice]> = .idle
    private var players: Loaded<[LineupPlayer]> = .idle

    @ObservationIgnored private var sheetTask: Task<Void, Never>?
    @ObservationIgnored private var playersTask: Task<Void, Never>?
    @ObservationIgnored private let loadAssociations: () async throws -> DataResult
    @ObservationIgnored private let loadClubs: (String) async throws -> DataResult
    @ObservationIgnored private let loadPlayers: (String) async throws -> DataResult
    @ObservationIgnored private let rememberPlayer: (Player) async throws -> Void
    @ObservationIgnored private let setMyPlayer: (String) async throws -> Void
    @ObservationIgnored private let setMyClub: (String) async throws -> Void
    @ObservationIgnored private let analytics: any AnalyticsTracker
    @ObservationIgnored private let screen: String
    @ObservationIgnored private let screenViews: ScreenViewReporter
    @ObservationIgnored private let searchEvents: SearchEvents
    @ObservationIgnored private let eventStream = AsyncStream<PlayersEvent>.makeStream()

    var events: AsyncStream<PlayersEvent> { eventStream.stream }

    init(
        mode: PlayersMode = .browse,
        loadAssociations: @escaping () async throws -> DataResult = { try await KoinHelper.shared.listAssociations() },
        loadClubs: @escaping (String) async throws -> DataResult = { try await KoinHelper.shared.listAssociationClubs(associationId: $0) },
        loadPlayers: @escaping (String) async throws -> DataResult = { try await KoinHelper.shared.listClubPlayers(clubId: $0) },
        rememberPlayer: @escaping (Player) async throws -> Void = { try await KoinHelper.shared.getRememberPlayerUseCase().invoke(player: $0) },
        setMyPlayer: @escaping (String) async throws -> Void = { try await KoinHelper.shared.getSetMyPlayerUseCase().invoke(playerId: $0) },
        setMyClub: @escaping (String) async throws -> Void = { try await KoinHelper.shared.getSetMyClubUseCase().invoke(clubId: $0) },
        analytics: any AnalyticsTracker = AppAnalytics.tracker,
        searchSettle: Duration = .seconds(1)
    ) {
        self.mode = mode
        self.loadAssociations = loadAssociations
        self.loadClubs = loadClubs
        self.loadPlayers = loadPlayers
        self.rememberPlayer = rememberPlayer
        self.setMyPlayer = setMyPlayer
        self.setMyClub = setMyClub
        self.analytics = analytics
        let screen = switch mode {
        case .browse: AnalyticsScreen.players
        case .myPlayer: AnalyticsScreen.choosePlayer
        case .myClub, .pickClub: AnalyticsScreen.chooseClub
        }
        self.screen = screen
        self.screenViews = ScreenViewReporter(tracker: analytics, screen: screen)
        self.searchEvents = SearchEvents(tracker: analytics, screen: screen, settle: searchSettle)
    }

    /// Reports the screen view, once per screen.
    func screenShown() {
        screenViews.report()
    }

    var uiState: PlayersUiState {
        PlayersUiState(
            associationName: association?.name,
            clubName: club?.name,
            query: query,
            list: listUi,
            showsPlayers: mode.showsPlayers,
            sheet: sheetKind.map { PlayersSheetUi(kind: $0, query: sheetQuery, content: sheetContent($0)) }
        )
    }

    func onAction(_ action: PlayersAction) {
        switch action {
        case .associationRowClicked: openSheet(.association)
        case .clubRowClicked: if association != nil { openSheet(.club) }
        case .sheetQueryChanged(let text):
            sheetQuery = text
            searchEvents.queryChanged(text)
        case .sheetEntryClicked(let id): choose(id: id)
        case .sheetRetryClicked: if let sheetKind { loadSheet(sheetKind, force: true) }
        case .sheetDismissed: closeSheet()
        case .queryChanged(let text):
            query = text
            searchEvents.queryChanged(text)
        case .playerClicked(let playerId, let name): openPlayer(playerId: playerId, name: name)
        case .retryClicked: if let club { loadClubPlayers(club) }
        }
    }

    private var listUi: PlayersListUi {
        guard club != nil else { return .noClub }
        switch players {
        case .idle, .loading: return .loading
        case .failed: return .error
        case .ready(let all):
            let matching = all.filter { query.isEmpty || $0.name.localizedCaseInsensitiveContains(query) }
            return .players(matching.map { PlayerRowUi(id: $0.playerId, name: $0.name, rcPoints: $0.rcPoints?.intValue) })
        }
    }

    private func sheetContent(_ kind: PlayersSheetKind) -> PlayersSheetContent {
        let (state, selected) = kind == .association ? (associations, association?.id) : (clubs, club?.id)
        switch state {
        case .idle, .loading: return .loading
        case .failed: return .error
        case .ready(let all):
            let matching = all.filter { sheetQuery.isEmpty || $0.name.localizedCaseInsensitiveContains(sheetQuery) }
            return .entries(matching.map { DirectoryEntryUi(id: $0.id, title: $0.name, subtitle: nil, selected: $0.id == selected) })
        }
    }

    private func openSheet(_ kind: PlayersSheetKind) {
        sheetKind = kind
        sheetQuery = ""
        loadSheet(kind, force: false)
    }

    private func closeSheet() {
        sheetTask?.cancel()
        sheetKind = nil
        sheetQuery = ""
    }

    private func loadSheet(_ kind: PlayersSheetKind, force: Bool) {
        switch kind {
        case .association:
            if case .ready = associations, !force { return }
            associations = .loading
            sheetTask?.cancel()
            sheetTask = Task { [loadAssociations] in
                let result = await Self.read(loadAssociations) { ($0 as? [Association])?.map { Choice(id: $0.id, name: $0.code) } }
                guard !Task.isCancelled else { return }
                associations = result
            }
        case .club:
            guard let association else { return }
            if case .ready = clubs, !force { return }
            clubs = .loading
            sheetTask?.cancel()
            sheetTask = Task { [loadClubs] in
                let result = await Self.read({ try await loadClubs(association.id) }) { ($0 as? [Club])?.map { Choice(id: $0.id, name: $0.name) } }
                guard !Task.isCancelled else { return }
                clubs = result
            }
        }
    }

    private func choose(id: String) {
        switch sheetKind {
        case .association:
            guard case .ready(let all) = associations, let entry = all.first(where: { $0.id == id }) else { return }
            if association?.id != entry.id {
                association = entry
                clubs = .idle
                clearClub()
            }
        case .club:
            guard case .ready(let all) = clubs, let entry = all.first(where: { $0.id == id }) else { return }
            if !mode.showsPlayers {
                club = entry
                chooseClub(entry)
            } else if club?.id != entry.id {
                club = entry
                query = ""
                loadClubPlayers(entry)
            }
        case nil:
            return
        }
        closeSheet()
    }

    private func clearClub() {
        playersTask?.cancel()
        club = nil
        query = ""
        players = .idle
    }

    private func loadClubPlayers(_ club: Choice) {
        players = .loading
        playersTask?.cancel()
        playersTask = Task { [loadPlayers] in
            let result = await Self.read({ try await loadPlayers(club.id) }) { ($0 as? [LineupPlayer]) }
            guard !Task.isCancelled else { return }
            players = result
        }
    }

    /// The Club is the end of the flow: it is either saved as My Club or handed to the caller.
    private func chooseClub(_ club: Choice) {
        Task {
            if mode == .myClub {
                guard (try? await setMyClub(club.id)) != nil else { return }
                analytics.myClubSelected(source: AnalyticsSource.home)
                eventStream.continuation.yield(.done)
            } else {
                eventStream.continuation.yield(.clubChosen(clubId: club.id))
            }
        }
    }

    private func openPlayer(playerId: String, name: String) {
        guard let club else { return }
        Task {
            if mode == .myPlayer {
                // A failed save keeps the screen open so the Player can be tapped again.
                guard (try? await rememberPlayer(Player(id: playerId, name: name, clubId: club.id))) != nil,
                      (try? await setMyPlayer(playerId)) != nil else { return }
                analytics.myPlayerSelected(source: AnalyticsSource.home)
                eventStream.continuation.yield(.done)
            } else {
                try? await rememberPlayer(Player(id: playerId, name: name, clubId: club.id))
                analytics.detailOpened(kind: AnalyticsKind.player, id: playerId, from: screen)
                eventStream.continuation.yield(.openPlayer(playerId: playerId))
            }
        }
    }

    /// A rejected read, or a payload of the wrong shape, is a failed load; cancellation never reaches the caller's state.
    private static func read<Value>(_ load: () async throws -> DataResult, map: (Any?) -> Value?) async -> Loaded<Value> {
        guard let result = try? await load() else { return .failed }
        switch onEnum(of: result) {
        case .success(let success): return map(success.value).map { .ready($0) } ?? .failed
        case .unavailable: return .failed
        }
    }
}
