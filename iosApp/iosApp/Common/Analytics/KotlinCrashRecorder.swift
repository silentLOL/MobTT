import Foundation
import Shared

/// Receives errors and logs from shared Kotlin (its Kermit writer and unhandled-exception hook).
final class KotlinCrashRecorder: NSObject, IosCrashRecorder {
    private let reporter: any CrashReporter

    init(reporter: any CrashReporter) {
        self.reporter = reporter
    }

    func recordNonFatal(name: String, reason: String, frames: [String]) {
        reporter.recordKotlinException(name: name, reason: reason, frames: frames)
    }

    func log(message: String) {
        reporter.log(message)
    }
}
