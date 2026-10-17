import SwiftUI

struct PlayersScreen: View {
    var body: some View {
        ScrollView {
            BatEmptyStateView(
                icon: .search,
                title: String(localized: "players_empty_title"),
                message: String(localized: "players_empty_body")
            )
        }
    }
}

#Preview {
    BatPreviewMatrix {
        PlayersScreen()
    }
}
