import SwiftUI

enum BatBadgeStyle {
    case brand
    case warning
    case live
    case neutral

    var colors: (container: Color, content: Color) {
        switch self {
        case .brand: (BatColor.brandContainer, BatColor.onBrandContainer)
        case .warning: (BatColor.warningContainer, BatColor.onWarningContainer)
        case .live: (BatColor.liveContainer, BatColor.onLiveContainer)
        case .neutral: (BatColor.surfaceMuted, BatColor.textSecondary)
        }
    }
}

struct BatBadgeView: View {
    let text: String
    var style: BatBadgeStyle = .brand

    var body: some View {
        Text(text)
            .batTextStyle(BatTypography.captionStrong)
            .foregroundStyle(style.colors.content)
            .padding(.horizontal, BatSpacing.sm)
            .padding(.vertical, BatSpacing.xxs)
            .background(style.colors.container, in: BatShapes.full)
    }
}

#Preview {
    BatPreviewMatrix {
        HStack(spacing: BatSpacing.sm) {
            BatBadgeView(text: "Brand")
            BatBadgeView(text: "Warning", style: .warning)
            BatBadgeView(text: "Live", style: .live)
            BatBadgeView(text: "Neutral", style: .neutral)
        }
    }
}
