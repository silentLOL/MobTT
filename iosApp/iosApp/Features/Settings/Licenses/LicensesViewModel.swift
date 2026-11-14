import Foundation
import Observation
import os

/// Where the generated list comes from; the app bundle's `licenses.json` unless a test hands in another.
typealias LicensesSource = @Sendable () async throws -> Data

@MainActor
@Observable
final class LicensesViewModel {
    private(set) var uiState = LicensesUiState()

    @ObservationIgnored private let source: LicensesSource
    @ObservationIgnored private let screenViews: ScreenViewReporter

    init(source: @escaping LicensesSource = LicensesViewModel.bundledList, analytics: any AnalyticsTracker = AppAnalytics.tracker) {
        self.source = source
        self.screenViews = ScreenViewReporter(tracker: analytics, screen: AnalyticsScreen.licenses)
    }

    nonisolated static func bundledList() async throws -> Data {
        guard let url = Bundle.main.url(forResource: "licenses", withExtension: "json") else {
            throw CocoaError(.fileNoSuchFile)
        }
        return try Data(contentsOf: url)
    }

    func screenShown() { screenViews.report() }

    func onAction(_ action: LicensesAction) {
        switch action {
        case .entryClicked(let id):
            uiState.expandedId = id == uiState.expandedId ? nil : id
        }
    }

    /// Reads and parses the list off the main thread: a few hundred entries and their texts.
    func load() async {
        let entries: [LicenseEntry]
        do {
            let data = try await source()
            entries = try await Task.detached { try parseLicenseEntries(data) }.value
        } catch {
            Logger(subsystem: Bundle.main.bundleIdentifier ?? "MobTT", category: "Licenses")
                .error("The licenses list could not be read: \(error, privacy: .public)")
            entries = []
        }
        uiState.groups = Dictionary(grouping: entries, by: \.kind)
            .map { LicenseGroupUi(kind: $0.key, entries: $0.value) }
            .sorted { $0.kind < $1.kind }
        uiState.isLoading = false
    }
}
