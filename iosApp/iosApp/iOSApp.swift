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

    /// Nil while unset: Settings then shows no "Buy me a coffee" row.
    private static var supportUrl: String? {
        Bundle.main.object(forInfoDictionaryKey: "BatSupportUrl") as? String
    }

    init() {
        AppAnalytics.configure(environmentKey: Self.environmentKey)
        KoinHelper.shared.start(
            environmentKey: Self.environmentKey,
            appVersion: Self.appVersion,
            xttvApiKey: Self.xttvApiKey,
            supportUrl: Self.supportUrl,
            crashRecorder: AppAnalytics.kotlinCrashRecorder
        )
        AppAnalytics.observeConsent()
    }

    var body: some Scene {
        WindowGroup {
            ShellScreen(appearance: $appearance)
                .preferredColorScheme(appearance.colorScheme)
        }
    }
}
