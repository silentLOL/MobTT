import SwiftUI

struct BatIconView: View {
    let icon: BatIcon
    var tint: Color = BatColor.textSecondary
    var size: CGFloat = BatSizing.icon
    var accessibilityText: String?

    @ScaledMetric private var scale: CGFloat = 1

    var body: some View {
        icon.image
            .renderingMode(.template)
            .resizable()
            .scaledToFit()
            .foregroundStyle(tint)
            .frame(width: size * scale, height: size * scale)
            .accessibilityLabel(accessibilityText ?? "")
            .accessibilityHidden(accessibilityText == nil)
    }
}

#Preview {
    BatPreviewMatrix {
        HStack(spacing: BatSpacing.md) {
            BatIconView(icon: .home)
            BatIconView(icon: .live, tint: BatColor.brand)
            BatIconView(icon: .club, size: BatSizing.iconLarge)
            BatIconView(icon: .settings, accessibilityText: "Settings")
        }
    }
}
