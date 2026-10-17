import SwiftUI

/// The whole row is the target and the check mark is decorative: selection is announced through the trait.
struct BatSelectableRowView: View {
    let label: String
    let isSelected: Bool
    var supportingText: String?
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            HStack(spacing: BatSpacing.md) {
                VStack(alignment: .leading, spacing: 0) {
                    Text(label)
                        .batTextStyle(BatTypography.bodyStrong)
                        .foregroundStyle(BatColor.textPrimary)
                    if let supportingText {
                        Text(supportingText)
                            .batTextStyle(BatTypography.caption)
                            .foregroundStyle(BatColor.textSecondary)
                    }
                }
                .frame(maxWidth: .infinity, alignment: .leading)
                if isSelected {
                    BatIconView(icon: .check, tint: BatColor.brand)
                }
            }
            .padding(.vertical, BatSpacing.sm)
            .frame(minHeight: BatSizing.minTouchTarget)
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
        .accessibilityAddTraits(isSelected ? .isSelected : [])
    }
}

#Preview {
    BatPreviewMatrix {
        VStack(spacing: 0) {
            BatSelectableRowView(label: "Follow system", isSelected: true) {}
            BatSelectableRowView(label: "Light", isSelected: false) {}
            BatSelectableRowView(
                label: "Katharina Habsburg-Lothringen-Esterházy",
                isSelected: false,
                supportingText: "Union Tischtennisclub Wien Nord-West"
            ) {}
        }
    }
}
