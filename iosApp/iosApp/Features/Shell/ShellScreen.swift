import SwiftUI

struct ShellScreen: View {
    @Binding var appearance: AppearanceChoice
    @State private var selectedTab = ShellTab.home
    @State private var isSettingsPresented = false

    var body: some View {
        BatScreenScaffoldView {
            BatTopBarView(title: String(localized: "core_app_title")) {
                BatIconButtonView(icon: .settings, accessibilityText: String(localized: "core_settings_button_cd")) {
                    isSettingsPresented = true
                }
            }
        } bottomBar: {
            BatTabBarView {
                ForEach(ShellTab.allCases, id: \.self) { tab in
                    BatTabBarItemView(
                        icon: tab.icon,
                        label: String(localized: tab.label),
                        isSelected: tab == selectedTab
                    ) {
                        selectedTab = tab
                    }
                }
            }
        } content: {
            // The page style keeps the system tab bar out of the way, which the design system's own bar replaces.
            TabView(selection: $selectedTab) {
                HomeRoute(onOpenSettings: { isSettingsPresented = true }).tag(ShellTab.home)
                ClubScreen().tag(ShellTab.club)
                PlayersScreen().tag(ShellTab.players)
                LiveScreen().tag(ShellTab.live)
            }
            .tabViewStyle(.page(indexDisplayMode: .never))
            .scrollDisabled(true)
        }
        .sheet(isPresented: $isSettingsPresented) {
            SettingsRoute(appearance: $appearance) { isSettingsPresented = false }
                .presentationDetents([.medium, .large])
                .presentationDragIndicator(.visible)
                .presentationBackground(BatColor.background)
        }
    }
}

#Preview {
    @Previewable @State var appearance = AppearanceChoice.system
    let _ = PreviewKoin.started
    ShellScreen(appearance: $appearance)
}

#Preview("Dark") {
    @Previewable @State var appearance = AppearanceChoice.dark
    let _ = PreviewKoin.started
    ShellScreen(appearance: $appearance)
        .preferredColorScheme(.dark)
}
