import SwiftUI

struct SettingsDerivedRowView: View {
    let title: String
    let value: String?
    let derivedFrom: DerivedFrom

    var body: some View {
        VStack(alignment: .leading, spacing: BatSpacing.xs) {
            Text(title)
                .textCase(.uppercase)
                .batTextStyle(BatTypography.overline)
                .foregroundStyle(BatColor.textTertiary)
                .accessibilityHidden(true)
            Text(value ?? String(localized: "settings_not_chosen_value"))
                .batTextStyle(BatTypography.bodyStrong)
                .foregroundStyle(BatColor.textPrimary)
            Text(hint)
                .batTextStyle(BatTypography.caption)
                .foregroundStyle(BatColor.textSecondary)
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .accessibilityElement(children: .ignore)
        .accessibilityLabel(title)
        .accessibilityValue(value ?? String(localized: "settings_not_chosen_value"))
        .accessibilityHint(hint)
    }

    private var hint: String {
        switch derivedFrom {
        case .none: String(localized: "settings_derived_none_hint")
        case .player: String(localized: "settings_derived_from_player_hint")
        case .club: String(localized: "settings_derived_from_club_hint")
        }
    }
}

#Preview {
    BatPreviewMatrix {
        VStack(spacing: BatSpacing.lg) {
            SettingsDerivedRowView(title: "Club", value: nil, derivedFrom: .none)
            SettingsDerivedRowView(title: "Club", value: "TTC Olympic", derivedFrom: .player)
            SettingsDerivedRowView(title: "Association", value: "Wiener Tischtennisverband (WTTV)", derivedFrom: .club)
        }
    }
}
