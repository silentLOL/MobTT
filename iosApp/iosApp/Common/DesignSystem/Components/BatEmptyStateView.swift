import SwiftUI

struct BatEmptyStateView: View {
    let icon: BatIcon
    let title: String
    let message: String

    var body: some View {
        VStack(spacing: BatSpacing.md) {
            BatIconView(icon: icon, tint: BatColor.textTertiary, size: BatSizing.iconLarge)
            Text(title)
                .batTextStyle(BatTypography.title)
                .foregroundStyle(BatColor.textPrimary)
                .accessibilityAddTraits(.isHeader)
            Text(message)
                .batTextStyle(BatTypography.body)
                .foregroundStyle(BatColor.textSecondary)
        }
        .multilineTextAlignment(.center)
        .frame(maxWidth: .infinity)
        .padding(BatSpacing.xxl)
        .accessibilityElement(children: .combine)
    }
}

#Preview {
    BatPreviewMatrix {
        BatEmptyStateView(
            icon: .live,
            title: "Nothing live right now",
            message: "Fixtures in progress will show up here as they are played."
        )
    }
}
