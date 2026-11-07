import SwiftUI

/// A section header the whole width of which opens and closes its section; the chevron points up while it is open.
struct BatCollapsibleSectionHeaderView: View {
    let title: String
    let isExpanded: Bool
    let onToggle: () -> Void

    var body: some View {
        Button(action: onToggle) {
            HStack {
                Text(title)
                    .textCase(.uppercase)
                    .batTextStyle(BatTypography.bodyStrong)
                    .foregroundStyle(BatColor.textPrimary)
                Spacer(minLength: BatSpacing.sm)
                BatIconView(icon: .chevronDown)
                    .rotationEffect(.degrees(isExpanded ? 180 : 0))
                    .animation(.easeInOut(duration: 0.2), value: isExpanded)
            }
            .frame(maxWidth: .infinity, minHeight: BatSizing.minTouchTarget)
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
        .accessibilityElement(children: .combine)
        .accessibilityAddTraits(.isHeader)
        .accessibilityValue(Text(isExpanded ? "core_section_expanded_cd" : "core_section_collapsed_cd"))
    }
}

#Preview {
    BatPreviewMatrix {
        VStack(spacing: BatSpacing.md) {
            BatCollapsibleSectionHeaderView(title: "Fixtures", isExpanded: false) {}
            BatCollapsibleSectionHeaderView(title: "Player ranking", isExpanded: true) {}
        }
    }
}
