import SwiftUI

struct HomeRoute: View {
    let onOpenSettings: () -> Void

    @State private var viewModel = HomeViewModel()

    var body: some View {
        HomeScreen(state: viewModel.uiState, onAction: viewModel.onAction)
            .task { await viewModel.observe() }
            .task {
                for await event in viewModel.events {
                    switch event {
                    case .openSettings: onOpenSettings()
                    }
                }
            }
    }
}

struct HomeScreen: View {
    let state: HomeUiState
    let onAction: (HomeAction) -> Void

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: BatSpacing.lg) {
                MySelectionView(selection: state.selection, onChooseClick: { onAction(.chooseClicked) })
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
                            FavoriteRowView(favorite: favorite, onRemoveClick: { onAction(.removeFavoriteClicked(favorite)) })
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
                    onPlayerClick: { onAction(.pickerPlayerClicked(id: $0)) },
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
