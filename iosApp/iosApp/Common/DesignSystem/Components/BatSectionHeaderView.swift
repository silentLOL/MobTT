import SwiftUI

/// `actionLabel` and `onAction` must be provided together to show the trailing action.
struct BatSectionHeaderView: View {
    let title: String
    var actionLabel: String?
    var onAction: (() -> Void)?

    var body: some View {
        HStack(alignment: .firstTextBaseline) {
            Text(title)
                .textCase(.uppercase)
                .batTextStyle(BatTypography.bodyStrong)
                .foregroundStyle(BatColor.textPrimary)
                .accessibilityAddTraits(.isHeader)
            Spacer(minLength: BatSpacing.sm)
            if let actionLabel, let onAction {
                Button(action: onAction) {
                    Text(actionLabel)
                        .batTextStyle(BatTypography.captionStrong)
                        .foregroundStyle(BatColor.brand)
                        .padding(.horizontal, BatSpacing.sm)
                        .frame(minHeight: BatSizing.minTouchTarget)
                        .contentShape(Rectangle())
                }
                .buttonStyle(.plain)
            }
        }
        .frame(maxWidth: .infinity)
    }
}

#Preview {
    BatPreviewMatrix {
        VStack(spacing: BatSpacing.md) {
            BatSectionHeaderView(title: "Upcoming matches")
            BatSectionHeaderView(title: "Favorites", actionLabel: "+ Add") {}
        }
    }
}
