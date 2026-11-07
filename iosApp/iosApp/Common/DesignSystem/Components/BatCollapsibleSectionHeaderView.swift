import SwiftUI

/// A section header the whole width of which opens and closes its section; the chevron points up while it is open. An `action`, such as an info button, sits before the chevron and is reached on its own, not as part of the header.
struct BatCollapsibleSectionHeaderView<Action: View>: View {
    let title: String
    let isExpanded: Bool
    let onToggle: () -> Void
    @ViewBuilder var action: () -> Action

    var body: some View {
        ZStack(alignment: .trailing) {
            header
            action()
                .padding(.trailing, BatSizing.icon + BatSpacing.sm)
        }
    }

    private var header: some View {
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

extension BatCollapsibleSectionHeaderView where Action == EmptyView {
    init(title: String, isExpanded: Bool, onToggle: @escaping () -> Void) {
        self.init(title: title, isExpanded: isExpanded, onToggle: onToggle) { EmptyView() }
    }
}

#Preview {
    BatPreviewMatrix {
        VStack(spacing: BatSpacing.md) {
            BatCollapsibleSectionHeaderView(title: "Fixtures", isExpanded: false) {}
            BatCollapsibleSectionHeaderView(title: "Player ranking", isExpanded: true) {}
            BatCollapsibleSectionHeaderView(title: "Rating history", isExpanded: false, onToggle: {}) {
                BatIconButtonView(icon: .info, accessibilityText: "About", action: {})
            }
        }
    }
}
