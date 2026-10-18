import SwiftUI

struct SettingsRoute: View {
    @Binding var appearance: AppearanceChoice
    let onDismiss: () -> Void

    @State private var viewModel = SettingsViewModel()

    var body: some View {
        SettingsScreen(state: viewModel.uiState, appearance: $appearance, onAction: viewModel.onAction)
            .task { await viewModel.observe() }
            .task {
                for await event in viewModel.events {
                    switch event {
                    case .dismiss: onDismiss()
                    }
                }
            }
    }
}

struct SettingsScreen: View {
    let state: SettingsUiState
    @Binding var appearance: AppearanceChoice
    let onAction: (SettingsAction) -> Void

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: BatSpacing.lg) {
                if let picker = state.picker {
                    pickerContent(picker)
                } else {
                    mainContent
                }
            }
            .padding(.horizontal, BatSpacing.lg)
            .padding(.vertical, BatSpacing.lg)
            .frame(maxWidth: BatSizing.maxContentWidth)
            .frame(maxWidth: .infinity)
        }
        .batBackground()
    }

    @ViewBuilder
    private var mainContent: some View {
        header(title: "settings_title", icon: .close, accessibilityLabel: "core_close_button_cd") { onAction(.doneClicked) }
        SettingsChoiceRowView(
            title: String(localized: "settings_my_player_title"),
            value: state.myPlayerName,
            onChooseClick: { onAction(.choosePlayerClicked) }
        )
        SettingsChoiceRowView(
            title: String(localized: "settings_my_club_title"),
            value: state.myClubName,
            onChooseClick: { onAction(.chooseClubClicked) }
        )
        SettingsDerivedRowView(
            title: String(localized: "settings_club_title"),
            value: state.derivedClubName,
            derivedFrom: state.derivedFrom
        )
        SettingsDerivedRowView(
            title: String(localized: "settings_association_title"),
            value: state.derivedAssociationName,
            derivedFrom: state.derivedFrom
        )
        VStack(alignment: .leading, spacing: 0) {
            BatSectionHeaderView(title: String(localized: "settings_appearance_title"))
            ForEach(AppearanceChoice.allCases, id: \.self) { choice in
                BatSelectableRowView(label: String(localized: choice.label), isSelected: choice == appearance) {
                    appearance = choice
                }
            }
        }
        BatPrimaryButtonView(title: String(localized: "settings_done_button")) { onAction(.doneClicked) }
    }

    @ViewBuilder
    private func pickerContent(_ picker: SettingsPickerUi) -> some View {
        header(
            title: picker.kind == .player ? "settings_picker_player_title" : "settings_picker_club_title",
            icon: .arrowBack,
            accessibilityLabel: "settings_picker_back_cd"
        ) { onAction(.pickerBackClicked) }
        DirectoryPickerView(
            query: picker.query,
            onQueryChange: { onAction(.pickerQueryChanged($0)) },
            sections: [DirectorySectionUi(title: nil, entries: picker.entries)],
            onEntryClick: { _, entry in onAction(.pickerEntryClicked(id: entry.id)) }
        )
    }

    private func header(title: LocalizedStringKey, icon: BatIcon, accessibilityLabel: String.LocalizationValue, onTap: @escaping () -> Void) -> some View {
        HStack {
            Text(title)
                .batTextStyle(BatTypography.headline)
                .foregroundStyle(BatColor.textPrimary)
                .accessibilityAddTraits(.isHeader)
            Spacer(minLength: BatSpacing.sm)
            BatIconButtonView(icon: icon, accessibilityText: String(localized: accessibilityLabel), action: onTap)
        }
    }
}

private let chosenPlayerState = SettingsUiState(
    myPlayerName: "Michael Huber",
    derivedFrom: .player,
    derivedClubName: "TTC Olympic",
    derivedAssociationName: "Wiener Tischtennisverband (WTTV)"
)

#Preview("Empty") {
    @Previewable @State var appearance = AppearanceChoice.system
    SettingsScreen(state: SettingsUiState(), appearance: $appearance, onAction: { _ in })
}

#Preview("My Player, dark") {
    @Previewable @State var appearance = AppearanceChoice.dark
    SettingsScreen(state: chosenPlayerState, appearance: $appearance, onAction: { _ in })
        .preferredColorScheme(.dark)
}

#Preview("My Club") {
    @Previewable @State var appearance = AppearanceChoice.light
    SettingsScreen(
        state: SettingsUiState(
            myClubName: "Nussdorf TTC",
            derivedFrom: .club,
            derivedClubName: "Nussdorf TTC",
            derivedAssociationName: "Wiener Tischtennisverband (WTTV)"
        ),
        appearance: $appearance,
        onAction: { _ in }
    )
}

#Preview("Long name, 200% text") {
    @Previewable @State var appearance = AppearanceChoice.light
    SettingsScreen(
        state: SettingsUiState(
            myPlayerName: "Katharina Habsburg-Lothringen-Esterházy",
            derivedFrom: .player,
            derivedClubName: "Union Tischtennisclub St. Pölten Nord-West",
            derivedAssociationName: "Niederösterreichischer Tischtennisverband (NÖTTV)"
        ),
        appearance: $appearance,
        onAction: { _ in }
    )
    .dynamicTypeSize(.accessibility2)
}

#Preview("Picker") {
    @Previewable @State var appearance = AppearanceChoice.system
    SettingsScreen(
        state: SettingsUiState(
            picker: SettingsPickerUi(
                kind: .player,
                query: "",
                entries: [
                    DirectoryEntryUi(id: "huber", title: "Michael Huber", subtitle: "TTC Olympic", selected: true),
                    DirectoryEntryUi(id: "gruber", title: "Anna Gruber", subtitle: "TTC Olympic", selected: false),
                ]
            )
        ),
        appearance: $appearance,
        onAction: { _ in }
    )
}
