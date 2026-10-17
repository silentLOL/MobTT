import SwiftUI

struct SettingsChoiceRowView: View {
    let title: String
    let value: String?
    let onChooseClick: () -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: BatSpacing.xs) {
            Text(title)
                .textCase(.uppercase)
                .batTextStyle(BatTypography.overline)
                .foregroundStyle(BatColor.textTertiary)
                .accessibilityHidden(true)
            HStack(spacing: BatSpacing.md) {
                Text(value ?? String(localized: "settings_not_chosen_value"))
                    .batTextStyle(BatTypography.bodyStrong)
                    .foregroundStyle(value == nil ? BatColor.textSecondary : BatColor.textPrimary)
                    .frame(maxWidth: .infinity, alignment: .leading)
                BatSecondaryButtonView(
                    title: value == nil ? String(localized: "settings_choose_button") : String(localized: "settings_change_button"),
                    action: onChooseClick
                )
            }
        }
        .accessibilityElement(children: .contain)
        .accessibilityLabel(title)
    }
}

#Preview {
    BatPreviewMatrix {
        VStack(spacing: BatSpacing.lg) {
            SettingsChoiceRowView(title: "My Player", value: nil, onChooseClick: {})
            SettingsChoiceRowView(title: "My Player", value: "Michael Huber", onChooseClick: {})
            SettingsChoiceRowView(title: "My Club", value: "Union Tischtennisclub St. Pölten Nord-West", onChooseClick: {})
        }
    }
}
