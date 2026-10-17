import SwiftUI

struct BatTextFieldView: View {
    @Binding var text: String
    var label: String?
    var placeholder: String?
    var leadingIcon: BatIcon?

    @FocusState private var isFocused: Bool

    var body: some View {
        VStack(alignment: .leading, spacing: BatSpacing.xs) {
            if let label {
                Text(label)
                    .textCase(.uppercase)
                    .batTextStyle(BatTypography.overline)
                    .foregroundStyle(BatColor.textTertiary)
                    .accessibilityHidden(true)
            }
            HStack(spacing: BatSpacing.sm) {
                if let leadingIcon {
                    BatIconView(icon: leadingIcon, tint: BatColor.textTertiary)
                }
                TextField("", text: $text, prompt: prompt, axis: .vertical)
                    .batTextStyle(BatTypography.body)
                    .foregroundStyle(BatColor.textPrimary)
                    .tint(BatColor.brand)
                    .focused($isFocused)
                    .accessibilityLabel(label ?? placeholder ?? "")
            }
            .padding(.horizontal, BatSpacing.md)
            .padding(.vertical, BatSpacing.sm)
            .frame(minHeight: BatSizing.minTouchTarget)
            .background(BatColor.surface, in: BatShapes.medium)
            .overlay(
                BatShapes.medium.stroke(
                    isFocused ? BatColor.brand : BatColor.borderInput,
                    lineWidth: isFocused ? BatSizing.borderFocused : BatSizing.borderThin
                )
            )
        }
    }

    private var prompt: Text? {
        placeholder.map { Text($0).foregroundColor(BatColor.textTertiary) }
    }
}

#Preview {
    @Previewable @State var empty = ""
    @Previewable @State var filled = "TTC Olympic"
    @Previewable @State var long = "Union Tischtennisclub Wien Donaustadt Allstars"
    BatPreviewMatrix {
        VStack(spacing: BatSpacing.md) {
            BatTextFieldView(text: $empty, placeholder: "Search players (e.g. Huber)", leadingIcon: .search)
            BatTextFieldView(text: $filled, label: "My club")
            BatTextFieldView(text: $long, label: "My club", leadingIcon: .club)
        }
    }
}
