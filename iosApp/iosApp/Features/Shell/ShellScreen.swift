import SwiftUI

private struct ClubDetailRoute: Hashable {
    let clubId: String
}

private struct PlayerDestination: Hashable {
    let playerId: String
}

private struct TeamDestination: Hashable {
    let teamId: String
}

private struct LeagueDestination: Hashable {
    let leagueId: String
    /// The Team to highlight, when opened from its Team screen.
    let highlightTeamId: String?
}

struct ShellScreen: View {
    @Binding var appearance: AppearanceChoice
    @State private var selectedTab = ShellTab.home
    @State private var isSettingsPresented = false
    @State private var homePath = NavigationPath()
    @State private var clubPath = NavigationPath()
    @State private var playersPath = NavigationPath()

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
                        playersPath = NavigationPath()
                    }
                }
            }
        } content: {
            // The page style keeps the system tab bar out of the way, which the design system's own bar replaces.
            TabView(selection: $selectedTab) {
                NavigationStack(path: $homePath) {
                    HomeRoute(
                        onOpenSettings: { isSettingsPresented = true },
                        onOpenClub: { homePath.append(ClubDetailRoute(clubId: $0)) },
                        onOpenTeam: { homePath.append(TeamDestination(teamId: $0)) },
                        onOpenPlayer: { homePath.append(PlayerDestination(playerId: $0)) }
                    )
                    .toolbar(.hidden, for: .navigationBar)
                    .navigationDestination(for: ClubDetailRoute.self) { route in
                        ClubRoute(
                            source: .specific(clubId: route.clubId),
                            onOpenSettings: { isSettingsPresented = true },
                            onOpenTeam: { homePath.append(TeamDestination(teamId: $0)) },
                            onOpenPlayer: { homePath.append(PlayerDestination(playerId: $0)) }
                        )
                        .navigationTitle(String(localized: "club_hero_overline"))
                        .navigationBarTitleDisplayMode(.inline)
                        .batBackground()
                    }
                    .navigationDestination(for: TeamDestination.self) { route in
                        teamDetail(route, path: $homePath)
                    }
                    .navigationDestination(for: LeagueDestination.self) { route in
                        leagueDetail(route, path: $homePath)
                    }
                    .navigationDestination(for: PlayerDestination.self) { route in
                        playerDetail(route, path: $homePath)
                    }
                }
                .tag(ShellTab.home)
                NavigationStack(path: $clubPath) {
                    ClubRoute(
                        source: .mine,
                        onOpenSettings: { isSettingsPresented = true },
                        onOpenTeam: { clubPath.append(TeamDestination(teamId: $0)) },
                        onOpenPlayer: { clubPath.append(PlayerDestination(playerId: $0)) }
                    )
                    .toolbar(.hidden, for: .navigationBar)
                    .navigationDestination(for: PlayerDestination.self) { route in
                        playerDetail(route, path: $clubPath)
                    }
                    .navigationDestination(for: ClubDetailRoute.self) { route in
                        ClubRoute(
                            source: .specific(clubId: route.clubId),
                            onOpenSettings: { isSettingsPresented = true },
                            onOpenTeam: { clubPath.append(TeamDestination(teamId: $0)) },
                            onOpenPlayer: { clubPath.append(PlayerDestination(playerId: $0)) }
                        )
                        .navigationTitle(String(localized: "club_hero_overline"))
                        .navigationBarTitleDisplayMode(.inline)
                        .batBackground()
                    }
                    .navigationDestination(for: TeamDestination.self) { route in
                        teamDetail(route, path: $clubPath)
                    }
                    .navigationDestination(for: LeagueDestination.self) { route in
                        leagueDetail(route, path: $clubPath)
                    }
                }
                .tag(ShellTab.club)
                NavigationStack(path: $playersPath) {
                    PlayersRoute(onOpenPlayer: { playersPath.append(PlayerDestination(playerId: $0)) })
                        .toolbar(.hidden, for: .navigationBar)
                        .navigationDestination(for: PlayerDestination.self) { route in
                            playerDetail(route, path: $playersPath)
                        }
                        .navigationDestination(for: ClubDetailRoute.self) { route in
                            ClubRoute(
                                source: .specific(clubId: route.clubId),
                                onOpenSettings: { isSettingsPresented = true },
                                onOpenTeam: { playersPath.append(TeamDestination(teamId: $0)) },
                                onOpenPlayer: { playersPath.append(PlayerDestination(playerId: $0)) }
                            )
                            .navigationTitle(String(localized: "club_hero_overline"))
                            .navigationBarTitleDisplayMode(.inline)
                            .batBackground()
                        }
                        .navigationDestination(for: TeamDestination.self) { route in
                            teamDetail(route, path: $playersPath)
                        }
                        .navigationDestination(for: LeagueDestination.self) { route in
                            leagueDetail(route, path: $playersPath)
                        }
                }
                .tag(ShellTab.players)
            }
            .tabViewStyle(.page(indexDisplayMode: .never))
            .scrollDisabled(true)
        }
        .sheet(isPresented: $isSettingsPresented) {
            SettingsRoute(appearance: $appearance) { isSettingsPresented = false }
                .presentationDetents([.fraction(0.75), .large])
                .presentationDragIndicator(.visible)
                .presentationBackground(BatColor.background)
        }
    }
}

extension ShellScreen {
    fileprivate func teamDetail(_ route: TeamDestination, path: Binding<NavigationPath>) -> some View {
        TeamDetailRoute(
            teamId: route.teamId,
            onOpenLeague: { path.wrappedValue.append(LeagueDestination(leagueId: $0, highlightTeamId: $1)) },
            onOpenPlayer: { path.wrappedValue.append(PlayerDestination(playerId: $0)) }
        )
            .navigationTitle(String(localized: "team_detail_hero_overline"))
            .navigationBarTitleDisplayMode(.inline)
            .batBackground()
    }

    fileprivate func playerDetail(_ route: PlayerDestination, path: Binding<NavigationPath>) -> some View {
        PlayerDetailRoute(
            playerId: route.playerId,
            onOpenClub: { path.wrappedValue.append(ClubDetailRoute(clubId: $0)) },
            onOpenTeam: { path.wrappedValue.append(TeamDestination(teamId: $0)) }
        )
        .navigationTitle(String(localized: "player_detail_hero_overline"))
        .navigationBarTitleDisplayMode(.inline)
        .batBackground()
    }

    fileprivate func leagueDetail(_ route: LeagueDestination, path: Binding<NavigationPath>) -> some View {
        LeagueDetailRoute(leagueId: route.leagueId, highlightTeamId: route.highlightTeamId, onOpenTeam: { path.wrappedValue.append(TeamDestination(teamId: $0)) })
            .navigationTitle(String(localized: "league_detail_hero_overline"))
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
