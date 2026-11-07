import Foundation
import Shared
import Testing

@testable import MobTT

@MainActor
struct LeagueTabViewModelTests {
    private let anna = Player(id: "p1", name: "Anna", clubId: "c1")

    @Test func withoutMyPlayerTheTabAsksToChooseOne() async {
        let viewModel = makeViewModel([DataResultSuccess(value: MyTeamsNoMyPlayer.shared)])

        await viewModel.observe()

        #expect(viewModel.uiState == .noMyPlayer)
    }

    @Test func oneTeamOpensItsLeagueWithTheTeamHighlighted() async {
        let viewModel = makeViewModel([found([teamDetail("t1", leagueId: "l1")])])

        await viewModel.observe()

        #expect(viewModel.uiState == .league(leagueId: "l1", teamId: "t1"))
    }

    @Test func noLineupNamingMyPlayerSaysNoTeamWasFound() async {
        let viewModel = makeViewModel([found([])])

        await viewModel.observe()

        #expect(viewModel.uiState == .noTeam(playerId: "p1", playerName: "Anna"))
    }

    @Test func theTabFollowsAChangeOfMyPlayer() async {
        let viewModel = makeViewModel([found([teamDetail("t1", leagueId: "l1")]), DataResultSuccess(value: MyTeamsNoMyPlayer.shared)])

        await viewModel.observe()

        #expect(viewModel.uiState == .noMyPlayer)
    }

    @Test func unavailableDataShowsAnErrorAndRetryRestarts() async {
        let viewModel = makeViewModel([DataResultUnavailable(error: DataUnavailable(message: "down", cause: nil))])

        await viewModel.observe()
        #expect(viewModel.uiState == .unavailable)

        viewModel.onAction(.retryClicked)
        #expect(viewModel.retryCount == 1)
    }

    @Test func theEmptyStateButtonOpensThePlayerPicker() async {
        let viewModel = makeViewModel([])
        var iterator = viewModel.events.makeAsyncIterator()

        viewModel.onAction(.choosePlayerClicked)

        guard case .openChoosePlayer = await iterator.next() else {
            Issue.record("Expected an openChoosePlayer event")
            return
        }
    }

    @Test func theNoTeamLinkOpensMyPlayer() async {
        let viewModel = makeViewModel([])
        var iterator = viewModel.events.makeAsyncIterator()

        viewModel.onAction(.myPlayerClicked(playerId: "p1"))

        guard case .openPlayer(let playerId) = await iterator.next() else {
            Issue.record("Expected an openPlayer event")
            return
        }
        #expect(playerId == "p1")
    }

    @Test func reportsTheScreenView() {
        let analytics = RecordingAnalyticsTracker()
        let viewModel = makeViewModel([], analytics: analytics)

        viewModel.screenShown()

        #expect(analytics.events == [LoggedEvent("screen_view", ["screen": "league"])])
    }

    private func found(_ teams: [TeamDetail]) -> DataResult {
        DataResultSuccess(value: MyTeamsFound(player: anna, teams: teams))
    }

    private func teamDetail(_ teamId: String, leagueId: String) -> TeamDetail {
        TeamDetail(
            team: Team(id: teamId, name: "Olympic", clubId: "c1", leagueId: leagueId),
            clubName: "TTC Olympic",
            league: League(id: leagueId, name: "Liga"),
            standing: nil,
            nextFixture: NextFixtureFreeWeek.shared,
            lineup: []
        )
    }

    private func makeViewModel(_ results: [DataResult], analytics: any AnalyticsTracker = NoOpAnalyticsTracker()) -> LeagueTabViewModel {
        LeagueTabViewModel(
            observeMyTeams: {
                AsyncStream { continuation in
                    results.forEach { continuation.yield($0) }
                    continuation.finish()
                }
            },
            analytics: analytics
        )
    }
}
