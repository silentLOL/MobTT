/// Reports crashes and handled failures. Messages must never contain tokens or the XTTV API key.
protocol CrashReporter {
    func recordNonFatal(_ error: any Error)

    /// An exception from shared Kotlin; [frames] are Kotlin stack trace lines.
    func recordKotlinException(name: String, reason: String, frames: [String])

    func log(_ message: String)

    /// Switches collection on or off; driven by Usage Consent.
    func setCollectionEnabled(_ enabled: Bool)
}
