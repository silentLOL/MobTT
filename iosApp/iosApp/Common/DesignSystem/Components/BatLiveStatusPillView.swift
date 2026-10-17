import SwiftUI

struct BatLiveStatusPillView: View {
    let text: String
    let isLive: Bool

    var body: some View {
        HStack(spacing: BatSpacing.xs) {
            if isLive {
                BatPulsingDotView()
            }
            Text(text)
                .batTextStyle(BatTypography.overline)
                .foregroundStyle(isLive ? BatColor.onLiveContainer : BatColor.textSecondary)
        }
        .padding(.horizontal, BatSpacing.sm)
        .padding(.vertical, BatSpacing.xs)
        .background(isLive ? BatColor.liveContainer : BatColor.surfaceMuted, in: BatShapes.full)
        .accessibilityElement(children: .combine)
    }
}

struct BatPulsingDotView: View {
    @Environment(\.accessibilityReduceMotion) private var reduceMotion
    @State private var dimmed = false

    var body: some View {
        Circle()
            .fill(BatColor.liveIndicator)
            .frame(width: BatSizing.liveIndicator, height: BatSizing.liveIndicator)
            .opacity(dimmed ? 0.3 : 1)
            .animation(reduceMotion ? nil : .easeInOut(duration: 0.9).repeatForever(autoreverses: true), value: dimmed)
            .onAppear { dimmed = true }
            .accessibilityHidden(true)
    }
}

#Preview {
    BatPreviewMatrix {
        VStack(alignment: .leading, spacing: BatSpacing.sm) {
            BatLiveStatusPillView(text: "LIVE • Table 2", isLive: true)
            BatLiveStatusPillView(text: "Finished • Table 1", isLive: false)
        }
    }
}
