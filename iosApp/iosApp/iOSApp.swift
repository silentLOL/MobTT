import SwiftUI
import Shared

@main
struct iOSApp: App {
    @AppStorage(AppearanceChoice.storageKey) private var appearance = AppearanceChoice.system

    private static var environmentKey: String {
        Bundle.main.object(forInfoDictionaryKey: "BatEnvironment") as? String ?? ""
    }

    init() {
        KoinHelper.shared.start(environmentKey: Self.environmentKey)
    }

    var body: some Scene {
        WindowGroup {
            ShellScreen(appearance: $appearance)
                .preferredColorScheme(appearance.colorScheme)
        }
    }
}
