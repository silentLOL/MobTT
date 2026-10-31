import SwiftUI
import Shared

@main
struct iOSApp: App {
    @AppStorage(AppearanceChoice.storageKey) private var appearance = AppearanceChoice.system

    private static var environmentKey: String {
        Bundle.main.object(forInfoDictionaryKey: "BatEnvironment") as? String ?? ""
    }

    private static var appVersion: String {
        Bundle.main.object(forInfoDictionaryKey: "CFBundleShortVersionString") as? String ?? "0"
    }

    private static var xttvApiKey: String {
        Bundle.main.object(forInfoDictionaryKey: "BatXttvApiKey") as? String ?? ""
    }

    init() {
        KoinHelper.shared.start(
            environmentKey: Self.environmentKey,
            appVersion: Self.appVersion,
            xttvApiKey: Self.xttvApiKey
        )
        AppAnalytics.start(environmentKey: Self.environmentKey)
    }

    var body: some Scene {
        WindowGroup {
            ShellScreen(appearance: $appearance)
                .preferredColorScheme(appearance.colorScheme)
        }
    }
}
