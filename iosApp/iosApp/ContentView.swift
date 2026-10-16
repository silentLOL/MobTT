import SwiftUI

struct ContentView: View {
    let platformName: String

    var body: some View {
        Text(platformName)
    }
}

#Preview {
    ContentView(platformName: "iOS 26")
}
