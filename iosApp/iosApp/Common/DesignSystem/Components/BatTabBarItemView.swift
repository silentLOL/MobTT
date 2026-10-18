import SwiftUI

/// `showsLiveIndicator` is decorative; convey live state through `label`.
struct BatTabBarItemView: View {
    let icon: BatIcon
    let label: String
    let isSelected: Bool
    var showsLiveIndicator: Bool = false
    let action: () -> Void

    @Environment(\.dynamicTypeSize) private var dynamicTypeSize

    var body: some View {
        Button(action: action) {
            VStack(spacing: 0) {
                BatIconView(icon: icon, tint: tint, size: BatSizing.iconLarge)
                    .overlay(alignment: .topTrailing) {
                        if showsLiveIndicator {
                            BatPulsingDotView().offset(x: BatSpacing.xs, y: -BatSpacing.xxs)
                        }
                    }
                Text(label)
                    .batTextStyle(BatTypography.overline)
                    .foregroundStyle(tint)
                    .multilineTextAlignment(.center)
                    .lineLimit(1)
                    // One-line labels in equal-width items cannot hold full size at accessibility sizes, so shrink.
                    .minimumScaleFactor(dynamicTypeSize.isAccessibilitySize ? 0.5 : 0.7)
            }
            .padding(.horizontal, BatSpacing.sm)
            .padding(.vertical, BatSpacing.xs)
            .frame(minWidth: BatSizing.minTouchTarget, minHeight: BatSizing.minTouchTarget)
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
        .accessibilityLabel(label)
        .accessibilityAddTraits(isSelected ? .isSelected : [])
    }

    private var tint: Color {
        isSelected ? BatColor.brand : BatColor.textTertiary
    }
}

struct BatTabBarView<Content: View>: View {
    @ViewBuilder let content: () -> Content

    var body: some View {
        VStack(spacing: 0) {
            Divider().overlay(BatColor.border)
            HStack { content() }
                .padding(.horizontal, BatSpacing.sm)
                .padding(.vertical, BatSpacing.xs)
        }
        .background(BatColor.surface.ignoresSafeArea(edges: .bottom))
    }
}

#Preview {
    BatPreviewMatrix {
        BatTabBarView {
            BatTabBarItemView(icon: .home, label: "Home", isSelected: true) {}
            BatTabBarItemView(icon: .club, label: "Club", isSelected: false) {}
            BatTabBarItemView(icon: .search, label: "Players", isSelected: false) {}
            BatTabBarItemView(icon: .live, label: "Live", isSelected: false, showsLiveIndicator: true) {}
        }
    }
}
