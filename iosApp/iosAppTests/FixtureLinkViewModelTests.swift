import Testing

@testable import MobTT

@MainActor
struct FixtureLinkViewModelTests {
    @Test func aFixtureLinkOpensItsFixtureAndReportsAColdStart() async {
        let analytics = RecordingAnalyticsTracker()
        let viewModel = makeViewModel(analytics: analytics)
        var iterator = viewModel.events.makeAsyncIterator()

        viewModel.onAction(.linkReceived(url: "https://links.mobtt.example/f/464821", coldStart: true))

        guard case .openFixture(let fixtureId) = await iterator.next() else {
            Issue.record("Expected an openFixture event")
            return
        }
        #expect(fixtureId == "464821")
        #expect(analytics.events == [LoggedEvent("fixture_link_opened", ["start": "cold"])])
    }

    @Test func aFixtureLinkWhileRunningIsReportedAsRunning() {
        let analytics = RecordingAnalyticsTracker()
        let viewModel = makeViewModel(analytics: analytics)

        viewModel.onAction(.linkReceived(url: "https://links.mobtt.example/f/464821", coldStart: false))

        #expect(analytics.events == [LoggedEvent("fixture_link_opened", ["start": "running"])])
    }

    @Test func anythingElseChangesNothing() {
        let analytics = RecordingAnalyticsTracker()
        let viewModel = makeViewModel(analytics: analytics)

        viewModel.onAction(.linkReceived(url: "https://links.mobtt.example/t/8846", coldStart: true))

        #expect(analytics.events.isEmpty)
    }

    /// Reads links like the shared `FixtureLinks`, which has its own tests.
    private func makeViewModel(analytics: RecordingAnalyticsTracker) -> FixtureLinkViewModel {
        FixtureLinkViewModel(
            fixtureIdOf: { url in
                let prefix = "https://links.mobtt.example/f/"
                return url.hasPrefix(prefix) ? String(url.dropFirst(prefix.count)) : nil
            },
            analytics: analytics
        )
    }
}
