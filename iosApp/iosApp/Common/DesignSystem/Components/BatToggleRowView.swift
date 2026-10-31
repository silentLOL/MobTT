import SwiftUI

/// The whole row is the target and the switch is part of it: state is announced by the Toggle itself.
struct BatToggleRowView: View {
    let label: String
    var supportingText: String?
    @Binding var isOn: Bool

    var body: some View {
        Toggle(isOn: $isOn) {
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
        }
        .tint(BatColor.brand)
        .padding(.vertical, BatSpacing.sm)
        .frame(minHeight: BatSizing.minTouchTarget)
    }
}

#Preview {
    @Previewable @State var on = true
    @Previewable @State var off = false
    BatPreviewMatrix {
        VStack(spacing: 0) {
            BatToggleRowView(label: "Usage statistics", supportingText: "Anonymous", isOn: $on)
            BatToggleRowView(label: "Usage statistics", isOn: $off)
            BatToggleRowView(
                label: "Nutzungsstatistiken und Absturzberichte für eine sehr lange Beschriftung",
                supportingText: "Anonym. Enthält nie Namen oder Eingaben, auch nicht bei sehr großer Schrift.",
                isOn: $off
            )
        }
    }
}
