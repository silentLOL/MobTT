import SwiftUI

enum BatAvatarTint {
    case brand
    case live
}

/// Decorative: the adjacent name already identifies the person, so it is hidden from accessibility services.
struct BatInitialsAvatarView: View {
    let initials: String
    var tint: BatAvatarTint = .brand

    @ScaledMetric private var size = BatSizing.avatarSmall

    var body: some View {
        Text(initials)
            .batTextStyle(BatTypography.bodyStrong)
            .foregroundStyle(tint == .brand ? BatColor.onBrandContainer : BatColor.onLiveContainer)
            .frame(width: size, height: size)
            .background(tint == .brand ? BatColor.brandContainer : BatColor.liveContainer, in: BatShapes.medium)
            .accessibilityHidden(true)
    }
}

#Preview {
    BatPreviewMatrix {
        HStack(spacing: BatSpacing.md) {
            BatInitialsAvatarView(initials: "MH")
            BatInitialsAvatarView(initials: "SF", tint: .live)
        }
    }
}
