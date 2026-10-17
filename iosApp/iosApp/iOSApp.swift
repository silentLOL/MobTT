import SwiftUI
import Shared

@main
struct iOSApp: App {
    @State private var platformName = ""

    private static var environmentKey: String {
        Bundle.main.object(forInfoDictionaryKey: "BatEnvironment") as? String ?? ""
    }

    init() {
        KoinHelper.shared.start(environmentKey: Self.environmentKey)
    }

    var body: some Scene {
        WindowGroup {
            ContentView(platformName: platformName)
                .task {
                    platformName = (try? await KoinHelper.shared.getPlatformNameUseCase().invoke()) ?? ""
                }
        }
    }
}
