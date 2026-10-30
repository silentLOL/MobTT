import SwiftUI

struct TeamDetailRoute: View {
    @State private var viewModel: TeamDetailViewModel

    init(teamId: String) {
        _viewModel = State(initialValue: TeamDetailViewModel(teamId: teamId))
    }

    var body: some View {
        TeamDetailScreen(state: viewModel.uiState, onAction: viewModel.onAction)
            .onAppear { viewModel.loadIfNeeded() }
            .task { await viewModel.observe() }
    }
}

struct TeamDetailScreen: View {
    let state: TeamDetailUiState
    let onAction: (TeamDetailAction) -> Void

    var body: some View {
        switch state {
        case .loading:
            ProgressView()
                .tint(BatColor.brand)
                .accessibilityLabel(Text("team_detail_loading_cd"))
                .frame(maxWidth: .infinity, maxHeight: .infinity)
        case .error:
            ScrollView {
                VStack(spacing: BatSpacing.md) {
                    BatEmptyStateView(
                        icon: .club,
                        title: String(localized: "team_detail_error_title"),
                        message: String(localized: "team_detail_error_body")
                    )
                    BatPrimaryButtonView(title: String(localized: "team_detail_error_retry_button")) {
                        onAction(.retryClicked)
                    }
                }
                .padding(BatSpacing.lg)
            }
            .onAppear { AccessibilityNotification.Announcement(String(localized: "team_detail_error_body")).post() }
        case .content(let detail):
            ContentView(detail: detail, onAction: onAction)
        }
    }
}

private struct ContentView: View {
    let detail: TeamDetailUi
    let onAction: (TeamDetailAction) -> Void

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: BatSpacing.lg) {
                BatHeroCardView {
                    HStack(alignment: .top, spacing: BatSpacing.md) {
                        VStack(alignment: .leading, spacing: BatSpacing.sm) {
                            BatHeroPillView(text: String(localized: "team_detail_hero_overline"))
                            Text(detail.teamName)
                                .batTextStyle(BatTypography.display)
                                .foregroundStyle(BatColor.onHero)
                            Text(detail.clubName)
                                .batTextStyle(BatTypography.body)
                                .foregroundStyle(BatColor.onHero)
                        }
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .accessibilityElement(children: .combine)
                        BatFavoriteButtonView(
                            isFavorite: detail.isFavorite,
                            accessibilityText: detail.isFavorite
                                ? String(localized: "team_detail_favorite_remove_cd \(detail.teamName)")
                                : String(localized: "team_detail_favorite_add_cd \(detail.teamName)"),
                            accessibilityState: detail.isFavorite
                                ? String(localized: "core_favorite_on_state")
                                : String(localized: "core_favorite_off_state"),
                            action: { onAction(.favoriteClicked) }
                        )
                    }
                }
                VStack(alignment: .leading, spacing: 0) {
                    BatSectionHeaderView(title: detail.leagueName ?? String(localized: "team_detail_standing_title"))
                    if detail.standings.isEmpty {
                        SectionMessage(key: "team_detail_standing_empty_body")
                    } else {
                        BatCardView(padding: BatSpacing.sm) {
                            VStack(spacing: 0) {
                                ForEach(detail.standings) { StandingRowView(row: $0) }
                            }
                        }
                    }
                }
                VStack(alignment: .leading, spacing: 0) {
                    BatSectionHeaderView(title: String(localized: "team_detail_lineup_title"))
                    if detail.lineup.isEmpty {
                        SectionMessage(key: "team_detail_lineup_empty_body")
                    } else {
                        BatCardView(padding: BatSpacing.sm) {
                            VStack(spacing: 0) {
                                ForEach(Array(detail.lineup.enumerated()), id: \.element.id) { index, player in
                                    if index > 0 {
                                        Divider().overlay(BatColor.border)
                                    }
                                    LineupRowView(player: player)
                                }
                            }
                        }
                    }
                }
            }
            .padding(BatSpacing.lg)
        }
    }
}

private struct SectionMessage: View {
    let key: LocalizedStringKey

    var body: some View {
        Text(key)
            .batTextStyle(BatTypography.body)
            .foregroundStyle(BatColor.textSecondary)
    }
}

#Preview("Loading") {
    BatPreviewMatrix {
        TeamDetailScreen(state: .loading, onAction: { _ in })
    }
}

#Preview("Error") {
    BatPreviewMatrix {
        TeamDetailScreen(state: .error, onAction: { _ in })
    }
}

#Preview("Loaded") {
    BatPreviewMatrix {
        TeamDetailScreen(state: .content(TeamDetailPreviewData.loaded), onAction: { _ in })
    }
}

#Preview("Favorite") {
    BatPreviewMatrix {
        TeamDetailScreen(state: .content(TeamDetailPreviewData.favorite), onAction: { _ in })
    }
}

#Preview("Empty lineup") {
    BatPreviewMatrix {
        TeamDetailScreen(state: .content(TeamDetailPreviewData.emptyLineup), onAction: { _ in })
    }
}

#Preview("No League") {
    BatPreviewMatrix {
        TeamDetailScreen(state: .content(TeamDetailPreviewData.noLeague), onAction: { _ in })
    }
}

#Preview("Long names") {
    BatPreviewMatrix {
        TeamDetailScreen(state: .content(TeamDetailPreviewData.longNames), onAction: { _ in })
    }
}
