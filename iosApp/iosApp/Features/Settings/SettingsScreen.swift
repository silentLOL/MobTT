import SwiftUI

struct SettingsRoute: View {
    @Binding var appearance: AppearanceChoice
    let onOpenLicenses: () -> Void

    @Environment(\.openURL) private var openURL
    @State private var viewModel = SettingsViewModel()

    var body: some View {
        let state = viewModel.uiState
        SettingsScreen(state: state, appearance: $appearance, onAction: viewModel.onAction)
            // Inside a picker the screen's own back arrow, the left-edge swipe and the VoiceOver escape step back first,
            // so the system's back button (and its swipe) stays hidden until the picker is closed.
            .navigationBarBackButtonHidden(state.picker != nil)
            .onAppear { viewModel.screenShown() }
            .task { await viewModel.observe() }
            .task {
                for await event in viewModel.events {
                    switch event {
                    case .openSupportLink(let url): openURL(url)
                    case .openLicenses: onOpenLicenses()
                    }
                }
            }
    }
}

struct SettingsScreen: View {
    let state: SettingsUiState
    @Binding var appearance: AppearanceChoice
    let onAction: (SettingsAction) -> Void

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: BatSpacing.lg) {
                if let picker = state.picker {
                    pickerContent(picker)
                } else {
                    mainContent
                }
            }
            .padding(.horizontal, BatSpacing.lg)
            .padding(.vertical, BatSpacing.lg)
            .frame(maxWidth: BatSizing.maxContentWidth)
            .frame(maxWidth: .infinity)
        }
        .batBackground()
        .overlay(alignment: .leading) { pickerBackSwipeStrip }
        .modifier(PickerEscapeModifier(isActive: state.picker != nil) { onAction(.pickerBackClicked) })
    }

    /// A thin strip at the left edge that stands in for the system's back swipe while a picker is open.
    @ViewBuilder
    private var pickerBackSwipeStrip: some View {
        if state.picker != nil {
            Color.clear
                .frame(width: BatSpacing.lg)
                .contentShape(Rectangle())
                .gesture(
                    DragGesture(minimumDistance: BatSpacing.md).onEnded { drag in
                        if drag.translation.width > Self.backSwipeDistance { onAction(.pickerBackClicked) }
                    }
                )
        }
    }

    private static let backSwipeDistance: CGFloat = 60

    @ViewBuilder
    private var mainContent: some View {
        SettingsChoiceRowView(
            title: String(localized: "settings_my_player_title"),
            value: state.myPlayerName,
            onChooseClick: { onAction(.choosePlayerClicked) }
        )
        SettingsChoiceRowView(
            title: String(localized: "settings_my_club_title"),
            value: state.myClubName,
            onChooseClick: { onAction(.chooseClubClicked) }
        )
        SettingsDerivedRowView(
            title: String(localized: "settings_club_title"),
            value: state.derivedClubName,
            derivedFrom: state.derivedFrom
        )
        SettingsDerivedRowView(
            title: String(localized: "settings_association_title"),
            value: state.derivedAssociationName,
            derivedFrom: state.derivedFrom
        )
        VStack(alignment: .leading, spacing: 0) {
            BatSectionHeaderView(title: String(localized: "settings_appearance_title"))
            ForEach(AppearanceChoice.allCases, id: \.self) { choice in
                BatSelectableRowView(label: String(localized: choice.label), isSelected: choice == appearance) {
                    appearance = choice
                    onAction(.appearanceSelected(choice))
                }
            }
        }
        BatToggleRowView(
            label: String(localized: "settings_usage_consent_title"),
            supportingText: String(localized: "settings_usage_consent_supporting"),
            isOn: Binding(
                get: { state.usageConsentAllowed },
                set: { onAction(.usageConsentToggled($0)) }
            )
        )
        aboutSection
    }

    private var aboutSection: some View {
        VStack(alignment: .leading, spacing: BatSpacing.sm) {
            BatSectionHeaderView(title: String(localized: "settings_about_title"))
            licensesRow
            if state.showSupportLink {
                supportRow
            }
            dataSourceCredit
        }
    }

    private var licensesRow: some View {
        Button { onAction(.licensesClicked) } label: {
            HStack(spacing: BatSpacing.md) {
                Text("settings_licenses_title")
                    .batTextStyle(BatTypography.bodyStrong)
                    .foregroundStyle(BatColor.textPrimary)
                    .frame(maxWidth: .infinity, alignment: .leading)
                BatIconView(icon: .arrowForward, tint: BatColor.textSecondary)
            }
            .frame(minHeight: BatSizing.minTouchTarget)
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
    }

    private var supportRow: some View {
        Button { onAction(.supportClicked) } label: {
            HStack(spacing: BatSpacing.md) {
                Text("settings_support_title")
                    .batTextStyle(BatTypography.bodyStrong)
                    .foregroundStyle(BatColor.textPrimary)
                    .frame(maxWidth: .infinity, alignment: .leading)
                BatIconView(icon: .arrowForward, tint: BatColor.textSecondary)
            }
            .frame(minHeight: BatSizing.minTouchTarget)
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
        .accessibilityHint(Text("settings_support_open_cd"))
        .accessibilityAddTraits(.isLink)
    }

    private var dataSourceCredit: some View {
        Link(destination: URL(string: "https://oettv.xttv.at")!) {
            Text("settings_data_source_credit")
                .batTextStyle(BatTypography.caption)
                .foregroundStyle(BatColor.textSecondary)
                .frame(maxWidth: .infinity, alignment: .leading)
        }
        .accessibilityHint(Text("settings_data_source_link_label"))
    }

    @ViewBuilder
    private func pickerContent(_ picker: SettingsPickerUi) -> some View {
        if let drillDown = picker.playerDrillDown {
            header(
                title: drillDown.step == .player ? "settings_picker_player_title" : drillDown.step.title,
                icon: .arrowBack,
                accessibilityLabel: drillDown.step == .association ? "settings_picker_back_cd" : "player_picker_back_cd"
            ) { onAction(.pickerBackClicked) }
            PlayerDrillDownView(
                drillDown: drillDown,
                onQueryChange: { onAction(.pickerQueryChanged($0)) },
                onEntryClick: { onAction(.pickerEntryClicked(id: $0)) }
            )
        } else {
            header(title: "settings_picker_club_title", icon: .arrowBack, accessibilityLabel: "settings_picker_back_cd") {
                onAction(.pickerBackClicked)
            }
            DirectoryPickerView(
                query: picker.query,
                onQueryChange: { onAction(.pickerQueryChanged($0)) },
                sections: [DirectorySectionUi(title: nil, entries: picker.entries)],
                onEntryClick: { _, entry in onAction(.pickerEntryClicked(id: entry.id)) }
            )
        }
    }

    private func header(title: LocalizedStringKey, icon: BatIcon, accessibilityLabel: String.LocalizationValue, onTap: @escaping () -> Void) -> some View {
        HStack {
            Text(title)
                .batTextStyle(BatTypography.headline)
                .foregroundStyle(BatColor.textPrimary)
                .accessibilityAddTraits(.isHeader)
            Spacer(minLength: BatSpacing.sm)
            BatIconButtonView(icon: icon, accessibilityText: String(localized: accessibilityLabel), action: onTap)
        }
    }
}

/// VoiceOver's two-finger scrub steps back inside a picker, and only there: elsewhere it still goes back a screen.
private struct PickerEscapeModifier: ViewModifier {
    let isActive: Bool
    let action: () -> Void

    @ViewBuilder
    func body(content: Content) -> some View {
        if isActive {
            content.accessibilityAction(.escape, action)
        } else {
            content
        }
    }
}

private let chosenPlayerState = SettingsUiState(
    myPlayerName: "Michael Huber",
    derivedFrom: .player,
    derivedClubName: "TTC Olympic",
    derivedAssociationName: "WTTV"
)

#Preview("Empty") {
    @Previewable @State var appearance = AppearanceChoice.system
    SettingsScreen(state: SettingsUiState(), appearance: $appearance, onAction: { _ in })
}

private let withSupportLinkState: SettingsUiState = {
    var state = chosenPlayerState
    state.showSupportLink = true
    return state
}()

#Preview("With the coffee row") {
    @Previewable @State var appearance = AppearanceChoice.system
    SettingsScreen(state: withSupportLinkState, appearance: $appearance, onAction: { _ in })
}

#Preview("My Player, dark") {
    @Previewable @State var appearance = AppearanceChoice.dark
    SettingsScreen(state: chosenPlayerState, appearance: $appearance, onAction: { _ in })
        .preferredColorScheme(.dark)
}

#Preview("My Club") {
    @Previewable @State var appearance = AppearanceChoice.light
    SettingsScreen(
        state: SettingsUiState(
            myClubName: "Nussdorf TTC",
            derivedFrom: .club,
            derivedClubName: "Nussdorf TTC",
            derivedAssociationName: "WTTV"
        ),
        appearance: $appearance,
        onAction: { _ in }
    )
}

#Preview("Long name, 200% text") {
    @Previewable @State var appearance = AppearanceChoice.light
    SettingsScreen(
        state: SettingsUiState(
            myPlayerName: "Katharina Habsburg-Lothringen-Esterházy",
            derivedFrom: .player,
            derivedClubName: "Union Tischtennisclub St. Pölten Nord-West",
            derivedAssociationName: "NÖTTV"
        ),
        appearance: $appearance,
        onAction: { _ in }
    )
    .dynamicTypeSize(.accessibility2)
}

#Preview("Club picker") {
    @Previewable @State var appearance = AppearanceChoice.system
    SettingsScreen(
        state: SettingsUiState(
            picker: SettingsPickerUi(
                kind: .club,
                query: "",
                entries: [DirectoryEntryUi(id: "nussdorf", title: "Nussdorf TTC", subtitle: nil, selected: true)]
            )
        ),
        appearance: $appearance,
        onAction: { _ in }
    )
}

#Preview("Player picker, Verband step") {
    @Previewable @State var appearance = AppearanceChoice.system
    SettingsScreen(
        state: SettingsUiState(
            picker: SettingsPickerUi(
                kind: .player,
                query: "",
                entries: [],
                playerDrillDown: PlayerDrillDownUi(
                    step: .association,
                    contextName: nil,
                    query: "",
                    entries: [
                        DirectoryEntryUi(id: "wttv", title: "WTTV", subtitle: nil, selected: false),
                        DirectoryEntryUi(id: "noettv", title: "NÖTTV", subtitle: nil, selected: false),
                    ]
                )
            )
        ),
        appearance: $appearance,
        onAction: { _ in }
    )
}

#Preview("Player picker, Player step") {
    @Previewable @State var appearance = AppearanceChoice.system
    let entries = [
        DirectoryEntryUi(id: "huber", title: "Michael Huber", subtitle: "TTC Olympic", selected: true),
        DirectoryEntryUi(id: "gruber", title: "Anna Gruber", subtitle: "TTC Olympic", selected: false),
    ]
    SettingsScreen(
        state: SettingsUiState(
            picker: SettingsPickerUi(
                kind: .player,
                query: "",
                entries: entries,
                playerDrillDown: PlayerDrillDownUi(step: .player, contextName: "1. Landesliga Herren", query: "", entries: entries)
            )
        ),
        appearance: $appearance,
        onAction: { _ in }
    )
}
