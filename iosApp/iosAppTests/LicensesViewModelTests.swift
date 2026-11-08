import Foundation
import Testing

@testable import MobTT

@MainActor
struct LicensesViewModelTests {
    private let list = Data("""
        {"licenses": {}, "entries": [
          {"id": "font:inter", "name": "Inter", "kind": "font", "licenseName": "OFL", "text": "OFL text"},
          {"id": "x:b", "name": "B", "kind": "library", "licenseName": "MIT", "text": "MIT text"},
          {"id": "x:a", "name": "A", "kind": "library", "licenseName": "MIT", "text": "MIT text"}
        ]}
        """.utf8)

    private func makeViewModel(
        analytics: RecordingAnalyticsTracker = RecordingAnalyticsTracker(),
        source: @escaping LicensesSource
    ) -> LicensesViewModel {
        LicensesViewModel(source: source, analytics: analytics)
    }

    @Test func startsLoading() {
        let viewModel = makeViewModel { Data() }

        #expect(viewModel.uiState.isLoading)
    }

    @Test func loadedEntriesAreGroupedAsLibrariesThenFonts() async {
        let viewModel = makeViewModel { [list] in list }

        await viewModel.load()

        #expect(!viewModel.uiState.isLoading)
        #expect(viewModel.uiState.groups.map(\.kind) == [.library, .font])
        #expect(viewModel.uiState.groups.first?.entries.map(\.name) == ["A", "B"])
    }

    @Test func anEmptyListIsLoadedWithNoGroups() async {
        let viewModel = makeViewModel { Data(#"{"licenses": {}, "entries": []}"#.utf8) }

        await viewModel.load()

        #expect(!viewModel.uiState.isLoading)
        #expect(viewModel.uiState.groups.isEmpty)
    }

    @Test func anUnreadableListShowsTheEmptyStateInsteadOfLoading() async {
        struct Unreadable: Error {}
        let viewModel = makeViewModel { throw Unreadable() }

        await viewModel.load()

        #expect(!viewModel.uiState.isLoading)
        #expect(viewModel.uiState.groups.isEmpty)
    }

    @Test func tappingAnEntryOpensItAndTappingItAgainClosesIt() async {
        let viewModel = makeViewModel { [list] in list }
        await viewModel.load()

        viewModel.onAction(.entryClicked(id: "x:a"))
        #expect(viewModel.uiState.expandedId == "x:a")

        viewModel.onAction(.entryClicked(id: "x:a"))
        #expect(viewModel.uiState.expandedId == nil)
    }

    @Test func openingAnotherEntryClosesTheFirst() async {
        let viewModel = makeViewModel { [list] in list }
        await viewModel.load()

        viewModel.onAction(.entryClicked(id: "x:a"))
        viewModel.onAction(.entryClicked(id: "x:b"))

        #expect(viewModel.uiState.expandedId == "x:b")
    }

    @Test func screenViewIsSentOnceForLicenses() {
        let analytics = RecordingAnalyticsTracker()
        let viewModel = makeViewModel(analytics: analytics) { Data() }

        viewModel.screenShown()
        viewModel.screenShown()

        #expect(analytics.events == [LoggedEvent("screen_view", ["screen": "licenses"])])
    }
}
