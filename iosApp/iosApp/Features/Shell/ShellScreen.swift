import SwiftUI

private struct ClubDetailRoute: Hashable {
    let clubId: String
}

struct ShellScreen: View {
    @Binding var appearance: AppearanceChoice
    @State private var selectedTab = ShellTab.home
    @State private var isSettingsPresented = false
    @State private var homePath: [ClubDetailRoute] = []

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
                        homePath.removeAll()
                    }
                }
            }
        } content: {
            // The page style keeps the system tab bar out of the way, which the design system's own bar replaces.
            TabView(selection: $selectedTab) {
                NavigationStack(path: $homePath) {
                    HomeRoute(
                        onOpenSettings: { isSettingsPresented = true },
                        onOpenMyClub: { selectedTab = .club },
                        onOpenClub: { homePath.append(ClubDetailRoute(clubId: $0)) }
                    )
                    .toolbar(.hidden, for: .navigationBar)
                    .navigationDestination(for: ClubDetailRoute.self) { route in
                        ClubRoute(source: .specific(clubId: route.clubId), onOpenSettings: { isSettingsPresented = true })
                            .navigationTitle(String(localized: "club_hero_overline"))
                            .navigationBarTitleDisplayMode(.inline)
                            .batBackground()
                    }
                }
                .tag(ShellTab.home)
                ClubRoute(source: .mine, onOpenSettings: { isSettingsPresented = true }).tag(ShellTab.club)
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
