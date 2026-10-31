import SwiftUI

struct TeamDetailRoute: View {
    @State private var viewModel: TeamDetailViewModel

    let onOpenLeague: (_ leagueId: String, _ teamId: String) -> Void
    let onOpenPlayer: (_ playerId: String) -> Void

    init(
        teamId: String,
        onOpenLeague: @escaping (_ leagueId: String, _ teamId: String) -> Void,
        onOpenPlayer: @escaping (_ playerId: String) -> Void
    ) {
        self.onOpenLeague = onOpenLeague
        self.onOpenPlayer = onOpenPlayer
        _viewModel = State(initialValue: TeamDetailViewModel(teamId: teamId))
    }

    var body: some View {
        TeamDetailScreen(state: viewModel.uiState, onAction: viewModel.onAction)
            .onAppear { viewModel.screenShown() }
            .onAppear { viewModel.loadIfNeeded() }
            .task { await viewModel.observe() }
            .task {
                for await event in viewModel.events {
                    switch event {
                    case .openLeague(let leagueId, let teamId): onOpenLeague(leagueId, teamId)
                    case .openPlayer(let playerId): onOpenPlayer(playerId)
                    }
                }
            }
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
                    ZStack(alignment: .topTrailing) {
                        VStack(alignment: .leading, spacing: BatSpacing.sm) {
                            BatHeroPillRowView(text: String(localized: "team_detail_hero_overline"))
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
                    BatSectionHeaderView(title: String(localized: "team_detail_league_title"))
                    if let league = detail.league {
                        TeamLeagueCardView(league: league, onTap: { onAction(.leagueClicked(leagueId: league.id)) })
                    } else {
                        SectionMessage(key: "team_detail_league_empty_body")
                    }
                }
                VStack(alignment: .leading, spacing: 0) {
                    BatSectionHeaderView(title: String(localized: "team_detail_lineup_title"))
                    if detail.lineup.isEmpty {
                        SectionMessage(key: "team_detail_lineup_empty_body")
                    } else {
                        BatListView {
                            ForEach(Array(detail.lineup.enumerated()), id: \.element.id) { index, player in
                                BatListRowView(
                                    title: player.name,
                                    trailingText: player.rcPoints.map { String(localized: "team_detail_lineup_rc_label \($0)") },
                                    isStriped: !index.isMultiple(of: 2),
                                    action: { onAction(.playerClicked(playerId: player.id, name: player.name)) }
                                )
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

#Preview("Free week") {
    BatPreviewMatrix {
        TeamDetailScreen(state: .content(TeamDetailPreviewData.freeWeek), onAction: { _ in })
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
