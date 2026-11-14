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
    @ObservationIgnored private let analytics: any AnalyticsTracker
    @ObservationIgnored private let screenViews: ScreenViewReporter
    @ObservationIgnored private let eventStream = EventChannel<LeagueTabEvent>()

    var events: AsyncStream<LeagueTabEvent> { eventStream.stream }

    init(
        observeMyTeams: @escaping () -> any AsyncSequence<DataResult, Never> = { KoinHelper.shared.observeMyTeams() },
        analytics: any AnalyticsTracker = AppAnalytics.tracker
    ) {
        self.observeMyTeams = observeMyTeams
        self.analytics = analytics
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
            eventStream.yield(.openChoosePlayer)
        case .myPlayerClicked(let playerId):
            eventStream.yield(.openPlayer(playerId: playerId))
        case .teamCardClicked(let leagueId, let teamId):
            analytics.detailOpened(kind: AnalyticsKind.league, id: leagueId, from: AnalyticsScreen.league)
            eventStream.yield(.openLeague(leagueId: leagueId, teamId: teamId))
        }
    }

    private static func state(of myTeams: MyTeams) -> LeagueTabUiState {
        switch onEnum(of: myTeams) {
        case .noMyPlayer:
            return .noMyPlayer
        case .unavailable:
            return .unavailable
        case .found(let found):
            let cards = found.teams.compactMap { detail in
                detail.leagueUi().map { LeagueTeamCardUi(teamId: detail.team.id, teamName: detail.team.name, league: $0) }
            }
            switch cards.count {
            case 0: return .noTeam(playerId: found.player.id, playerName: found.player.name)
            case 1: return .league(leagueId: cards[0].league.id, teamId: cards[0].teamId)
            default: return .teams(cards)
            }
        }
    }
}
