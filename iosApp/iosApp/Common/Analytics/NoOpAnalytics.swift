/// Used in previews, tests, the Debug configuration and builds without Firebase config.
struct NoOpAnalyticsTracker: AnalyticsTracker {
    func logEvent(_ name: String, parameters: [String: Any]) {}

    func setCollectionEnabled(_ enabled: Bool) {}
}

struct NoOpCrashReporter: CrashReporter {
    func recordNonFatal(_ error: any Error) {}

    func log(_ message: String) {}

    func setCollectionEnabled(_ enabled: Bool) {}
}
