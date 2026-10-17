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

    init() {
        KoinHelper.shared.start(environmentKey: Self.environmentKey, appVersion: Self.appVersion)
    }

    var body: some Scene {
        WindowGroup {
            ShellScreen(appearance: $appearance)
                .preferredColorScheme(appearance.colorScheme)
        }
    }
}
