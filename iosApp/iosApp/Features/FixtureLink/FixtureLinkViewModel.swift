import Foundation
import Observation
import Shared

enum FixtureLinkAction {
    /// The app was opened with `url`; `coldStart` when the link started it rather than reaching it while running.
    case linkReceived(url: String, coldStart: Bool)
}

enum FixtureLinkEvent {
    case openFixture(fixtureId: String)
}

/// Turns a link the app was opened with into the Fixture it names; anything that is not a Fixture link is ignored.
@MainActor
@Observable
final class FixtureLinkViewModel {
    @ObservationIgnored private let eventStream = AsyncStream<FixtureLinkEvent>.makeStream()
    @ObservationIgnored private let fixtureIdOf: (String) -> String?
    @ObservationIgnored private let analytics: any AnalyticsTracker

    var events: AsyncStream<FixtureLinkEvent> { eventStream.stream }

    init(
        fixtureIdOf: @escaping (String) -> String? = { KoinHelper.shared.fixtureIdOfLink(url: $0) },
        analytics: any AnalyticsTracker = AppAnalytics.tracker
    ) {
        self.fixtureIdOf = fixtureIdOf
        self.analytics = analytics
    }

    func onAction(_ action: FixtureLinkAction) {
        switch action {
        case .linkReceived(let url, let coldStart):
            guard let fixtureId = fixtureIdOf(url) else { return }
            analytics.fixtureLinkOpened(start: coldStart ? AnalyticsStart.cold : AnalyticsStart.running)
            eventStream.continuation.yield(.openFixture(fixtureId: fixtureId))
        }
    }
}
