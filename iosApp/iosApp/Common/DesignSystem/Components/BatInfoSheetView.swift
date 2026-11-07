import SwiftUI

/// A link at the bottom of a `BatInfoSheetView`: `title` on the button, `action` to follow it.
struct BatInfoSheetLink {
    let title: String
    let action: () -> Void
}

/// Explains something in a sheet: `title`, `message` and an optional `link`. It closes with a swipe or its close button, and scrolls when the text does not fit. Present it with `.sheet` and `.presentationDetents([.medium, .large])`.
struct BatInfoSheetView: View {
    let title: String
    let message: String
    var link: BatInfoSheetLink?
    let onDismiss: () -> Void

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: BatSpacing.lg) {
                HStack(alignment: .center, spacing: BatSpacing.sm) {
                    Text(title)
                        .batTextStyle(BatTypography.headline)
                        .foregroundStyle(BatColor.textPrimary)
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .accessibilityAddTraits(.isHeader)
                    BatIconButtonView(icon: .close, accessibilityText: String(localized: "core_close_button_cd"), action: onDismiss)
                }
                Text(message)
                    .batTextStyle(BatTypography.body)
                    .foregroundStyle(BatColor.textSecondary)
                    .fixedSize(horizontal: false, vertical: true)
                if let link {
                    BatSecondaryButtonView(title: link.title, action: link.action)
                }
            }
            .padding(BatSpacing.lg)
        }
        .background(BatColor.background)
    }
}

#Preview {
    BatPreviewMatrix {
        BatInfoSheetView(
            title: "About the Rating history",
            message: "The line is the estimate of the Player's playing strength.\n\nThe ± number is how certain that estimate is.",
            link: BatInfoSheetLink(title: "Learn more at Ratings Central") {},
            onDismiss: {}
        )
    }
}
