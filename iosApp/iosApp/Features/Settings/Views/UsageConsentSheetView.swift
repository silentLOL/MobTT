import SwiftUI

/// The one-time consent step: shown once a Player or Club is chosen and Usage Consent is still unanswered.
struct UsageConsentSheetView: View {
    let onAction: (UsageConsentAction) -> Void

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: BatSpacing.lg) {
                Text("consent_title")
                    .batTextStyle(BatTypography.headline)
                    .foregroundStyle(BatColor.textPrimary)
                    .accessibilityAddTraits(.isHeader)
                Text("consent_body")
                    .batTextStyle(BatTypography.body)
                    .foregroundStyle(BatColor.textSecondary)
                BatPrimaryButtonView(title: String(localized: "consent_allow_button")) { onAction(.allowClicked) }
                BatSecondaryButtonView(title: String(localized: "consent_not_now_button")) { onAction(.notNowClicked) }
            }
            .padding(BatSpacing.lg)
            .frame(maxWidth: BatSizing.maxContentWidth)
            .frame(maxWidth: .infinity)
        }
        .batBackground()
    }
}

#Preview {
    BatPreviewMatrix {
        UsageConsentSheetView(onAction: { _ in })
    }
}
