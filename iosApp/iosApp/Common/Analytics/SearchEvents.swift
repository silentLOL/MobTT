import Foundation

/// Sends one `search_performed` once typing has paused, so a query is counted once and not per keystroke.
@MainActor
final class SearchEvents {
    private let tracker: any AnalyticsTracker
    private let screen: String
    private let settle: Duration
    private var pending: Task<Void, Never>?

    init(tracker: any AnalyticsTracker, screen: String, settle: Duration = .seconds(1)) {
        self.tracker = tracker
        self.screen = screen
        self.settle = settle
    }

    func queryChanged(_ query: String) {
        pending?.cancel()
        guard !query.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty else { return }
        pending = Task { [tracker, screen, settle] in
            do {
                try await Task.sleep(for: settle)
            } catch {
                return
            }
            tracker.searchPerformed(screen: screen, query: query)
        }
    }
}

/// Sends `screen_view` once per screen, however often the view asks.
@MainActor
final class ScreenViewReporter {
    private let tracker: any AnalyticsTracker
    private let screen: String
    private var reported = false

    init(tracker: any AnalyticsTracker, screen: String) {
        self.tracker = tracker
        self.screen = screen
    }

    func report() {
        guard !reported else { return }
        reported = true
        tracker.screenView(screen)
    }
}
