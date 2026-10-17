import SwiftUI

/// Label and value are announced as one element, e.g. "QTTR points, 1842".
struct BatStatView: View {
    let label: String
    let value: String
    var onHero: Bool = false

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            Text(label)
                .textCase(.uppercase)
                .batTextStyle(BatTypography.overline)
                .foregroundStyle(onHero ? BatColor.onHeroMuted : BatColor.textTertiary)
            Text(value)
                .batTextStyle(BatTypography.headline)
                .foregroundStyle(onHero ? BatColor.onHero : BatColor.textPrimary)
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .accessibilityElement(children: .ignore)
        .accessibilityLabel("\(label), \(value)")
    }
}

#Preview {
    BatPreviewMatrix {
        HStack(spacing: BatSpacing.lg) {
            BatStatView(label: "QTTR", value: "1842")
            BatStatView(label: "Rank", value: "#45")
            BatStatView(label: "Record", value: "34:12")
        }
    }
}
