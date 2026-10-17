import SwiftUI

struct LiveScreen: View {
    var body: some View {
        ScrollView {
            BatEmptyStateView(
                icon: .live,
                title: String(localized: "live_empty_title"),
                message: String(localized: "live_empty_body")
            )
        }
    }
}

#Preview {
    BatPreviewMatrix {
        LiveScreen()
    }
}
