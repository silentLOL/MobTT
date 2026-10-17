import SwiftUI

/// The round surface is `BatSizing.iconButton`; the tap area is padded up to the minimum touch target.
struct BatIconButtonView: View {
    let icon: BatIcon
    let accessibilityText: String
    let action: () -> Void

    @ScaledMetric private var scale: CGFloat = 1

    var body: some View {
        Button(action: action) {
            BatIconView(icon: icon)
                .frame(width: BatSizing.iconButton * scale, height: BatSizing.iconButton * scale)
                .background(BatColor.surfaceMuted, in: BatShapes.full)
                .frame(minWidth: BatSizing.minTouchTarget, minHeight: BatSizing.minTouchTarget)
                .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
        .accessibilityLabel(accessibilityText)
    }
}

#Preview {
    BatPreviewMatrix {
        HStack {
            BatIconButtonView(icon: .settings, accessibilityText: "Settings") {}
            BatIconButtonView(icon: .darkMode, accessibilityText: "Dark mode") {}
        }
    }
}
