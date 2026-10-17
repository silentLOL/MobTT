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

    private var unavailable: DataResult { DataResultUnavailable(error: DataUnavailable(message: "down", cause: nil)) }
}
