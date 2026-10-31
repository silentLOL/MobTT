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
    private var usageConsentAllowed = false
    private var searchTask: Task<Void, Never>?
    private let playerDrillDown: PlayerDrillDown

    @ObservationIgnored private let observeUserContext: any AsyncSequence<DataResult, Never>
    @ObservationIgnored private let observeUsageConsent: any AsyncSequence<UsageConsent, Never>
    @ObservationIgnored private let setUsageConsent: (Bool) async throws -> Void
    @ObservationIgnored private let setMyPlayer: (String) async throws -> Void
    @ObservationIgnored private let setMyClub: (String) async throws -> Void
    @ObservationIgnored private let searchClubs: (String) async -> [Club]
    @ObservationIgnored private let eventStream = AsyncStream<SettingsEvent>.makeStream()

    var events: AsyncStream<SettingsEvent> { eventStream.stream }

    init(
        observeUserContext: any AsyncSequence<DataResult, Never> = KoinHelper.shared.observeUserContext(),
        observeUsageConsent: any AsyncSequence<UsageConsent, Never> = KoinHelper.shared.getObserveUsageConsentUseCase().invoke(),
        setUsageConsent: @escaping (Bool) async throws -> Void = { try await KoinHelper.shared.getSetUsageConsentUseCase().invoke(allowed: $0) },
        setMyPlayer: @escaping (String) async throws -> Void = { try await KoinHelper.shared.getSetMyPlayerUseCase().invoke(playerId: $0) },
        setMyClub: @escaping (String) async throws -> Void = { try await KoinHelper.shared.getSetMyClubUseCase().invoke(clubId: $0) },
        playerLoaders: PlayerDrillDownLoaders = .live,
        searchClubs: @escaping (String) async -> [Club] = { (try? await KoinHelper.shared.getSearchClubsUseCase().invoke(query: $0)) ?? [] }
    ) {
        self.observeUserContext = observeUserContext
        self.observeUsageConsent = observeUsageConsent
        self.setUsageConsent = setUsageConsent
        self.setMyPlayer = setMyPlayer
        self.setMyClub = setMyClub
        self.playerDrillDown = PlayerDrillDown(loaders: playerLoaders)
        self.searchClubs = searchClubs
    }

    var uiState: SettingsUiState {
        var result = state
        result.usageConsentAllowed = usageConsentAllowed
        result.picker = pickerKind.map { kind in
            if kind == .player {
                let drillDown = playerDrillDown.ui { $0 == currentPlayerId }
                return SettingsPickerUi(kind: kind, query: drillDown.query, entries: drillDown.entries, playerDrillDown: drillDown)
            }
            let currentId = currentClubId
            let entries = pickerEntries.map {
                DirectoryEntryUi(id: $0.id, title: $0.title, subtitle: $0.subtitle, selected: $0.id == currentId)
            }
            return SettingsPickerUi(kind: kind, query: pickerQuery, entries: entries)
        }
        return result
    }

    /// Runs until the calling task is cancelled.
    func observe() async {
        await withTaskGroup(of: Void.self) { group in
            group.addTask { @MainActor in await self.observeContext() }
            group.addTask { @MainActor in await self.observeConsent() }
        }
    }

    private func observeContext() async {
        for await result in observeUserContext {
            switch onEnum(of: result) {
            case .success(let success): apply((success.value as? UserContext) ?? UserContextNone.shared)
            // Home shows the failure; Settings falls back to nothing chosen until a later emission.
            case .unavailable: apply(UserContextNone.shared)
            }
        }
    }

    private func observeConsent() async {
        for await consent in observeUsageConsent {
            usageConsentAllowed = consent == .allowed
        }
    }

    func onAction(_ action: SettingsAction) {
        switch action {
        case .choosePlayerClicked:
            openPicker(.player)
        case .chooseClubClicked:
            openPicker(.club)
        case .pickerQueryChanged(let query):
            if pickerKind == .player {
                playerDrillDown.setQuery(query)
            } else {
                pickerQuery = query
                search()
            }
        case .pickerEntryClicked(let id):
            if pickerKind == .player {
                if case .player(let playerId) = playerDrillDown.select(id) {
                    choose(playerId)
                }
            } else {
                choose(id)
            }
        case .pickerBackClicked:
            if pickerKind == .player, playerDrillDown.back() { return }
            closePicker()
        case .usageConsentToggled(let allowed):
            Task { try? await setUsageConsent(allowed) }
        case .doneClicked:
            eventStream.continuation.yield(.dismiss)
        }
    }

    private func openPicker(_ kind: PickerKind) {
        pickerKind = kind
        pickerQuery = ""
        // A result computed for the other kind must not be shown, or its ids would be saved as the wrong choice.
        pickerEntries = []
        if kind == .player {
            playerDrillDown.start()
        } else {
            search()
        }
    }

    private func closePicker() {
        searchTask?.cancel()
        playerDrillDown.cancel()
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
            // Players are browsed through the drill-down, never searched.
            return []
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
                derivedAssociationName: myPlayer.association.code
            )
        case .myClub(let myClub):
            currentClubId = myClub.club.id
            state = SettingsUiState(
                myClubName: myClub.club.name,
                derivedFrom: .club,
                derivedClubName: myClub.club.name,
                derivedAssociationName: myClub.association.code
            )
        }
    }
}
