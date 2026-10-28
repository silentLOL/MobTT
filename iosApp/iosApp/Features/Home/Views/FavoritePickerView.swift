import SwiftUI

struct FavoritePickerView: View {
    let picker: FavoritePickerUi
    let onQueryChange: (String) -> Void
    let onBrowsePlayersClick: () -> Void
    let onDrillDownQueryChange: (String) -> Void
    let onDrillDownEntryClick: (String) -> Void
    let onDrillDownBack: () -> Void
    let onClubClick: (String) -> Void
    let onDismiss: () -> Void

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: BatSpacing.lg) {
                if let drillDown = picker.playerDrillDown {
                    header(
                        title: drillDown.step.title,
                        icon: .arrowBack,
                        accessibilityLabel: drillDown.step == .association ? "home_favorite_picker_back_cd" : "player_picker_back_cd",
                        action: onDrillDownBack
                    )
                    PlayerDrillDownView(drillDown: drillDown, onQueryChange: onDrillDownQueryChange, onEntryClick: onDrillDownEntryClick)
                } else {
                    header(title: "home_favorite_picker_title", icon: .close, accessibilityLabel: "core_close_button_cd", action: onDismiss)
                    BatSelectableRowView(label: String(localized: "home_favorite_browse_players"), isSelected: false, action: onBrowsePlayersClick)
                    DirectoryPickerView(
                        query: picker.query,
                        onQueryChange: onQueryChange,
                        sections: [DirectorySectionUi(title: String(localized: "core_clubs_section_title"), entries: picker.clubs)],
                        onEntryClick: { _, entry in onClubClick(entry.id) }
                    )
                }
            }
            .padding(.horizontal, BatSpacing.lg)
            .padding(.vertical, BatSpacing.lg)
            .frame(maxWidth: BatSizing.maxContentWidth)
            .frame(maxWidth: .infinity)
        }
        .batBackground()
    }

    private func header(
        title: LocalizedStringKey,
        icon: BatIcon,
        accessibilityLabel: String.LocalizationValue,
        action: @escaping () -> Void
    ) -> some View {
        HStack {
            Text(title)
                .batTextStyle(BatTypography.headline)
                .foregroundStyle(BatColor.textPrimary)
                .accessibilityAddTraits(.isHeader)
            Spacer(minLength: BatSpacing.sm)
            BatIconButtonView(icon: icon, accessibilityText: String(localized: accessibilityLabel), action: action)
        }
    }
}

private let previewPicker = FavoritePickerUi(
    query: "",
    clubs: [DirectoryEntryUi(id: "nussdorf", title: "Nussdorf TTC", subtitle: nil, selected: false)]
)

private let previewDrillDown = FavoritePickerUi(
    query: "",
    clubs: [],
    playerDrillDown: PlayerDrillDownUi(
        step: .player,
        contextName: "1. Landesliga Herren",
        query: "",
        entries: [
            DirectoryEntryUi(id: "huber", title: "Michael Huber", subtitle: "TTC Olympic", selected: true),
            DirectoryEntryUi(id: "gruber", title: "Anna Gruber", subtitle: "TTC Olympic", selected: false),
        ]
    )
)

private func previewPickerView(_ picker: FavoritePickerUi) -> FavoritePickerView {
    FavoritePickerView(
        picker: picker,
        onQueryChange: { _ in },
        onBrowsePlayersClick: {},
        onDrillDownQueryChange: { _ in },
        onDrillDownEntryClick: { _ in },
        onDrillDownBack: {},
        onClubClick: { _ in },
        onDismiss: {}
    )
}

#Preview("Results") {
    previewPickerView(previewPicker)
}

#Preview("Players step") {
    previewPickerView(previewDrillDown)
}

#Preview("No matches") {
    previewPickerView(FavoritePickerUi(query: "zzz", clubs: [])).preferredColorScheme(.dark)
}

#Preview("200% text") {
    previewPickerView(previewPicker).dynamicTypeSize(.accessibility2)
}
