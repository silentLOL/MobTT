import SwiftUI

struct PlayerDetailRoute: View {
    let onOpenClub: (String) -> Void
    let onOpenTeam: (String) -> Void

    @State private var viewModel: PlayerDetailViewModel

    init(playerId: String, onOpenClub: @escaping (String) -> Void, onOpenTeam: @escaping (String) -> Void) {
        self.onOpenClub = onOpenClub
        self.onOpenTeam = onOpenTeam
        _viewModel = State(initialValue: PlayerDetailViewModel(playerId: playerId))
    }

    var body: some View {
        PlayerDetailScreen(state: viewModel.uiState, onAction: viewModel.onAction)
            .onAppear { viewModel.screenShown() }
            .onAppear { viewModel.loadIfNeeded() }
            .task { await viewModel.observe() }
            .task {
                for await event in viewModel.events {
                    switch event {
                    case .openClub(let clubId): onOpenClub(clubId)
                    case .openTeam(let teamId): onOpenTeam(teamId)
                    }
                }
            }
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
            VStack(alignment: .leading, spacing: BatSpacing.lg) {
                HStack(alignment: .top, spacing: BatSpacing.sm) {
                    Text(detail.playerName)
                        .batTextStyle(BatTypography.display)
                        .foregroundStyle(BatColor.textPrimary)
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .accessibilityAddTraits(.isHeader)
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
                if detail.rcPoints != nil || detail.ageGroup != nil {
                    HStack(spacing: BatSpacing.lg) {
                        if let rcPoints = detail.rcPoints {
                            BatStatView(label: String(localized: "player_detail_rc_label"), value: String(rcPoints))
                        }
                        if let ageGroup = detail.ageGroup {
                            BatStatView(label: String(localized: "player_detail_age_group_label"), value: ageGroup)
                        }
                    }
                }
                if let clubName = detail.clubName {
                    VStack(alignment: .leading, spacing: BatSpacing.sm) {
                        BatSectionHeaderView(title: String(localized: "player_detail_club_title"))
                        LinkRowView(title: clubName, detail: detail.associationName) { onAction(.clubClicked) }
                    }
                }
                if !detail.teams.isEmpty {
                    VStack(alignment: .leading, spacing: BatSpacing.sm) {
                        BatSectionHeaderView(title: String(localized: "player_detail_teams_title"))
                        ForEach(detail.teams) { team in
                            LinkRowView(title: team.name, detail: nil) { onAction(.teamClicked(id: team.id)) }
                        }
                    }
                }
            }
            .padding(BatSpacing.lg)
        }
    }
}

private struct LinkRowView: View {
    let title: String
    let detail: String?
    let onTap: () -> Void

    var body: some View {
        BatCardView(padding: BatSpacing.md, onTap: onTap) {
            VStack(alignment: .leading, spacing: 0) {
                Text(title)
                    .batTextStyle(BatTypography.bodyStrong)
                    .foregroundStyle(BatColor.textPrimary)
                if let detail {
                    Text(detail)
                        .batTextStyle(BatTypography.caption)
                        .foregroundStyle(BatColor.textSecondary)
                }
            }
            .frame(maxWidth: .infinity, minHeight: BatSizing.minTouchTarget, alignment: .leading)
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

#Preview("No Teams") {
    BatPreviewMatrix {
        PlayerDetailScreen(state: .content(PlayerDetailPreviewData.noTeams), onAction: { _ in })
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
