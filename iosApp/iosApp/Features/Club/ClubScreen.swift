import SwiftUI

struct ClubScreen: View {
    var body: some View {
        ScrollView {
            BatEmptyStateView(
                icon: .club,
                title: String(localized: "club_empty_title"),
                message: String(localized: "club_empty_body")
            )
        }
    }
}

#Preview {
    BatPreviewMatrix {
        ClubScreen()
    }
}
