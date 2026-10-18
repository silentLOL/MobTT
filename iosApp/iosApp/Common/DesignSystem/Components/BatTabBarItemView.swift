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
            content
                .padding(.horizontal, BatSpacing.sm)
                .padding(.vertical, BatSpacing.xs)
                .frame(minWidth: BatSizing.minTouchTarget, minHeight: BatSizing.minTouchTarget)
                .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
        .accessibilityLabel(label)
        .accessibilityAddTraits(isSelected ? .isSelected : [])
        .accessibilityShowsLargeContentViewer {
            Label {
                Text(label)
            } icon: {
                icon.image
            }
        }
    }

    /// At accessibility sizes a one-word label no longer fits an equal-width item, so the item shows its icon alone;
    /// VoiceOver keeps the label and the Large Content Viewer shows it on a long press.
    private var content: some View {
        VStack(spacing: 0) {
            BatIconView(icon: icon, tint: tint, size: BatSizing.iconLarge)
                .overlay(alignment: .topTrailing) {
                    if showsLiveIndicator {
                        BatPulsingDotView().offset(x: BatSpacing.xs, y: -BatSpacing.xxs)
                    }
                }
            if !dynamicTypeSize.isAccessibilitySize {
                Text(label)
                    .batTextStyle(BatTypography.overline)
                    .foregroundStyle(tint)
                    .multilineTextAlignment(.center)
                    .lineLimit(1)
                    .minimumScaleFactor(0.7)
            }
        }
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
