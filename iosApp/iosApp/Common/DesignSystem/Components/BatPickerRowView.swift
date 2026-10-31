import SwiftUI

/// A labelled field that opens a picker. Shows [value], or [placeholder] while nothing is chosen; [hint] explains a
/// disabled row. The whole row is the target and the chevron is decorative.
struct BatPickerRowView: View {
    let label: String
    let value: String?
    let placeholder: String
    var hint: String?
    var isEnabled = true
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            VStack(alignment: .leading, spacing: BatSpacing.xs) {
                Text(label)
                    .textCase(.uppercase)
                    .batTextStyle(BatTypography.overline)
                    .foregroundStyle(BatColor.textTertiary)
                HStack(spacing: BatSpacing.sm) {
                    Text(value ?? placeholder)
                        .batTextStyle(BatTypography.body)
                        .foregroundStyle(value == nil ? BatColor.textTertiary : BatColor.textPrimary)
                        .frame(maxWidth: .infinity, alignment: .leading)
                    BatIconView(icon: .chevronDown, tint: BatColor.textTertiary)
                }
                .padding(.horizontal, BatSpacing.md)
                .padding(.vertical, BatSpacing.sm)
                .frame(minHeight: BatSizing.minTouchTarget)
                .background(BatColor.surface, in: BatShapes.medium)
                .overlay(BatShapes.medium.stroke(BatColor.borderInput, lineWidth: BatSizing.borderThin))
                if !isEnabled, let hint {
                    Text(hint)
                        .batTextStyle(BatTypography.caption)
                        .foregroundStyle(BatColor.textSecondary)
                }
            }
            .contentShape(Rectangle())
            .opacity(isEnabled ? 1 : 0.6)
        }
        .buttonStyle(.plain)
        .disabled(!isEnabled)
        .accessibilityElement(children: .combine)
        .accessibilityValue(value ?? placeholder)
        .accessibilityHint(isEnabled ? "" : (hint ?? ""))
    }
}

#Preview {
    BatPickerRowView(label: "Association", value: "OÖTTV", placeholder: "Choose an Association") {}
        .padding(BatSpacing.lg)
        .batBackground()
}

#Preview("States") {
    BatPreviewMatrix {
        VStack(spacing: BatSpacing.md) {
            BatPickerRowView(label: "Association", value: nil, placeholder: "Choose an Association") {}
            BatPickerRowView(
                label: "Club",
                value: nil,
                placeholder: "Choose a Club",
                hint: "Choose an Association first",
                isEnabled: false
            ) {}
            BatPickerRowView(label: "Club", value: "Union Tischtennisclub St. Pölten Nord-West", placeholder: "Choose a Club") {}
        }
    }
}
