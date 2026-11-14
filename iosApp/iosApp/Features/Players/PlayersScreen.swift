import SwiftUI

struct PlayersRoute: View {
    var onOpenPlayer: (String) -> Void = { _ in }
    var onChooseClub: (String) -> Void = { _ in }
    var onDone: () -> Void = {}

    @State private var viewModel: PlayersViewModel

    init(
        mode: PlayersMode = .browse,
        onOpenPlayer: @escaping (String) -> Void = { _ in },
        onChooseClub: @escaping (String) -> Void = { _ in },
        onDone: @escaping () -> Void = {}
    ) {
        self.onOpenPlayer = onOpenPlayer
        self.onChooseClub = onChooseClub
        self.onDone = onDone
        _viewModel = State(initialValue: PlayersViewModel(mode: mode))
    }

    var body: some View {
        PlayersScreen(state: viewModel.uiState, onAction: viewModel.onAction)
            .onAppear { viewModel.screenShown() }
            .task {
                for await event in viewModel.events {
                    switch event {
                    case .openPlayer(let playerId): onOpenPlayer(playerId)
                    case .clubChosen(let clubId): onChooseClub(clubId)
                    case .done: onDone()
                    }
                }
            }
    }
}

struct PlayersScreen: View {
    let state: PlayersUiState
    let onAction: (PlayersAction) -> Void

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: BatSpacing.md) {
                BatPickerRowView(
                    label: String(localized: "players_association_label"),
                    value: state.associationName,
                    placeholder: String(localized: "players_association_placeholder")
                ) { onAction(.associationRowClicked) }
                BatPickerRowView(
                    label: String(localized: "players_club_label"),
                    value: state.clubName,
                    placeholder: String(localized: "players_club_placeholder"),
                    hint: String(localized: "players_club_disabled_hint"),
                    isEnabled: state.isClubEnabled
                ) { onAction(.clubRowClicked) }
                if state.showsPlayers {
                    list
                }
            }
            .padding(BatSpacing.lg)
            .frame(maxWidth: BatSizing.maxContentWidth)
            .frame(maxWidth: .infinity)
        }
        .sheet(isPresented: Binding(get: { state.sheet != nil }, set: { if !$0 { onAction(.sheetDismissed) } })) {
            if let sheet = state.sheet {
                PlayersSheetView(
                    sheet: sheet,
                    onQueryChange: { onAction(.sheetQueryChanged($0)) },
                    onEntryClick: { onAction(.sheetEntryClicked(id: $0)) },
                    onRetry: { onAction(.sheetRetryClicked) },
                    onDismiss: { onAction(.sheetDismissed) }
                )
                .presentationDetents([.fraction(0.75), .large])
                .presentationDragIndicator(.visible)
                .presentationBackground(BatColor.background)
            }
        }
    }

    @ViewBuilder
    private var list: some View {
        switch state.list {
        case .noClub:
            BatEmptyStateView(
                icon: .search,
                title: String(localized: "players_empty_title"),
                message: String(localized: "players_pick_club_body")
            )
        case .loading:
            ProgressView()
                .tint(BatColor.brand)
                .accessibilityLabel(Text("club_loading_cd"))
                .frame(maxWidth: .infinity)
        case .error:
            VStack(spacing: BatSpacing.md) {
                BatEmptyStateView(icon: .search, title: String(localized: "players_error_title"), message: String(localized: "players_error_body"))
                BatPrimaryButtonView(title: String(localized: "club_error_retry_button")) { onAction(.retryClicked) }
            }
            .onAppear { AccessibilityNotification.Announcement(String(localized: "players_error_body")).post() }
        case .players(let players):
            BatTextFieldView(
                text: Binding(get: { state.query }, set: { onAction(.queryChanged($0)) }),
                label: String(localized: "core_search_label"),
                placeholder: String(localized: "players_search_placeholder"),
                leadingIcon: .search
            )
            if players.isEmpty {
                Text("core_search_empty_body")
                    .batTextStyle(BatTypography.body)
                    .foregroundStyle(BatColor.textSecondary)
            }
            ForEach(players) { player in
                BatSelectableRowView(
                    label: player.name,
                    isSelected: false,
                    supportingText: player.rcPoints.map { "RC \($0)" }
                ) { onAction(.playerClicked(playerId: player.id, name: player.name)) }
            }
        }
    }
}

#Preview("Empty") {
    BatPreviewMatrix {
        PlayersScreen(state: PlayersUiState(), onAction: { _ in })
    }
}

#Preview("Association chosen") {
    BatPreviewMatrix {
        PlayersScreen(state: PlayersUiState(associationName: "OÖTTV"), onAction: { _ in })
    }
}

#Preview("Players") {
    BatPreviewMatrix {
        PlayersScreen(state: PlayersPreviewData.chosen, onAction: { _ in })
    }
}

#Preview("Pick a Club") {
    BatPreviewMatrix {
        PlayersScreen(state: PlayersUiState(associationName: "OÖTTV", showsPlayers: false), onAction: { _ in })
    }
}
