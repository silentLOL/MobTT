/// Reports crashes and handled failures. Messages must never contain tokens or the XTTV API key.
protocol CrashReporter {
    func recordNonFatal(_ error: any Error)

    func log(_ message: String)

    /// Switches collection on or off; driven by Usage Consent.
    func setCollectionEnabled(_ enabled: Bool)
}
