import FirebaseAnalytics
import FirebaseCore
import FirebaseCrashlytics

struct FirebaseAnalyticsTracker: AnalyticsTracker {
    func logEvent(_ name: String, parameters: [String: Any]) {
        Analytics.logEvent(name, parameters: parameters)
    }

    func setCollectionEnabled(_ enabled: Bool) {
        Analytics.setAnalyticsCollectionEnabled(enabled)
    }
}

struct FirebaseCrashReporter: CrashReporter {
    func recordNonFatal(_ error: any Error) {
        Crashlytics.crashlytics().record(error: error)
    }

    func log(_ message: String) {
        Crashlytics.crashlytics().log(message)
    }

    func setCollectionEnabled(_ enabled: Bool) {
        Crashlytics.crashlytics().setCrashlyticsCollectionEnabled(enabled)
    }
}
