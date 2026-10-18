import Observation
import Shared

@MainActor
@Observable
final class SettingsViewModel {
    private var state = SettingsUiState()
    private var pickerKind: PickerKind?
    private var pickerQuery = ""
    private var pickerEntries: [DirectoryEntryUi] = []
    private var currentPlayerId: String?
    private var currentClubId: String?
    private var searchTask: Task<Void, Never>?

    @ObservationIgnored private let observeUserContext: any AsyncSequence<DataResult, Never>
    @ObservationIgnored private let setMyPlayer: (String) async throws -> Void
    @ObservationIgnored private let setMyClub: (String) async throws -> Void
    @ObservationIgnored private let searchPlayers: (String) async -> [PlayerSearchResult]
    @ObservationIgnored private let searchClubs: (String) async -> [Club]
    @ObservationIgnored private let eventStream = AsyncStream<SettingsEvent>.makeStream()

    var events: AsyncStream<SettingsEvent> { eventStream.stream }

    init(
        observeUserContext: any AsyncSequence<DataResult, Never> = KoinHelper.shared.observeUserContext(),
        setMyPlayer: @escaping (String) async throws -> Void = { try await KoinHelper.shared.getSetMyPlayerUseCase().invoke(playerId: $0) },
        setMyClub: @escaping (String) async throws -> Void = { try await KoinHelper.shared.getSetMyClubUseCase().invoke(clubId: $0) },
        searchPlayers: @escaping (String) async -> [PlayerSearchResult] = { (try? await KoinHelper.shared.getSearchPlayersUseCase().invoke(query: $0)) ?? [] },
        searchClubs: @escaping (String) async -> [Club] = { (try? await KoinHelper.shared.getSearchClubsUseCase().invoke(query: $0)) ?? [] }
    ) {
        self.observeUserContext = observeUserContext
        self.setMyPlayer = setMyPlayer
        self.setMyClub = setMyClub
        self.searchPlayers = searchPlayers
        self.searchClubs = searchClubs
    }

    var uiState: SettingsUiState {
        var result = state
        result.picker = pickerKind.map { kind in
            let currentId = kind == .player ? currentPlayerId : currentClubId
            let entries = pickerEntries.map {
                DirectoryEntryUi(id: $0.id, title: $0.title, subtitle: $0.subtitle, selected: $0.id == currentId)
            }
            return SettingsPickerUi(kind: kind, query: pickerQuery, entries: entries)
        }
        return result
    }

    /// Runs until the calling task is cancelled.
    func observe() async {
        for await result in observeUserContext {
            switch onEnum(of: result) {
            case .success(let success): apply((success.value as? UserContext) ?? UserContextNone.shared)
            // Home shows the failure; Settings falls back to nothing chosen until a later emission.
            case .unavailable: apply(UserContextNone.shared)
            }
        }
    }

    func onAction(_ action: SettingsAction) {
        switch action {
        case .choosePlayerClicked:
            openPicker(.player)
        case .chooseClubClicked:
            openPicker(.club)
        case .pickerQueryChanged(let query):
            pickerQuery = query
            search()
        case .pickerEntryClicked(let id):
            choose(id)
        case .pickerBackClicked:
            closePicker()
        case .doneClicked:
            eventStream.continuation.yield(.dismiss)
        }
    }

    private func openPicker(_ kind: PickerKind) {
        pickerKind = kind
        pickerQuery = ""
        // A result computed for the other kind must not be shown, or its ids would be saved as the wrong choice.
        pickerEntries = []
        search()
    }

    private func closePicker() {
        searchTask?.cancel()
        pickerKind = nil
    }

    private func search() {
        guard let kind = pickerKind else { return }
        let query = pickerQuery
        searchTask?.cancel()
        searchTask = Task {
            let entries = await entries(for: kind, query: query)
            guard !Task.isCancelled else { return }
            pickerEntries = entries
        }
    }

    private func entries(for kind: PickerKind, query: String) async -> [DirectoryEntryUi] {
        switch kind {
        case .player:
            let results = await searchPlayers(query)
            return results.map { DirectoryEntryUi(id: $0.player.id, title: $0.player.name, subtitle: $0.clubName, selected: false) }
        case .club:
            let clubs = await searchClubs(query)
            return clubs.map { DirectoryEntryUi(id: $0.id, title: $0.name, subtitle: nil, selected: false) }
        }
    }

    private func choose(_ id: String) {
        guard let kind = pickerKind else { return }
        Task {
            do {
                switch kind {
                case .player: try await setMyPlayer(id)
                case .club: try await setMyClub(id)
                }
                closePicker()
            } catch {
                // The picker stays open so the choice can be retried.
            }
        }
    }

    private func apply(_ context: UserContext) {
        currentPlayerId = nil
        currentClubId = nil
        switch onEnum(of: context) {
        case .none:
            state = SettingsUiState()
        case .myPlayer(let myPlayer):
            currentPlayerId = myPlayer.player.id
            state = SettingsUiState(
                myPlayerName: myPlayer.player.name,
                derivedFrom: .player,
                derivedClubName: myPlayer.club.name,
                derivedAssociationName: myPlayer.association.name
            )
        case .myClub(let myClub):
            currentClubId = myClub.club.id
            state = SettingsUiState(
                myClubName: myClub.club.name,
                derivedFrom: .club,
                derivedClubName: myClub.club.name,
                derivedAssociationName: myClub.association.name
            )
        }
    }
}
