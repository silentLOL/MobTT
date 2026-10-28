import SwiftUI

/// The list of one drill-down step with its filter field. The screen around it draws the title and the back button.
struct PlayerDrillDownView: View {
    let drillDown: PlayerDrillDownUi
    let onQueryChange: (String) -> Void
    let onEntryClick: (String) -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: BatSpacing.md) {
            if let contextName = drillDown.contextName {
                Text(contextName)
                    .batTextStyle(BatTypography.body)
                    .foregroundStyle(BatColor.textSecondary)
            }
            DirectoryPickerView(
                query: drillDown.query,
                onQueryChange: onQueryChange,
                sections: [DirectorySectionUi(title: nil, entries: drillDown.entries)],
                onEntryClick: { _, entry in onEntryClick(entry.id) },
                placeholder: String(localized: "core_filter_placeholder")
            )
        }
    }
}

extension PlayerDrillDownStep {
    var title: LocalizedStringKey {
        switch self {
        case .association: "player_picker_association_title"
        case .league: "player_picker_league_title"
        case .player: "player_picker_player_title"
        }
    }
}

private let previewLeagues = PlayerDrillDownUi(
    step: .league,
    contextName: "Wiener Tischtennisverband (WTTV)",
    query: "",
    entries: [
        DirectoryEntryUi(id: "l1", title: "1. Landesliga Herren", subtitle: nil, selected: false),
        DirectoryEntryUi(id: "l2", title: "2. Landesliga Herren", subtitle: nil, selected: false),
    ]
)

#Preview("League step") {
    BatPreviewMatrix {
        PlayerDrillDownView(drillDown: previewLeagues, onQueryChange: { _ in }, onEntryClick: { _ in })
    }
}

#Preview("No matches") {
    BatPreviewMatrix {
        PlayerDrillDownView(
            drillDown: PlayerDrillDownUi(step: .player, contextName: "1. Landesliga Herren", query: "zzz", entries: []),
            onQueryChange: { _ in },
            onEntryClick: { _ in }
        )
    }
}
