import SwiftUI

struct SettingsScreen: View {
    @Binding var appearance: AppearanceChoice
    let onDismiss: () -> Void

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: BatSpacing.lg) {
                HStack {
                    Text("settings_title")
                        .batTextStyle(BatTypography.headline)
                        .foregroundStyle(BatColor.textPrimary)
                        .accessibilityAddTraits(.isHeader)
                    Spacer(minLength: BatSpacing.sm)
                    BatIconButtonView(icon: .close, accessibilityText: String(localized: "core_close_button_cd"), action: onDismiss)
                }
                VStack(alignment: .leading, spacing: 0) {
                    BatSectionHeaderView(title: String(localized: "settings_appearance_title"))
                    ForEach(AppearanceChoice.allCases, id: \.self) { choice in
                        BatSelectableRowView(label: String(localized: choice.label), isSelected: choice == appearance) {
                            appearance = choice
                        }
                    }
                }
                BatPrimaryButtonView(title: String(localized: "settings_done_button"), action: onDismiss)
            }
            .padding(.horizontal, BatSpacing.lg)
            .padding(.vertical, BatSpacing.lg)
            .frame(maxWidth: BatSizing.maxContentWidth)
            .frame(maxWidth: .infinity)
        }
        .batBackground()
    }
}

#Preview("System") {
    @Previewable @State var appearance = AppearanceChoice.system
    SettingsScreen(appearance: $appearance, onDismiss: {})
}

#Preview("Dark selected") {
    @Previewable @State var appearance = AppearanceChoice.dark
    SettingsScreen(appearance: $appearance, onDismiss: {})
        .preferredColorScheme(.dark)
}

#Preview("200% text") {
    @Previewable @State var appearance = AppearanceChoice.light
    SettingsScreen(appearance: $appearance, onDismiss: {})
        .dynamicTypeSize(.accessibility2)
}
