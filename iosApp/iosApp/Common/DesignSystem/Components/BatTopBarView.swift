import SwiftUI

struct BatTopBarView<Actions: View>: View {
    let title: String
    var badge: String?
    var subtitle: String?
    @ViewBuilder let actions: () -> Actions

    @Environment(\.dynamicTypeSize) private var dynamicTypeSize

    private var titleAndBadge: some View {
        let layout = dynamicTypeSize.isAccessibilitySize
            ? AnyLayout(VStackLayout(alignment: .leading, spacing: BatSpacing.xs))
            : AnyLayout(HStackLayout(spacing: BatSpacing.sm))
        return layout {
            Text(title)
                .batTextStyle(BatTypography.subtitle)
                .foregroundStyle(BatColor.textPrimary)
                .fixedSize(horizontal: false, vertical: true)
                .accessibilityAddTraits(.isHeader)
            if let badge {
                BatBadgeView(text: badge)
            }
        }
    }

    private var titleColumn: some View {
        VStack(alignment: .leading, spacing: 0) {
            titleAndBadge
            if let subtitle {
                Text(subtitle)
                    .batTextStyle(BatTypography.caption)
                    .foregroundStyle(BatColor.textSecondary)
                    .fixedSize(horizontal: false, vertical: true)
            }
        }
    }

    @ViewBuilder private var header: some View {
        if dynamicTypeSize.isAccessibilitySize {
            VStack(alignment: .leading, spacing: BatSpacing.sm) {
                HStack(spacing: BatSpacing.md) {
                    BatLogoMarkView()
                    Spacer(minLength: 0)
                    actions()
                }
                titleColumn
            }
        } else {
            HStack(spacing: BatSpacing.md) {
                BatLogoMarkView()
                titleColumn
                Spacer(minLength: 0)
                actions()
            }
        }
    }

    var body: some View {
        VStack(spacing: 0) {
            header
                .padding(.horizontal, BatSpacing.lg)
                .padding(.vertical, BatSpacing.sm)
                .frame(maxWidth: .infinity, alignment: .leading)
            Divider().overlay(BatColor.border)
        }
        .background(BatColor.surface.ignoresSafeArea(edges: .top))
    }
}

extension BatTopBarView where Actions == EmptyView {
    init(title: String, badge: String? = nil, subtitle: String? = nil) {
        self.init(title: title, badge: badge, subtitle: subtitle) { EmptyView() }
    }
}

#Preview {
    BatPreviewMatrix {
        VStack(spacing: BatSpacing.md) {
            BatTopBarView(title: "Bat Mate", badge: "Pro", subtitle: "Vienna Table Tennis Association (WTTV)") {
                BatIconButtonView(icon: .darkMode, accessibilityText: "Dark mode") {}
                BatIconButtonView(icon: .settings, accessibilityText: "Settings") {}
            }
            BatTopBarView(title: "Bat Mate")
        }
    }
}

#Preview("Long content") {
    BatPreviewMatrix {
        VStack(spacing: BatSpacing.md) {
            BatTopBarView(
                title: "Bat Mate Championship Overview",
                badge: "Pro",
                subtitle: "Lower Austrian Table Tennis Association, Division 1 West"
            ) {
                BatIconButtonView(icon: .settings, accessibilityText: "Settings") {}
            }
            BatTopBarView(title: "Bat Mate", badge: "Beta")
        }
    }
}
