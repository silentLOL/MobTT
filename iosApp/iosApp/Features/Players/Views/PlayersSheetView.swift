import SwiftUI

/// The Association or Club picker: a closable header, then a searchable list, a spinner, or a retry message.
struct PlayersSheetView: View {
    let sheet: PlayersSheetUi
    let onQueryChange: (String) -> Void
    let onEntryClick: (String) -> Void
    let onRetry: () -> Void
    let onDismiss: () -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: BatSpacing.lg) {
            HStack {
                Text(title)
                    .batTextStyle(BatTypography.headline)
                    .foregroundStyle(BatColor.textPrimary)
                    .accessibilityAddTraits(.isHeader)
                Spacer(minLength: BatSpacing.sm)
                BatIconButtonView(icon: .close, accessibilityText: String(localized: "core_close_button_cd"), action: onDismiss)
            }
            content
        }
        .padding(.horizontal, BatSpacing.lg)
        .padding(.vertical, BatSpacing.lg)
        .frame(maxWidth: BatSizing.maxContentWidth, maxHeight: .infinity, alignment: .top)
        .frame(maxWidth: .infinity)
        .batBackground()
    }

    private var title: LocalizedStringKey {
        sheet.kind == .association ? "core_drilldown_association_title" : "players_club_sheet_title"
    }

    @ViewBuilder
    private var content: some View {
        switch sheet.content {
        case .loading:
            ProgressView()
                .tint(BatColor.brand)
                .accessibilityLabel(Text("club_loading_cd"))
                .frame(maxWidth: .infinity)
        case .error:
            VStack(spacing: BatSpacing.md) {
                BatEmptyStateView(icon: .search, title: String(localized: "players_error_title"), message: String(localized: "players_error_body"))
                BatPrimaryButtonView(title: String(localized: "club_error_retry_button"), action: onRetry)
            }
            .onAppear { AccessibilityNotification.Announcement(String(localized: "players_error_body")).post() }
        case .entries(let entries):
            DirectoryPickerView(
                query: sheet.query,
                onQueryChange: onQueryChange,
                sections: [DirectorySectionUi(title: nil, entries: entries)],
                onEntryClick: { _, entry in onEntryClick(entry.id) },
                placeholder: String(localized: "core_search_placeholder"),
                scrollsList: true
            )
        }
    }
}

#Preview("Associations") {
    PlayersSheetView(
        sheet: PlayersSheetUi(kind: .association, query: "", content: .entries(PlayersPreviewData.associations)),
        onQueryChange: { _ in },
        onEntryClick: { _ in },
        onRetry: {},
        onDismiss: {}
    )
}

#Preview("Error") {
    PlayersSheetView(
        sheet: PlayersSheetUi(kind: .club, query: "", content: .error),
        onQueryChange: { _ in },
        onEntryClick: { _ in },
        onRetry: {},
        onDismiss: {}
    )
    .preferredColorScheme(.dark)
}
