import Shared
import Testing

@testable import BatMate

@MainActor
struct ClubViewModelTests {
    private let club = Club(id: "1", name: "Nussdorf TTC", associationId: "w")
    private let association = Association(id: "w", name: "WTTV")

    @Test func dataUnavailableShowsTheErrorState() async {
        let viewModel = ClubViewModel(source: .specific(clubId: "1")) { _ in unavailable }

        await viewModel.observe()

        #expect(viewModel.uiState == .error)
    }

    @Test func clubWithoutTeamsIsContentNotAnError() async {
        let overview = ClubOverview(club: club, association: association, teams: [])
        let viewModel = ClubViewModel(source: .specific(clubId: "1")) { _ in DataResultSuccess(value: overview) }

        await viewModel.observe()

        #expect(viewModel.uiState == .content(ClubOverviewUi(clubName: "Nussdorf TTC", associationName: "WTTV", teams: [])))
    }

    @Test func unknownClubShowsTheErrorState() async {
        let viewModel = ClubViewModel(source: .specific(clubId: "1")) { _ in DataResultSuccess<ClubOverview>(value: nil) }

        await viewModel.observe()

        #expect(viewModel.uiState == .error)
    }

    @Test func retryRestartsObservationAndSuccessClearsTheError() async {
        var failures = 1
        let overview = ClubOverview(club: club, association: association, teams: [])
        let viewModel = ClubViewModel(source: .specific(clubId: "1")) { _ in
            if failures > 0 {
                failures -= 1
                return unavailable
            }
            return DataResultSuccess(value: overview)
        }

        await viewModel.observe()
        #expect(viewModel.uiState == .error)

        viewModel.onAction(.retryClicked)
        #expect(viewModel.retryCount == 1)
        await viewModel.observe()

        #expect(viewModel.uiState == .content(ClubOverviewUi(clubName: "Nussdorf TTC", associationName: "WTTV", teams: [])))
    }

    @Test func myClubLoadsTheClubOfTheChosenPlayer() async {
        let context = UserContextMyPlayer(player: Player(id: "p1", name: "Anna Gruber", clubId: "1"), club: club, association: association)
        var requested: [String] = []
        let viewModel = makeMineViewModel(contexts: [DataResultSuccess(value: context)]) { id in
            requested.append(id)
            return DataResultSuccess(value: ClubOverview(club: club, association: association, teams: []))
        }

        await viewModel.observe()
        await waitUntil { viewModel.uiState != .loading }

        #expect(requested == ["1"])
        #expect(viewModel.uiState == .content(ClubOverviewUi(clubName: "Nussdorf TTC", associationName: "WTTV", teams: [])))
    }

    @Test func myClubWithNothingChosenShowsThePromptToChoose() async {
        let viewModel = makeMineViewModel(contexts: [DataResultSuccess(value: UserContextNone.shared)])

        await viewModel.observe()

        #expect(viewModel.uiState == .noClub)
    }

    @Test func myClubWithUnavailableUserContextShowsTheErrorNotThePrompt() async {
        let viewModel = makeMineViewModel(contexts: [unavailable])

        await viewModel.observe()

        #expect(viewModel.uiState == .error)
    }

    @Test func myClubFollowsAChangedSelection() async {
        let context = UserContextMyClub(club: club, association: association)
        let viewModel = makeMineViewModel(contexts: [DataResultSuccess(value: context), DataResultSuccess(value: UserContextNone.shared)])

        await viewModel.observe()

        #expect(viewModel.uiState == .noClub)
    }

    private func makeMineViewModel(
        contexts: [DataResult],
        loadOverview: @escaping (String) async throws -> DataResult = { _ in DataResultUnavailable(error: DataUnavailable(message: "down", cause: nil)) }
    ) -> ClubViewModel {
        ClubViewModel(
            source: .mine,
            observeUserContext: AsyncStream { continuation in
                contexts.forEach { continuation.yield($0) }
                continuation.finish()
            },
            loadOverview: loadOverview
        )
    }

    private func waitUntil(_ condition: () -> Bool) async {
        for _ in 0..<100 where !condition() {
            await Task.yield()
        }
    }

    private var unavailable: DataResult { DataResultUnavailable(error: DataUnavailable(message: "down", cause: nil)) }
}
