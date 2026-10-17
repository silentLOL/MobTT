import SwiftUI

struct FavoritePickerView: View {
    let picker: FavoritePickerUi
    let onQueryChange: (String) -> Void
    let onPlayerClick: (String) -> Void
    let onClubClick: (String) -> Void
    let onDismiss: () -> Void

    private static let playersSection = 0

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: BatSpacing.lg) {
                HStack {
                    Text("home_favorite_picker_title")
                        .batTextStyle(BatTypography.headline)
                        .foregroundStyle(BatColor.textPrimary)
                        .accessibilityAddTraits(.isHeader)
                    Spacer(minLength: BatSpacing.sm)
                    BatIconButtonView(icon: .close, accessibilityText: String(localized: "core_close_button_cd"), action: onDismiss)
                }
                DirectoryPickerView(
                    query: picker.query,
                    onQueryChange: onQueryChange,
                    sections: [
                        DirectorySectionUi(title: String(localized: "core_players_section_title"), entries: picker.players),
                        DirectorySectionUi(title: String(localized: "core_clubs_section_title"), entries: picker.clubs),
                    ],
                    onEntryClick: { sectionIndex, entry in
                        if sectionIndex == Self.playersSection {
                            onPlayerClick(entry.id)
                        } else {
                            onClubClick(entry.id)
                        }
                    }
                )
            }
            .padding(.horizontal, BatSpacing.lg)
            .padding(.vertical, BatSpacing.lg)
            .frame(maxWidth: BatSizing.maxContentWidth)
            .frame(maxWidth: .infinity)
        }
        .batBackground()
    }
}

private let previewPicker = FavoritePickerUi(
    query: "",
    players: [
        DirectoryEntryUi(id: "huber", title: "Michael Huber", subtitle: "TTC Olympic", selected: true),
        DirectoryEntryUi(id: "gruber", title: "Anna Gruber", subtitle: "TTC Olympic", selected: false),
    ],
    clubs: [DirectoryEntryUi(id: "nussdorf", title: "Nussdorf TTC", subtitle: nil, selected: false)]
)

#Preview("Results") {
    FavoritePickerView(picker: previewPicker, onQueryChange: { _ in }, onPlayerClick: { _ in }, onClubClick: { _ in }, onDismiss: {})
}

#Preview("No matches") {
    FavoritePickerView(
        picker: FavoritePickerUi(query: "zzz", players: [], clubs: []),
        onQueryChange: { _ in },
        onPlayerClick: { _ in },
        onClubClick: { _ in },
        onDismiss: {}
    )
    .preferredColorScheme(.dark)
}

#Preview("200% text") {
    FavoritePickerView(picker: previewPicker, onQueryChange: { _ in }, onPlayerClick: { _ in }, onClubClick: { _ in }, onDismiss: {})
        .dynamicTypeSize(.accessibility2)
}
