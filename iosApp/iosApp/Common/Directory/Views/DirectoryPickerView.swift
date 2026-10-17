import SwiftUI

/// Shows one "no matches" message when every section is empty, so a search that found nothing says so.
struct DirectoryPickerView: View {
    let query: String
    let onQueryChange: (String) -> Void
    let sections: [DirectorySectionUi]
    let onEntryClick: (_ sectionIndex: Int, _ entry: DirectoryEntryUi) -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: BatSpacing.md) {
            BatTextFieldView(
                text: Binding(get: { query }, set: onQueryChange),
                label: String(localized: "core_search_label"),
                placeholder: String(localized: "core_search_placeholder"),
                leadingIcon: .search
            )
            if sections.allSatisfy({ $0.entries.isEmpty }) {
                Text("core_search_empty_body")
                    .batTextStyle(BatTypography.body)
                    .foregroundStyle(BatColor.textSecondary)
            }
            ForEach(Array(sections.enumerated()), id: \.offset) { sectionIndex, section in
                if !section.entries.isEmpty {
                    VStack(alignment: .leading, spacing: 0) {
                        if let title = section.title {
                            BatSectionHeaderView(title: title)
                        }
                        ForEach(section.entries) { entry in
                            BatSelectableRowView(
                                label: entry.title,
                                isSelected: entry.selected,
                                supportingText: entry.subtitle
                            ) {
                                onEntryClick(sectionIndex, entry)
                            }
                        }
                    }
                }
            }
        }
    }
}

private let previewSections = [
    DirectorySectionUi(title: "Players", entries: [
        DirectoryEntryUi(id: "1", title: "Michael Huber", subtitle: "TTC Olympic", selected: true),
        DirectoryEntryUi(
            id: "2",
            title: "Katharina Habsburg-Lothringen-Esterházy",
            subtitle: "Union Tischtennisclub St. Pölten Nord-West",
            selected: false
        ),
    ]),
]

#Preview("Loaded") {
    BatPreviewMatrix {
        DirectoryPickerView(query: "", onQueryChange: { _ in }, sections: previewSections, onEntryClick: { _, _ in })
    }
}

#Preview("No matches") {
    BatPreviewMatrix {
        DirectoryPickerView(
            query: "zzz",
            onQueryChange: { _ in },
            sections: [DirectorySectionUi(title: nil, entries: [])],
            onEntryClick: { _, _ in }
        )
    }
}
