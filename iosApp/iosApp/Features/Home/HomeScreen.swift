import SwiftUI

struct HomeScreen: View {
    var body: some View {
        ScrollView {
            BatEmptyStateView(
                icon: .home,
                title: String(localized: "home_empty_title"),
                message: String(localized: "home_empty_body")
            )
        }
    }
}

#Preview {
    BatPreviewMatrix {
        HomeScreen()
    }
}
