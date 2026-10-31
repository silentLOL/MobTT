import FirebaseCore
import Foundation
import Shared

/// Starts Firebase when the build carries a `GoogleService-Info.plist` and hands out the tracker and crash reporter.
/// Without the plist the SDK is never configured and both are no-ops.
@MainActor
enum AppAnalytics {
    private(set) static var tracker: any AnalyticsTracker = NoOpAnalyticsTracker()
    private(set) static var crashReporter: any CrashReporter = NoOpCrashReporter()

    /// Call before starting Koin: shared code logs to `crashReporter` from its first line.
    /// `deb` sends no Analytics events; Crashlytics still reports to the `deb` Firebase app.
    static func configure(environmentKey: String) {
        guard Bundle.main.path(forResource: "GoogleService-Info", ofType: "plist") != nil else { return }
        FirebaseApp.configure()
        if environmentKey != "deb" {
            tracker = FirebaseAnalyticsTracker()
        }
        crashReporter = FirebaseCrashReporter()
    }

    /// Call after starting Koin: collection follows Usage Consent, at launch and on every change.
    static func observeConsent() {
        let collection = UsageConsentCollection(
            allowed: KoinHelper.shared.getObserveUsageConsentUseCase().invoke().map { $0 == .allowed },
            tracker: tracker,
            crashReporter: crashReporter
        )
        Task { await collection.run() }
    }

    static var kotlinCrashRecorder: KotlinCrashRecorder {
        KotlinCrashRecorder(reporter: crashReporter)
    }
}
