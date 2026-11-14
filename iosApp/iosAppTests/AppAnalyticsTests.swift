import Testing

@testable import MobTT

struct AppAnalyticsTests {
    @Test func debRecordsNothingBecauseItsTrackerIsTheNoOp() {
        let firebase = RecordingAnalyticsTracker()

        let tracker = AppAnalytics.chooseTracker(environmentKey: "deb") { firebase }
        tracker.screenView(AnalyticsScreen.home)

        #expect(tracker is NoOpAnalyticsTracker)
        #expect(firebase.events.isEmpty)
    }

    @Test(arguments: ["dev", "beta", "prod"])
    func otherEnvironmentsUseTheFirebaseTracker(environmentKey: String) {
        let firebase = RecordingAnalyticsTracker()

        AppAnalytics.chooseTracker(environmentKey: environmentKey) { firebase }.screenView(AnalyticsScreen.home)

        #expect(firebase.events.count == 1)
    }
}
