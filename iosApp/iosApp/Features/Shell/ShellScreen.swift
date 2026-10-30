import SwiftUI

private struct ClubDetailRoute: Hashable {
    let clubId: String
}

private struct TeamDestination: Hashable {
    let teamId: String
}

struct ShellScreen: View {
    @Binding var appearance: AppearanceChoice
    @State private var selectedTab = ShellTab.home
    @State private var isSettingsPresented = false
    @State private var homePath = NavigationPath()
    @State private var clubPath = NavigationPath()

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
                        homePath = NavigationPath()
                        clubPath = NavigationPath()
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
                        onOpenClub: { homePath.append(ClubDetailRoute(clubId: $0)) },
                        onOpenTeam: { homePath.append(TeamDestination(teamId: $0)) }
                    )
                    .toolbar(.hidden, for: .navigationBar)
                    .navigationDestination(for: ClubDetailRoute.self) { route in
                        ClubRoute(
                            source: .specific(clubId: route.clubId),
                            onOpenSettings: { isSettingsPresented = true },
                            onOpenTeam: { homePath.append(TeamDestination(teamId: $0)) }
                        )
                        .navigationTitle(String(localized: "club_hero_overline"))
                        .navigationBarTitleDisplayMode(.inline)
                        .batBackground()
                    }
                    .navigationDestination(for: TeamDestination.self) { route in
                        teamDetail(route)
                    }
                }
                .tag(ShellTab.home)
                NavigationStack(path: $clubPath) {
                    ClubRoute(
                        source: .mine,
                        onOpenSettings: { isSettingsPresented = true },
                        onOpenTeam: { clubPath.append(TeamDestination(teamId: $0)) }
                    )
                    .toolbar(.hidden, for: .navigationBar)
                    .navigationDestination(for: TeamDestination.self) { route in
                        teamDetail(route)
                    }
                }
                .tag(ShellTab.club)
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

extension ShellScreen {
    fileprivate func teamDetail(_ route: TeamDestination) -> some View {
        TeamDetailRoute(teamId: route.teamId)
            .navigationTitle(String(localized: "team_detail_hero_overline"))
            .navigationBarTitleDisplayMode(.inline)
            .batBackground()
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
