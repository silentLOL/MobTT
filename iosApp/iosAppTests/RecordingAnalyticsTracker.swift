import Foundation

@testable import MobTT

struct LoggedEvent: Equatable {
    let name: String
    let parameters: [String: String]

    init(_ name: String, _ parameters: [String: String] = [:]) {
        self.name = name
        self.parameters = parameters
    }
}

/// Keeps what was logged, for ViewModel tests. Parameter values are compared as text.
final class RecordingAnalyticsTracker: AnalyticsTracker, @unchecked Sendable {
    private(set) var events: [LoggedEvent] = []

    func logEvent(_ name: String, parameters: [String: Any]) {
        events.append(LoggedEvent(name, parameters.mapValues { "\($0)" }))
    }

    func setCollectionEnabled(_ enabled: Bool) {}
}
