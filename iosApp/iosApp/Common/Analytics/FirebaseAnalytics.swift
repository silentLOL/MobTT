import FirebaseAnalytics
import FirebaseCore
import FirebaseCrashlytics
import Foundation

struct FirebaseAnalyticsTracker: AnalyticsTracker {
    func logEvent(_ name: String, parameters: [String: Any]) {
        Analytics.logEvent(name, parameters: parameters)
    }

    func setCollectionEnabled(_ enabled: Bool) {
        Analytics.setAnalyticsCollectionEnabled(enabled)
    }
}

/// Drops everything until Usage Consent allows it, so nothing logged before the answer is sent later.
final class FirebaseCrashReporter: CrashReporter, @unchecked Sendable {
    private let lock = NSLock()
    private var enabled = false

    private var isEnabled: Bool {
        lock.withLock { enabled }
    }

    func recordNonFatal(_ error: any Error) {
        guard isEnabled else { return }
        Crashlytics.crashlytics().record(error: error)
    }

    func recordKotlinException(name: String, reason: String, frames: [String]) {
        guard isEnabled else { return }
        let model = ExceptionModel(name: name, reason: reason)
        model.stackTrace = frames.map { StackFrame(symbol: $0, file: "", line: 0) }
        Crashlytics.crashlytics().record(exceptionModel: model)
    }

    func log(_ message: String) {
        guard isEnabled else { return }
        Crashlytics.crashlytics().log(message)
    }

    func setCollectionEnabled(_ enabled: Bool) {
        lock.withLock { self.enabled = enabled }
        Crashlytics.crashlytics().setCrashlyticsCollectionEnabled(enabled)
        if !enabled {
            Crashlytics.crashlytics().deleteUnsentReports()
        }
    }
}
