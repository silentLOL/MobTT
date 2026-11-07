import SwiftUI

struct LeagueTabRoute: View {
    @State private var viewModel = LeagueTabViewModel()

    let onChoosePlayer: () -> Void
    let onOpenPlayer: (_ playerId: String) -> Void
    let onOpenTeam: (_ teamId: String) -> Void
    let onOpenFixture: (_ fixtureId: String) -> Void
    let onOpenLeague: (_ leagueId: String, _ teamId: String) -> Void

    var body: some View {
        content
            .onAppear { viewModel.screenShown() }
            .task(id: viewModel.retryCount) { await viewModel.observe() }
            .task {
                for await event in viewModel.events {
                    switch event {
                    case .openChoosePlayer: onChoosePlayer()
                    case .openPlayer(let playerId): onOpenPlayer(playerId)
                    case .openLeague(let leagueId, let teamId): onOpenLeague(leagueId, teamId)
                    }
                }
            }
    }

    private var content: some View {
        LeagueTabScreen(state: viewModel.uiState, onAction: viewModel.onAction) { leagueId, teamId in
            // My Player's Team came from the tab, not from its Team screen, so its table row still opens it. The tab
            // already counts this screen view, so the League screen does not count another.
            LeagueDetailRoute(
                leagueId: leagueId,
                highlightTeamId: teamId,
                openedFromTeamId: nil,
                onOpenTeam: onOpenTeam,
                onOpenFixture: onOpenFixture,
                onOpenPlayer: onOpenPlayer,
                reportsScreenView: false
            )
            .id("\(leagueId)/\(teamId)")
        }
    }
}

/// `league` draws the League screen for the one-Team state, which has its own ViewModel.
struct LeagueTabScreen<LeagueContent: View>: View {
    let state: LeagueTabUiState
    let onAction: (LeagueTabAction) -> Void
    @ViewBuilder let league: (_ leagueId: String, _ teamId: String) -> LeagueContent

    var body: some View {
        switch state {
        case .league(let leagueId, let teamId):
            league(leagueId, teamId)
        case .loading:
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
        case .teams(let cards):
            ScrollView {
                VStack(alignment: .leading, spacing: BatSpacing.md) {
                    BatSectionHeaderView(title: String(localized: "league_tab_teams_title"))
                    ForEach(cards) { card in
                        TeamLeagueCardView(league: card.league, teamName: card.teamName) {
                            onAction(.teamCardClicked(leagueId: card.league.id, teamId: card.teamId))
                        }
                    }
                }
                .padding(BatSpacing.lg)
            }
        case .unavailable:
            message(title: String(localized: "league_tab_error_title"), body: String(localized: "league_tab_error_body")) {
                BatPrimaryButtonView(title: String(localized: "league_tab_error_retry_button")) { onAction(.retryClicked) }
            }
            .onAppear { AccessibilityNotification.Announcement(String(localized: "league_tab_error_body")).post() }
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

extension LeagueTabScreen where LeagueContent == EmptyView {
    /// For previews of the states that draw no League.
    init(state: LeagueTabUiState, onAction: @escaping (LeagueTabAction) -> Void) {
        self.init(state: state, onAction: onAction) { _, _ in EmptyView() }
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

#Preview("Teams") {
    BatPreviewMatrix {
        LeagueTabScreen(
            state: .teams([
                LeagueTeamCardUi(teamId: "olympic-1", teamName: "TTC Olympic 1", league: TeamDetailPreviewData.loaded.league!),
                LeagueTeamCardUi(teamId: "s1", teamName: "Union Tischtennisclub St. Pölten Nord-West 1", league: TeamDetailPreviewData.longNames.league!),
            ]),
            onAction: { _ in }
        )
    }
}

#Preview("Unavailable") {
    BatPreviewMatrix {
        LeagueTabScreen(state: .unavailable, onAction: { _ in })
    }
}
