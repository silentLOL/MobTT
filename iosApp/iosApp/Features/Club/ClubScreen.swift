import SwiftUI

struct ClubRoute: View {
    let source: ClubSource
    let onOpenSettings: () -> Void
    let onOpenTeam: (String) -> Void

    @State private var viewModel: ClubViewModel

    init(source: ClubSource, onOpenSettings: @escaping () -> Void, onOpenTeam: @escaping (String) -> Void) {
        self.source = source
        self.onOpenSettings = onOpenSettings
        self.onOpenTeam = onOpenTeam
        _viewModel = State(initialValue: ClubViewModel(source: source))
    }

    var body: some View {
        ClubScreen(state: viewModel.uiState, onAction: viewModel.onAction)
            .task(id: viewModel.retryCount) { await viewModel.observe() }
            .task {
                for await event in viewModel.events {
                    switch event {
                    case .openSettings: onOpenSettings()
                    case .openTeam(let teamId): onOpenTeam(teamId)
                    }
                }
            }
    }
}

struct ClubScreen: View {
    let state: ClubUiState
    let onAction: (ClubAction) -> Void

    var body: some View {
        switch state {
        case .loading:
            ProgressView()
                .tint(BatColor.brand)
                .accessibilityLabel(Text("club_loading_cd"))
                .frame(maxWidth: .infinity, maxHeight: .infinity)
        case .noClub:
            MessageView(
                title: String(localized: "club_empty_title"),
                message: String(localized: "club_empty_body"),
                buttonTitle: String(localized: "club_empty_button"),
                onButtonClick: { onAction(.chooseClicked) }
            )
        case .error:
            MessageView(
                title: String(localized: "club_error_title"),
                message: String(localized: "club_error_body"),
                buttonTitle: String(localized: "club_error_retry_button"),
                onButtonClick: { onAction(.retryClicked) }
            )
            .onAppear { AccessibilityNotification.Announcement(String(localized: "club_error_body")).post() }
        case .content(let overview):
            ContentView(overview: overview, onAction: onAction)
        }
    }
}

private struct MessageView: View {
    let title: String
    let message: String
    let buttonTitle: String
    let onButtonClick: () -> Void

    var body: some View {
        ScrollView {
            VStack(spacing: BatSpacing.md) {
                BatEmptyStateView(icon: .club, title: title, message: message)
                BatPrimaryButtonView(title: buttonTitle, action: onButtonClick)
            }
            .padding(BatSpacing.lg)
        }
    }
}

private struct ContentView: View {
    let overview: ClubOverviewUi
    let onAction: (ClubAction) -> Void

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: BatSpacing.lg) {
                BatHeroCardView {
                    HStack(alignment: .top, spacing: BatSpacing.md) {
                        VStack(alignment: .leading, spacing: BatSpacing.sm) {
                            BatHeroPillView(text: String(localized: "club_hero_overline"))
                            Text(overview.clubName)
                                .batTextStyle(BatTypography.display)
                                .foregroundStyle(BatColor.onHero)
                            Text(overview.associationName)
                                .batTextStyle(BatTypography.body)
                                .foregroundStyle(BatColor.onHero)
                        }
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .accessibilityElement(children: .combine)
                        BatFavoriteButtonView(
                            isFavorite: overview.isFavorite,
                            accessibilityText: overview.isFavorite
                                ? String(localized: "club_favorite_remove_cd \(overview.clubName)")
                                : String(localized: "club_favorite_add_cd \(overview.clubName)"),
                            accessibilityState: overview.isFavorite
                                ? String(localized: "core_favorite_on_state")
                                : String(localized: "core_favorite_off_state"),
                            action: { onAction(.favoriteClicked) }
                        )
                    }
                }
                VStack(alignment: .leading, spacing: 0) {
                    BatSectionHeaderView(title: String(localized: "club_teams_title"))
                    if overview.teams.isEmpty {
                        Text("club_teams_empty_body")
                            .batTextStyle(BatTypography.body)
                            .foregroundStyle(BatColor.textSecondary)
                    }
                    VStack(spacing: BatSpacing.md) {
                        ForEach(overview.teams) { team in
                            TeamCardView(team: team, onTap: { onAction(.teamClicked(teamId: team.id)) })
                        }
                    }
                }
            }
            .padding(BatSpacing.lg)
        }
    }
}

#Preview("Loading") {
    BatPreviewMatrix {
        ClubScreen(state: .loading, onAction: { _ in })
    }
}

#Preview("No Club") {
    BatPreviewMatrix {
        ClubScreen(state: .noClub, onAction: { _ in })
    }
}

#Preview("Error") {
    BatPreviewMatrix {
        ClubScreen(state: .error, onAction: { _ in })
    }
}

#Preview("Loaded") {
    BatPreviewMatrix {
        ClubScreen(state: .content(ClubPreviewData.overview), onAction: { _ in })
    }
}

#Preview("Favorite") {
    BatPreviewMatrix {
        ClubScreen(state: .content(ClubPreviewData.favorite), onAction: { _ in })
    }
}

#Preview("Long names") {
    BatPreviewMatrix {
        ClubScreen(state: .content(ClubPreviewData.longNames), onAction: { _ in })
    }
}

#Preview("No Teams") {
    BatPreviewMatrix {
        ClubScreen(state: .content(ClubPreviewData.noTeams), onAction: { _ in })
    }
}
