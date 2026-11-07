import Foundation
import Observation
import Shared

/// The League tab: the League of My Player's Team, or why there is none.
@MainActor
@Observable
final class LeagueTabViewModel {
    private(set) var uiState: LeagueTabUiState = .loading
    /// Bumped by a retry; the route restarts `observe()` when it changes.
    private(set) var retryCount = 0

    @ObservationIgnored private let observeMyTeams: () -> any AsyncSequence<DataResult, Never>
    @ObservationIgnored private let screenViews: ScreenViewReporter
    @ObservationIgnored private let eventStream = AsyncStream<LeagueTabEvent>.makeStream()

    var events: AsyncStream<LeagueTabEvent> { eventStream.stream }

    init(
        observeMyTeams: @escaping () -> any AsyncSequence<DataResult, Never> = { KoinHelper.shared.observeMyTeams() },
        analytics: any AnalyticsTracker = AppAnalytics.tracker
    ) {
        self.observeMyTeams = observeMyTeams
        self.screenViews = ScreenViewReporter(tracker: analytics, screen: AnalyticsScreen.league)
    }

    /// Reports the screen view, once per screen.
    func screenShown() {
        screenViews.report()
    }

    /// Follows My Teams until the calling task is cancelled or XTTV cannot answer.
    func observe() async {
        uiState = .loading
        for await result in observeMyTeams() {
            switch onEnum(of: result) {
            case .success(let success):
                uiState = (success.value as? MyTeams).map(Self.state(of:)) ?? .unavailable
            case .unavailable:
                uiState = .unavailable
            }
        }
    }

    func onAction(_ action: LeagueTabAction) {
        switch action {
        case .retryClicked:
            retryCount += 1
        case .choosePlayerClicked:
            eventStream.continuation.yield(.openChoosePlayer)
        case .myPlayerClicked(let playerId):
            eventStream.continuation.yield(.openPlayer(playerId: playerId))
        }
    }

    private static func state(of myTeams: MyTeams) -> LeagueTabUiState {
        switch onEnum(of: myTeams) {
        case .noMyPlayer:
            return .noMyPlayer
        case .found(let found):
            guard let detail = found.teams.first, let league = detail.league else {
                return .noTeam(playerId: found.player.id, playerName: found.player.name)
            }
            return .league(leagueId: league.id, teamId: detail.team.id)
        }
    }
}
