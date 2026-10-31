import Testing

@testable import MobTT

@MainActor
struct UsageConsentCollectionTests {
    @Test func collectionFollowsConsent() async {
        let tracker = RecordingTracker()
        let crashReporter = RecordingCrashReporter()
        let collection = UsageConsentCollection(
            allowed: [false, true, true, false].asStream,
            tracker: tracker,
            crashReporter: crashReporter
        )

        await collection.run()

        #expect(tracker.states == [false, true, false])
        #expect(crashReporter.states == [false, true, false])
    }
}

private final class RecordingTracker: AnalyticsTracker, @unchecked Sendable {
    var states: [Bool] = []

    func logEvent(_ name: String, parameters: [String: Any]) {}

    func setCollectionEnabled(_ enabled: Bool) { states.append(enabled) }
}

private final class RecordingCrashReporter: CrashReporter, @unchecked Sendable {
    var states: [Bool] = []

    func recordNonFatal(_ error: any Error) {}

    func log(_ message: String) {}

    func setCollectionEnabled(_ enabled: Bool) { states.append(enabled) }
}

private extension Array {
    var asStream: AsyncStream<Element> {
        AsyncStream { continuation in
            forEach { continuation.yield($0) }
            continuation.finish()
        }
    }
}
