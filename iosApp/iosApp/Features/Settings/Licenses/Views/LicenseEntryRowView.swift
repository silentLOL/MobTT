import SwiftUI

struct LicenseEntryRowView: View {
    let entry: LicenseEntry
    let isExpanded: Bool
    let onToggle: () -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: BatSpacing.sm) {
            Button(action: onToggle) {
                HStack(spacing: BatSpacing.md) {
                    VStack(alignment: .leading, spacing: BatSpacing.xs) {
                        Text(title)
                            .batTextStyle(BatTypography.bodyStrong)
                            .foregroundStyle(BatColor.textPrimary)
                        Text(entry.licenseName)
                            .batTextStyle(BatTypography.caption)
                            .foregroundStyle(BatColor.textSecondary)
                    }
                    .frame(maxWidth: .infinity, alignment: .leading)
                    BatIconView(icon: .chevronDown, tint: BatColor.textSecondary)
                        .rotationEffect(.degrees(isExpanded ? 180 : 0))
                }
                .frame(minHeight: BatSizing.minTouchTarget)
                .contentShape(Rectangle())
            }
            .buttonStyle(.plain)
            .accessibilityElement(children: .combine)
            .accessibilityValue(Text(isExpanded ? "core_section_expanded_cd" : "core_section_collapsed_cd"))
            .accessibilityHint(Text(isExpanded ? "licenses_entry_collapse_cd" : "licenses_entry_expand_cd"))
            if isExpanded {
                licenseText
            }
        }
        .padding(.vertical, BatSpacing.xs)
    }

    private var title: String {
        [entry.name, entry.version].compactMap { $0 }.joined(separator: " ")
    }

    @ViewBuilder
    private var licenseText: some View {
        if let text = entry.licenseText {
            Text(text)
                .batTextStyle(BatTypography.caption)
                .foregroundStyle(BatColor.textSecondary)
                .textSelection(.enabled)
        } else {
            VStack(alignment: .leading, spacing: BatSpacing.xs) {
                Text("licenses_text_unavailable")
                    .batTextStyle(BatTypography.caption)
                    .foregroundStyle(BatColor.textSecondary)
                if let url = entry.licenseUrl.flatMap(URL.init(string:)) {
                    Link(entry.licenseUrl ?? "", destination: url)
                        .batTextStyle(BatTypography.caption)
                }
            }
        }
    }
}

#Preview("Collapsed, open, no text") {
    VStack(alignment: .leading) {
        let entries = licensesSampleGroups.flatMap(\.entries)
        LicenseEntryRowView(entry: entries[0], isExpanded: false) {}
        LicenseEntryRowView(entry: entries[1], isExpanded: true) {}
        LicenseEntryRowView(entry: entries[2], isExpanded: true) {}
    }
    .padding()
    .batBackground()
}
