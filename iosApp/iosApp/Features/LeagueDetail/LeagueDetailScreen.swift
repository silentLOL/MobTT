import SwiftUI

struct LeagueDetailRoute: View {
    @State private var viewModel: LeagueDetailViewModel

    init(leagueId: String) {
        _viewModel = State(initialValue: LeagueDetailViewModel(leagueId: leagueId))
    }

    var body: some View {
        LeagueDetailScreen(state: viewModel.uiState, onAction: viewModel.onAction)
            .onAppear { viewModel.loadIfNeeded() }
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
            ContentView(detail: detail)
        }
    }
}

private struct ContentView: View {
    let detail: LeagueDetailUi

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
                        BatCardView(padding: BatSpacing.sm) {
                            VStack(spacing: 0) {
                                ForEach(detail.standings) { StandingRowView(row: $0) }
                            }
                        }
                    }
                }
                VStack(alignment: .leading, spacing: 0) {
                    BatSectionHeaderView(title: String(localized: "league_detail_fixtures_title"))
                    if detail.fixtures.isEmpty {
                        SectionMessage(key: "league_detail_fixtures_empty_body")
                    } else {
                        BatCardView(padding: BatSpacing.sm) {
                            VStack(spacing: 0) {
                                ForEach(Array(detail.fixtures.enumerated()), id: \.element.id) { index, fixture in
                                    if index > 0 {
                                        Divider().overlay(BatColor.border)
                                    }
                                    FixtureRowView(row: fixture)
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
