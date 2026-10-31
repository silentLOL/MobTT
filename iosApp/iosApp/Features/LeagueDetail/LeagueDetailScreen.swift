import SwiftUI

struct LeagueDetailRoute: View {
    @State private var viewModel: LeagueDetailViewModel

    let onOpenTeam: (_ teamId: String) -> Void

    init(leagueId: String, highlightTeamId: String?, onOpenTeam: @escaping (_ teamId: String) -> Void) {
        self.onOpenTeam = onOpenTeam
        _viewModel = State(initialValue: LeagueDetailViewModel(leagueId: leagueId, highlightTeamId: highlightTeamId))
    }

    var body: some View {
        LeagueDetailScreen(state: viewModel.uiState, onAction: viewModel.onAction)
            .onAppear { viewModel.loadIfNeeded() }
            .task {
                for await event in viewModel.events {
                    switch event {
                    case .openTeam(let teamId): onOpenTeam(teamId)
                    }
                }
            }
    }
}

struct LeagueDetailScreen: View {
    let state: LeagueDetailUiState
    let onAction: (LeagueDetailAction) -> Void

    var body: some View {
        switch state {
        case .loading:
            ProgressView()
                .tint(BatColor.brand)
                .accessibilityLabel(Text("league_detail_loading_cd"))
                .frame(maxWidth: .infinity, maxHeight: .infinity)
        case .error:
            ScrollView {
                VStack(spacing: BatSpacing.md) {
                    BatEmptyStateView(
                        icon: .club,
                        title: String(localized: "league_detail_error_title"),
                        message: String(localized: "league_detail_error_body")
                    )
                    BatPrimaryButtonView(title: String(localized: "league_detail_error_retry_button")) {
                        onAction(.retryClicked)
                    }
                }
                .padding(BatSpacing.lg)
            }
            .onAppear { AccessibilityNotification.Announcement(String(localized: "league_detail_error_body")).post() }
        case .content(let detail):
            ContentView(detail: detail, onAction: onAction)
        }
    }
}

private struct ContentView: View {
    let detail: LeagueDetailUi
    let onAction: (LeagueDetailAction) -> Void

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: BatSpacing.lg) {
                BatHeroCardView {
                    VStack(alignment: .leading, spacing: BatSpacing.sm) {
                        BatHeroPillRowView(text: String(localized: "league_detail_hero_overline"))
                        Text(detail.leagueName)
                            .batTextStyle(BatTypography.display)
                            .foregroundStyle(BatColor.onHero)
                    }
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .accessibilityElement(children: .combine)
                }
                VStack(alignment: .leading, spacing: 0) {
                    BatSectionHeaderView(title: String(localized: "league_detail_standing_title"))
                    if detail.standings.isEmpty {
                        SectionMessage(key: "league_detail_standing_empty_body")
                    } else {
                        BatListView {
                            ForEach(Array(detail.standings.enumerated()), id: \.element.id) { index, row in
                                BatListRowView(
                                    title: row.teamName,
                                    leadingText: "\(row.position)",
                                    supportingText: String(localized: "team_detail_standing_stats_label \(row.played) \(row.points)"),
                                    badge: row.isThisTeam ? String(localized: "team_detail_this_team_badge") : nil,
                                    isStriped: !index.isMultiple(of: 2),
                                    isHighlighted: row.isThisTeam,
                                    accessibilityText: row.isThisTeam
                                        ? String(localized: "team_detail_standing_row_this_team_cd \(row.position) \(row.teamName) \(row.played) \(row.points)")
                                        : String(localized: "team_detail_standing_row_cd \(row.position) \(row.teamName) \(row.played) \(row.points)")
                                )
                            }
                        }
                    }
                }
                VStack(alignment: .leading, spacing: 0) {
                    BatSectionHeaderView(title: String(localized: "league_detail_fixtures_title"))
                    if detail.canFilterToTeam {
                        Toggle(
                            "league_detail_only_team_fixtures_label",
                            isOn: Binding(
                                get: { detail.onlyTeamFixtures },
                                set: { onAction(.onlyTeamFixturesToggled($0)) }
                            )
                        )
                        .batTextStyle(BatTypography.body)
                        .foregroundStyle(BatColor.textPrimary)
                        .tint(BatColor.brand)
                        .padding(.vertical, BatSpacing.sm)
                    }
                    if detail.visibleFixtures.isEmpty {
                        SectionMessage(key: "league_detail_fixtures_empty_body")
                    } else {
                        BatListView {
                            ForEach(Array(detail.visibleFixtures.enumerated()), id: \.element.id) { index, fixture in
                                FixtureRowView(
                                    row: fixture,
                                    highlightTeamId: detail.highlightTeamId,
                                    isStriped: !index.isMultiple(of: 2),
                                    onTeamTap: { onAction(.teamClicked(teamId: $0)) }
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
        LeagueDetailScreen(state: .loading, onAction: { _ in })
    }
}

#Preview("Error") {
    BatPreviewMatrix {
        LeagueDetailScreen(state: .error, onAction: { _ in })
    }
}

#Preview("Loaded") {
    BatPreviewMatrix {
        LeagueDetailScreen(state: .content(LeagueDetailPreviewData.loaded), onAction: { _ in })
    }
}

#Preview("Highlighted Team") {
    BatPreviewMatrix {
        LeagueDetailScreen(state: .content(LeagueDetailPreviewData.highlighted), onAction: { _ in })
    }
}

#Preview("Only Team Fixtures") {
    BatPreviewMatrix {
        LeagueDetailScreen(state: .content(LeagueDetailPreviewData.onlyTeamFixtures), onAction: { _ in })
    }
}

#Preview("Empty") {
    BatPreviewMatrix {
        LeagueDetailScreen(state: .content(LeagueDetailPreviewData.empty), onAction: { _ in })
    }
}

#Preview("Long names") {
    BatPreviewMatrix {
        LeagueDetailScreen(state: .content(LeagueDetailPreviewData.longNames), onAction: { _ in })
    }
}
