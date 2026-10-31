/// Sends interaction events to analytics. Parameters carry IDs and enum values only, never names or free text.
protocol AnalyticsTracker {
    func logEvent(_ name: String, parameters: [String: Any])

    /// Switches collection on or off; driven by Usage Consent.
    func setCollectionEnabled(_ enabled: Bool)
}

extension AnalyticsTracker {
    func logEvent(_ name: String) {
        logEvent(name, parameters: [:])
    }
}
