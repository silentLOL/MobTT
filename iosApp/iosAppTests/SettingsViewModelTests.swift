import Shared
import Testing

@testable import BatMate

@MainActor
struct SettingsViewModelTests {
    private let association = Association(id: "w", name: "WTTV")
    private let club = Club(id: "c1", name: "Nussdorf TTC", associationId: "w")

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
        for _ in 0..<100 where viewModel.uiState.picker?.entries.isEmpty ?? true {
            await Task.yield()
        }

        #expect(viewModel.uiState.picker?.entries == [DirectoryEntryUi(id: "c1", title: "Nussdorf TTC", subtitle: nil, selected: false)])
    }

    @Test func choosingAnEntrySavesItAndClosesThePicker() async {
        var saved: [String] = []
        let viewModel = makeViewModel(emitting: [], clubs: [club], setMyClub: { saved.append($0) })

        viewModel.onAction(.chooseClubClicked)
        viewModel.onAction(.pickerEntryClicked(id: "c1"))
        for _ in 0..<100 where viewModel.uiState.picker != nil {
            await Task.yield()
        }

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
        for _ in 0..<100 where attempts == 0 {
            await Task.yield()
        }

        #expect(attempts == 1)
        #expect(viewModel.uiState.picker?.kind == .club)
    }

    @Test func doneAsksTheSheetToDismiss() async {
        let viewModel = makeViewModel(emitting: [])
        var events = viewModel.events.makeAsyncIterator()

        viewModel.onAction(.doneClicked)

        #expect(await events.next() == .dismiss)
    }

    private func makeViewModel(
        emitting results: [DataResult],
        clubs: [Club] = [],
        setMyClub: @escaping (String) async throws -> Void = { _ in }
    ) -> SettingsViewModel {
        let stream = AsyncStream<DataResult> { continuation in
            results.forEach { continuation.yield($0) }
            continuation.finish()
        }
        return SettingsViewModel(
            observeUserContext: stream,
            setMyPlayer: { _ in },
            setMyClub: setMyClub,
            searchPlayers: { _ in [] },
            searchClubs: { _ in clubs }
        )
    }
}
