import SwiftUI

struct HomeRoute: View {
    let onOpenSettings: () -> Void
    let onOpenMyClub: () -> Void
    let onOpenClub: (String) -> Void
    let onOpenTeam: (String) -> Void
    let onOpenPlayer: (String) -> Void

    @State private var viewModel = HomeViewModel()

    var body: some View {
        HomeScreen(state: viewModel.uiState, onAction: viewModel.onAction)
            .task(id: viewModel.retryCount) { await viewModel.observe() }
            .task {
                for await event in viewModel.events {
                    switch event {
                    case .openSettings: onOpenSettings()
                    case .openMyClub: onOpenMyClub()
                    case .openClub(let clubId): onOpenClub(clubId)
                    case .openTeam(let teamId): onOpenTeam(teamId)
                    case .openPlayer(let playerId): onOpenPlayer(playerId)
                    }
                }
            }
    }
}

struct HomeScreen: View {
    let state: HomeUiState
    let onAction: (HomeAction) -> Void

    private func openAction(for favorite: HomeFavoriteUi) -> (() -> Void)? {
        switch favorite.kind {
        case .club: { onAction(.favoriteClubClicked(id: favorite.entityId)) }
        case .team: { onAction(.favoriteTeamClicked(id: favorite.entityId)) }
        case .player: { onAction(.favoritePlayerClicked(id: favorite.entityId)) }
        }
    }

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: BatSpacing.lg) {
                MySelectionView(selection: state.selection, onChooseClick: { onAction(.chooseClicked) })
                if state.selection.isChosen {
                    BatSecondaryButtonView(title: String(localized: "home_view_club_button")) { onAction(.viewClubClicked) }
                }
                UpcomingFixturesView(fixtures: state.fixtures, onRetry: { onAction(.retryFixturesClicked) })
                VStack(alignment: .leading, spacing: 0) {
                    BatSectionHeaderView(
                        title: String(localized: "home_favorites_title"),
                        actionLabel: String(localized: "home_favorites_add_button"),
                        onAction: { onAction(.addFavoriteClicked) }
                    )
                    if state.favorites.isEmpty {
                        Text("home_favorites_empty_body")
                            .batTextStyle(BatTypography.body)
                            .foregroundStyle(BatColor.textSecondary)
                    }
                    VStack(spacing: BatSpacing.sm) {
                        ForEach(state.favorites) { favorite in
                            FavoriteRowView(
                                favorite: favorite,
                                onClick: openAction(for: favorite),
                                onRemoveClick: { onAction(.removeFavoriteClicked(favorite)) }
                            )
                        }
                    }
                }
            }
            .padding(BatSpacing.lg)
        }
        .sheet(isPresented: Binding(get: { state.picker != nil }, set: { if !$0 { onAction(.pickerDismissed) } })) {
            if let picker = state.picker {
                FavoritePickerView(
                    picker: picker,
                    onQueryChange: { onAction(.pickerQueryChanged($0)) },
                    onBrowsePlayersClick: { onAction(.browsePlayersClicked) },
                    onDrillDownQueryChange: { onAction(.pickerQueryChanged($0)) },
                    onDrillDownEntryClick: { onAction(.pickerDrillDownEntryClicked(id: $0)) },
                    onDrillDownBack: { onAction(.pickerDrillDownBackClicked) },
                    onClubClick: { onAction(.pickerClubClicked(id: $0)) },
                    onDismiss: { onAction(.pickerDismissed) }
                )
                .presentationDetents([.medium, .large])
                .presentationDragIndicator(.visible)
                .presentationBackground(BatColor.background)
            }
        }
    }
}

private let loadedState = HomeUiState(
    selection: .player(name: "Michael Huber", clubName: "TTC Olympic", associationName: "Wiener Tischtennisverband (WTTV)"),
    favorites: [
        HomeFavoriteUi(kind: .player, entityId: "gruber", name: "Anna Gruber", detail: "TTC Olympic"),
        HomeFavoriteUi(kind: .club, entityId: "nussdorf", name: "Nussdorf TTC", detail: "Wiener Tischtennisverband (WTTV)"),
        HomeFavoriteUi(kind: .team, entityId: "olympic-1", name: "TTC Olympic 1", detail: "TTC Olympic"),
    ]
)

#Preview("Empty") {
    BatPreviewMatrix {
        HomeScreen(state: HomeUiState(), onAction: { _ in })
    }
}

#Preview("Loaded") {
    BatPreviewMatrix {
        HomeScreen(state: loadedState, onAction: { _ in })
    }
}

#Preview("Long name") {
    BatPreviewMatrix {
        HomeScreen(
            state: HomeUiState(
                selection: .player(
                    name: "Katharina Habsburg-Lothringen-Esterházy",
                    clubName: "Union Tischtennisclub St. Pölten Nord-West",
                    associationName: "Niederösterreichischer Tischtennisverband (NÖTTV)"
                ),
                favorites: [
                    HomeFavoriteUi(
                        kind: .player,
                        entityId: "pichler",
                        name: "Katharina Habsburg-Lothringen-Esterházy",
                        detail: "Union Tischtennisclub St. Pölten Nord-West"
                    ),
                ]
            ),
            onAction: { _ in }
        )
    }
}

#Preview("Fixtures") {
    BatPreviewMatrix {
        HomeScreen(
            state: HomeUiState(selection: loadedState.selection, favorites: loadedState.favorites, fixtures: HomePreviewData.fixtures),
            onAction: { _ in }
        )
    }
}

#Preview("Fixtures unavailable") {
    BatPreviewMatrix {
        HomeScreen(
            state: HomeUiState(selection: loadedState.selection, favorites: loadedState.favorites, fixtures: .unavailable),
            onAction: { _ in }
        )
    }
}

#Preview("My Club") {
    BatPreviewMatrix {
        HomeScreen(
            state: HomeUiState(
                selection: .club(name: "Nussdorf TTC", associationName: "Wiener Tischtennisverband (WTTV)"),
                favorites: loadedState.favorites
            ),
            onAction: { _ in }
        )
    }
}
