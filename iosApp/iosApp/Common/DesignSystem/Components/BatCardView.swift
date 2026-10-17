import SwiftUI

/// Pass `onTap` to make the whole card a single tappable target.
struct BatCardView<Content: View>: View {
    var padding: CGFloat = BatSpacing.lg
    var onTap: (() -> Void)?
    @ViewBuilder let content: () -> Content

    var body: some View {
        if let onTap {
            Button(action: onTap) { card }
                .buttonStyle(.plain)
        } else {
            card
        }
    }

    private var card: some View {
        VStack(alignment: .leading, spacing: 0) { content() }
            .padding(padding)
            .frame(maxWidth: .infinity, alignment: .leading)
            .background(BatColor.surface, in: BatShapes.large)
            .overlay(BatShapes.large.stroke(BatColor.border, lineWidth: BatSizing.borderThin))
            .batShadow(.sm)
            .accessibilityElement(children: .combine)
    }
}

#Preview {
    BatPreviewMatrix {
        VStack(spacing: BatSpacing.md) {
            BatCardView {
                Text("Static card").batTextStyle(BatTypography.bodyStrong)
            }
            BatCardView(onTap: {}) {
                Text("Tappable card").batTextStyle(BatTypography.bodyStrong)
            }
        }
    }
}

#Preview("Long content") {
    BatPreviewMatrix {
        BatCardView(padding: BatSpacing.xl, onTap: {}) {
            Text("Match day").batTextStyle(BatTypography.bodyStrong)
            Text("A longer description that wraps onto several lines so the padding and border can be checked at larger text sizes.")
                .batTextStyle(BatTypography.body)
                .foregroundStyle(BatColor.textSecondary)
        }
    }
}
