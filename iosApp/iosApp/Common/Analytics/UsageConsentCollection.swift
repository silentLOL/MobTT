/// Keeps SDK collection in step with Usage Consent: off until the user allows it, off again when they decline.
struct UsageConsentCollection {
    let allowed: any AsyncSequence<Bool, Never>
    let tracker: any AnalyticsTracker
    let crashReporter: any CrashReporter

    /// Runs until the calling task is cancelled.
    func run() async {
        var last: Bool?
        for await isAllowed in allowed where isAllowed != last {
            last = isAllowed
            tracker.setCollectionEnabled(isAllowed)
            crashReporter.setCollectionEnabled(isAllowed)
        }
    }
}
