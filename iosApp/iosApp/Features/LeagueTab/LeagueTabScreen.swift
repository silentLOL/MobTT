import SwiftUI

struct LeagueTabRoute: View {
    @State private var viewModel = LeagueTabViewModel()

    let onChoosePlayer: () -> Void
    let onOpenPlayer: (_ playerId: String) -> Void
    let onOpenTeam: (_ teamId: String) -> Void
    let onOpenFixture: (_ fixtureId: String) -> Void

    var body: some View {
        content
            .onAppear { viewModel.screenShown() }
            .task(id: viewModel.retryCount) { await viewModel.observe() }
            .task {
                for await event in viewModel.events {
                    switch event {
                    case .openChoosePlayer: onChoosePlayer()
                    case .openPlayer(let playerId): onOpenPlayer(playerId)
                    }
                }
            }
    }

    @ViewBuilder
    private var content: some View {
        if case .league(let leagueId, let teamId) = viewModel.uiState {
            // My Player's Team came from the tab, not from its Team screen, so its table row still opens it.
            LeagueDetailRoute(
                leagueId: leagueId,
                highlightTeamId: teamId,
                openedFromTeamId: nil,
                onOpenTeam: onOpenTeam,
                onOpenFixture: onOpenFixture,
                onOpenPlayer: onOpenPlayer
            )
            .id("\(leagueId)/\(teamId)")
        } else {
            LeagueTabScreen(state: viewModel.uiState, onAction: viewModel.onAction)
        }
    }
}

/// Every state of the tab except a League, which the League screen draws itself.
struct LeagueTabScreen: View {
    let state: LeagueTabUiState
    let onAction: (LeagueTabAction) -> Void

    var body: some View {
        switch state {
        case .loading, .league:
            ProgressView()
                .tint(BatColor.brand)
                .accessibilityLabel(Text("league_tab_loading_cd"))
                .frame(maxWidth: .infinity, maxHeight: .infinity)
        case .noMyPlayer:
            message(title: String(localized: "league_tab_no_player_title"), body: String(localized: "league_tab_no_player_body")) {
                BatPrimaryButtonView(title: String(localized: "league_tab_choose_player_button")) { onAction(.choosePlayerClicked) }
            }
        case .noTeam(let playerId, let playerName):
            message(title: String(localized: "league_tab_no_team_title"), body: String(localized: "league_tab_no_team_body \(playerName)")) {
                BatSecondaryButtonView(title: String(localized: "league_tab_open_player_button")) { onAction(.myPlayerClicked(playerId: playerId)) }
            }
        case .unavailable:
            message(title: String(localized: "league_tab_error_title"), body: String(localized: "league_detail_error_body")) {
                BatPrimaryButtonView(title: String(localized: "league_detail_error_retry_button")) { onAction(.retryClicked) }
            }
            .onAppear { AccessibilityNotification.Announcement(String(localized: "league_detail_error_body")).post() }
        }
    }

    private func message(title: String, body: String, @ViewBuilder action: () -> some View) -> some View {
        ScrollView {
            VStack(spacing: BatSpacing.md) {
                BatEmptyStateView(icon: .league, title: title, message: body)
                action()
            }
            .padding(BatSpacing.lg)
        }
    }
}

#Preview("Loading") {
    BatPreviewMatrix {
        LeagueTabScreen(state: .loading, onAction: { _ in })
    }
}

#Preview("No My Player") {
    BatPreviewMatrix {
        LeagueTabScreen(state: .noMyPlayer, onAction: { _ in })
    }
}

#Preview("No Team") {
    BatPreviewMatrix {
        LeagueTabScreen(state: .noTeam(playerId: "p1", playerName: "Katharina Habsburg-Lothringen-Esterházy"), onAction: { _ in })
    }
}

#Preview("Unavailable") {
    BatPreviewMatrix {
        LeagueTabScreen(state: .unavailable, onAction: { _ in })
    }
}
