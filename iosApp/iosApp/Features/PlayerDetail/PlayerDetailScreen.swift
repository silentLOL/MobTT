import SwiftUI

struct PlayerDetailRoute: View {
    @State private var viewModel: PlayerDetailViewModel

    init(playerId: String) {
        _viewModel = State(initialValue: PlayerDetailViewModel(playerId: playerId))
    }

    var body: some View {
        PlayerDetailScreen(state: viewModel.uiState, onAction: viewModel.onAction)
            .onAppear { viewModel.loadIfNeeded() }
            .task { await viewModel.observe() }
    }
}

struct PlayerDetailScreen: View {
    let state: PlayerDetailUiState
    let onAction: (PlayerDetailAction) -> Void

    var body: some View {
        switch state {
        case .loading:
            ProgressView()
                .tint(BatColor.brand)
                .accessibilityLabel(Text("player_detail_loading_cd"))
                .frame(maxWidth: .infinity, maxHeight: .infinity)
        case .unavailable:
            ScrollView {
                BatEmptyStateView(
                    icon: .search,
                    title: String(localized: "player_detail_unavailable_title"),
                    message: String(localized: "player_detail_unavailable_body")
                )
                .padding(BatSpacing.lg)
            }
            .onAppear { AccessibilityNotification.Announcement(String(localized: "player_detail_unavailable_body")).post() }
        case .content(let detail):
            ContentView(detail: detail, onAction: onAction)
        }
    }
}

private struct ContentView: View {
    let detail: PlayerDetailUi
    let onAction: (PlayerDetailAction) -> Void

    var body: some View {
        ScrollView {
            BatHeroCardView {
                HStack(alignment: .top, spacing: BatSpacing.md) {
                    VStack(alignment: .leading, spacing: BatSpacing.sm) {
                        BatHeroPillView(text: String(localized: "player_detail_hero_overline"))
                        Text(detail.playerName)
                            .batTextStyle(BatTypography.display)
                            .foregroundStyle(BatColor.onHero)
                        if let clubName = detail.clubName {
                            Text(clubName)
                                .batTextStyle(BatTypography.body)
                                .foregroundStyle(BatColor.onHero)
                        }
                        if let associationName = detail.associationName {
                            Text(associationName)
                                .batTextStyle(BatTypography.caption)
                                .foregroundStyle(BatColor.onHero)
                        }
                    }
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .accessibilityElement(children: .combine)
                    BatFavoriteButtonView(
                        isFavorite: detail.isFavorite,
                        accessibilityText: detail.isFavorite
                            ? String(localized: "player_detail_favorite_remove_cd \(detail.playerName)")
                            : String(localized: "player_detail_favorite_add_cd \(detail.playerName)"),
                        accessibilityState: detail.isFavorite
                            ? String(localized: "core_favorite_on_state")
                            : String(localized: "core_favorite_off_state"),
                        action: { onAction(.favoriteClicked) }
                    )
                }
            }
            .padding(BatSpacing.lg)
        }
    }
}

#Preview("Loading") {
    BatPreviewMatrix {
        PlayerDetailScreen(state: .loading, onAction: { _ in })
    }
}

#Preview("Unavailable") {
    BatPreviewMatrix {
        PlayerDetailScreen(state: .unavailable, onAction: { _ in })
    }
}

#Preview("Loaded") {
    BatPreviewMatrix {
        PlayerDetailScreen(state: .content(PlayerDetailPreviewData.loaded), onAction: { _ in })
    }
}

#Preview("Favorite") {
    BatPreviewMatrix {
        PlayerDetailScreen(state: .content(PlayerDetailPreviewData.favorite), onAction: { _ in })
    }
}

#Preview("No Club") {
    BatPreviewMatrix {
        PlayerDetailScreen(state: .content(PlayerDetailPreviewData.noClub), onAction: { _ in })
    }
}

#Preview("Long names") {
    BatPreviewMatrix {
        PlayerDetailScreen(state: .content(PlayerDetailPreviewData.longNames), onAction: { _ in })
    }
}
