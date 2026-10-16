import SwiftUI
import Shared

@main
struct iOSApp: App {
    @State private var platformName = ""

    init() {
        KoinHelper.shared.start()
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
