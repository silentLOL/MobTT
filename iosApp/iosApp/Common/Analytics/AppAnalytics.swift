import FirebaseCore
import Foundation
import Shared

/// Starts Firebase when the build carries a `GoogleService-Info.plist` and hands out the tracker and crash reporter.
/// Without the plist the SDK is never configured and both are no-ops.
@MainActor
enum AppAnalytics {
    private(set) static var tracker: any AnalyticsTracker = NoOpAnalyticsTracker()
    private(set) static var crashReporter: any CrashReporter = NoOpCrashReporter()

    /// `deb` sends no Analytics events; Crashlytics still reports to the `deb` Firebase app.
    static func start(environmentKey: String) {
        guard Bundle.main.path(forResource: "GoogleService-Info", ofType: "plist") != nil else { return }
        FirebaseApp.configure()
        if environmentKey != "deb" {
            tracker = FirebaseAnalyticsTracker()
        }
        crashReporter = FirebaseCrashReporter()

        let collection = UsageConsentCollection(
            allowed: KoinHelper.shared.getObserveUsageConsentUseCase().invoke().map { $0 == .allowed },
            tracker: tracker,
            crashReporter: crashReporter
        )
        Task { await collection.run() }
    }
}
