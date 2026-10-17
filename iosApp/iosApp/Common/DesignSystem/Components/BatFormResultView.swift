import SwiftUI

enum BatFormResult {
    case win
    case loss
}

/// `label` is the short visible letter; `accessibilityText` spells the outcome out for screen readers.
struct BatFormResultView: View {
    let result: BatFormResult
    let label: String
    let accessibilityText: String

    @ScaledMetric private var size = BatSizing.formResult

    var body: some View {
        Text(label)
            .batTextStyle(BatTypography.bodyStrong)
            .foregroundStyle(BatColor.onSuccess)
            .frame(width: size, height: size)
            .background(result == .win ? BatColor.success : BatColor.danger, in: BatShapes.small)
            .accessibilityElement(children: .ignore)
            .accessibilityLabel(accessibilityText)
    }
}

#Preview {
    BatPreviewMatrix {
        HStack(spacing: BatSpacing.sm) {
            BatFormResultView(result: .win, label: "W", accessibilityText: "Win")
            BatFormResultView(result: .win, label: "W", accessibilityText: "Win")
            BatFormResultView(result: .loss, label: "L", accessibilityText: "Loss")
            BatFormResultView(result: .win, label: "W", accessibilityText: "Win")
        }
    }
}
