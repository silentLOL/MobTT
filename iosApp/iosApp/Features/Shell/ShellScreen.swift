import SwiftUI

private struct ClubDetailRoute: Hashable {
    let clubId: String
}

private struct PlayerDestination: Hashable {
    let playerId: String
}

/// The Players flow reused to choose the hero: a Player (saved as My Player) or a Club (saved as My Club).
private struct ChooseHeroDestination: Hashable {
    let mode: PlayersMode
}

private struct TeamDestination: Hashable {
    let teamId: String
}

private struct FixtureDestination: Hashable {
    let fixtureId: String
}

private struct SettingsDestination: Hashable {}

private struct LicensesDestination: Hashable {}

/// Settings with its title, reporting to the shell while it is showing so the shell can hide its gear.
private struct SettingsDestinationView: View {
    @Binding var appearance: AppearanceChoice
    @Binding var openSettingsIds: Set<UUID>
    let onOpenLicenses: () -> Void
    @State private var id = UUID()

    var body: some View {
        SettingsRoute(appearance: $appearance, onOpenLicenses: onOpenLicenses)
            .navigationTitle(String(localized: "settings_title"))
            .navigationBarTitleDisplayMode(.inline)
            .batBackground()
            .onAppear { openSettingsIds.insert(id) }
            .onDisappear { openSettingsIds.remove(id) }
    }
}

/// The licenses list, which counts as Settings being open: the gear stays hidden and the consent prompt waits.
private struct LicensesDestinationView: View {
    @Binding var openSettingsIds: Set<UUID>
    @State private var id = UUID()

    var body: some View {
        LicensesRoute()
            .navigationTitle(String(localized: "licenses_title"))
            .navigationBarTitleDisplayMode(.inline)
            .onAppear { openSettingsIds.insert(id) }
            .onDisappear { openSettingsIds.remove(id) }
    }
}

private struct LeagueDestination: Hashable {
    let leagueId: String
    /// The Team to highlight in the table and the Fixtures.
    let highlightTeamId: String?
    /// The Team whose screen pushed this League; its row does not open it again.
    var openedFromTeamId: String?
}

struct ShellScreen: View {
    @Binding var appearance: AppearanceChoice
    @State private var selectedTab = ShellTab.home
    /// The Settings screens showing, each by its own id: a late disappear from a screen on another tab removes only that
    /// screen, never one opened since.
    @State private var openSettingsIds: Set<UUID> = []
    @State private var consentViewModel = UsageConsentViewModel()
    @State private var homePath = NavigationPath()
    @State private var leaguePath = NavigationPath()
    @State private var clubPath = NavigationPath()
    @State private var playersPath = NavigationPath()
    @State private var fixtureLinkViewModel = FixtureLinkViewModel()
    /// When the shell first appeared; a link soon after that is the one the app was launched with.
    @State private var appearedAt: Date?

    var body: some View {
        BatScreenScaffoldView {
            BatTopBarView(title: String(localized: "core_app_title")) {
                if !isSettingsOpen {
                    BatIconButtonView(icon: .settings, accessibilityText: String(localized: "core_settings_button_cd"), action: openSettings)
                }
            }
        } bottomBar: {
            EmptyView()
        } content: {
            // The system tab bar: the floating Liquid Glass bar on iOS 26, the standard translucent one before. Not the
            // page style with a custom bar, which let a page's scroll view run below the bar, so the end of a long screen
            // could not be scrolled into view.
            TabView(selection: $selectedTab) {
                NavigationStack(path: $homePath) {
                    HomeRoute(
                        onChooseMyPlayer: { homePath.append(ChooseHeroDestination(mode: .myPlayer)) },
                        onChooseMyClub: { homePath.append(ChooseHeroDestination(mode: .myClub)) },
                        onOpenClub: { homePath.append(ClubDetailRoute(clubId: $0)) },
                        onOpenTeam: { homePath.append(TeamDestination(teamId: $0)) },
                        onOpenPlayer: { homePath.append(PlayerDestination(playerId: $0)) }
                    )
                    .toolbar(.hidden, for: .navigationBar)
                    .navigationDestination(for: SettingsDestination.self) { _ in settingsDetail() }
                    .navigationDestination(for: LicensesDestination.self) { _ in licensesDetail() }
                    .navigationDestination(for: ClubDetailRoute.self) { route in
                        ClubRoute(
                            source: .specific(clubId: route.clubId),
                            onOpenSettings: openSettings,
                            onOpenTeam: { homePath.append(TeamDestination(teamId: $0)) },
                            onOpenPlayer: { homePath.append(PlayerDestination(playerId: $0)) }
                        )
                        .navigationTitle(String(localized: "club_hero_overline"))
                        .navigationBarTitleDisplayMode(.inline)
                        .batBackground()
                    }
                    .navigationDestination(for: ChooseHeroDestination.self) { route in
                        PlayersRoute(mode: route.mode, onDone: { homePath = NavigationPath() })
                            .navigationTitle(String(localized: route.mode == .myPlayer ? "home_choose_player_title" : "home_choose_club_title"))
                            .navigationBarTitleDisplayMode(.inline)
                            .batBackground()
                    }
                    .navigationDestination(for: TeamDestination.self) { route in
                        teamDetail(route, path: $homePath)
                    }
                    .navigationDestination(for: LeagueDestination.self) { route in
                        leagueDetail(route, path: $homePath)
                    }
                    .navigationDestination(for: FixtureDestination.self) { route in
                        fixtureDetail(route, path: $homePath)
                    }
                    .navigationDestination(for: PlayerDestination.self) { route in
                        playerDetail(route, path: $homePath)
                    }
                }
                .tabItem { tabLabel(.home) }
                .tag(ShellTab.home)
                NavigationStack(path: $leaguePath) {
                    LeagueTabRoute(
                        onChoosePlayer: { leaguePath.append(ChooseHeroDestination(mode: .myPlayer)) },
                        onOpenPlayer: { leaguePath.append(PlayerDestination(playerId: $0)) },
                        onOpenTeam: { leaguePath.append(TeamDestination(teamId: $0)) },
                        onOpenFixture: { leaguePath.append(FixtureDestination(fixtureId: $0)) },
                        onOpenLeague: { leaguePath.append(LeagueDestination(leagueId: $0, highlightTeamId: $1)) }
                    )
                    .toolbar(.hidden, for: .navigationBar)
                    .navigationDestination(for: SettingsDestination.self) { _ in settingsDetail() }
                    .navigationDestination(for: LicensesDestination.self) { _ in licensesDetail() }
                    .navigationDestination(for: ChooseHeroDestination.self) { route in
                        PlayersRoute(mode: route.mode, onDone: { leaguePath = NavigationPath() })
                            .navigationTitle(String(localized: "home_choose_player_title"))
                            .navigationBarTitleDisplayMode(.inline)
                            .batBackground()
                    }
                    .navigationDestination(for: ClubDetailRoute.self) { route in
                        ClubRoute(
                            source: .specific(clubId: route.clubId),
                            onOpenSettings: openSettings,
                            onOpenTeam: { leaguePath.append(TeamDestination(teamId: $0)) },
                            onOpenPlayer: { leaguePath.append(PlayerDestination(playerId: $0)) }
                        )
                        .navigationTitle(String(localized: "club_hero_overline"))
                        .navigationBarTitleDisplayMode(.inline)
                        .batBackground()
                    }
                    .navigationDestination(for: TeamDestination.self) { route in
                        teamDetail(route, path: $leaguePath)
                    }
                    .navigationDestination(for: LeagueDestination.self) { route in
                        leagueDetail(route, path: $leaguePath)
                    }
                    .navigationDestination(for: FixtureDestination.self) { route in
                        fixtureDetail(route, path: $leaguePath)
                    }
                    .navigationDestination(for: PlayerDestination.self) { route in
                        playerDetail(route, path: $leaguePath)
                    }
                }
                .tabItem { tabLabel(.league) }
                .tag(ShellTab.league)
                NavigationStack(path: $clubPath) {
                    ClubTabRoute(
                        onOpenTeam: { clubPath.append(TeamDestination(teamId: $0)) },
                        onOpenPlayer: { clubPath.append(PlayerDestination(playerId: $0)) }
                    )
                    .toolbar(.hidden, for: .navigationBar)
                    .navigationDestination(for: SettingsDestination.self) { _ in settingsDetail() }
                    .navigationDestination(for: LicensesDestination.self) { _ in licensesDetail() }
                    .navigationDestination(for: PlayerDestination.self) { route in
                        playerDetail(route, path: $clubPath)
                    }
                    .navigationDestination(for: ClubDetailRoute.self) { route in
                        ClubRoute(
                            source: .specific(clubId: route.clubId),
                            onOpenSettings: openSettings,
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
                    .navigationDestination(for: FixtureDestination.self) { route in
                        fixtureDetail(route, path: $clubPath)
                    }
                }
                .tabItem { tabLabel(.club) }
                .tag(ShellTab.club)
                NavigationStack(path: $playersPath) {
                    PlayersRoute(onOpenPlayer: { playersPath.append(PlayerDestination(playerId: $0)) })
                        .toolbar(.hidden, for: .navigationBar)
                        .navigationDestination(for: SettingsDestination.self) { _ in settingsDetail() }
                        .navigationDestination(for: LicensesDestination.self) { _ in licensesDetail() }
                        .navigationDestination(for: PlayerDestination.self) { route in
                            playerDetail(route, path: $playersPath)
                        }
                        .navigationDestination(for: ClubDetailRoute.self) { route in
                            ClubRoute(
                                source: .specific(clubId: route.clubId),
                                onOpenSettings: openSettings,
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
                        .navigationDestination(for: FixtureDestination.self) { route in
                            fixtureDetail(route, path: $playersPath)
                        }
                }
                .tabItem { tabLabel(.players) }
                .tag(ShellTab.players)
            }
            .tint(BatColor.brand)
            .onChange(of: selectedTab) { _, tab in
                // Leaving a tab drops what was pushed on it; the tab just chosen keeps its own, which a link may have set.
                openSettingsIds = []
                if tab != .home { homePath = NavigationPath() }
                if tab != .league { leaguePath = NavigationPath() }
                if tab != .club { clubPath = NavigationPath() }
                if tab != .players { playersPath = NavigationPath() }
            }
        }
        .task { await consentViewModel.observe() }
        .onAppear { appearedAt = appearedAt ?? .now }
        .onOpenURL { url in
            let coldStart = appearedAt.map { Date.now.timeIntervalSince($0) < Self.coldStartWindow } ?? true
            fixtureLinkViewModel.onAction(.linkReceived(url: url.absoluteString, coldStart: coldStart))
        }
        .task {
            for await event in fixtureLinkViewModel.events {
                switch event {
                case .openFixture(let fixtureId): openFromLink(FixtureDestination(fixtureId: fixtureId))
                }
            }
        }
        // Never over Settings: the prompt waits until Settings closes.
        .sheet(isPresented: Binding(
            get: { consentViewModel.isPromptVisible && !isSettingsOpen },
            set: { if !$0 { consentViewModel.onAction(.notNowClicked) } }
        )) {
            UsageConsentSheetView(onAction: consentViewModel.onAction)
                .presentationDetents([.medium, .large])
                .presentationBackground(BatColor.background)
        }
    }
}

extension ShellScreen {
    /// How long after the shell appears a link still counts as the one that launched the app.
    private static let coldStartWindow: TimeInterval = 3

    private func tabLabel(_ tab: ShellTab) -> some View {
        Label { Text(tab.label) } icon: { tab.icon.image }
    }

    private var isSettingsOpen: Bool { !openSettingsIds.isEmpty }

    private func openSettings() {
        guard !isSettingsOpen else { return }
        switch selectedTab {
        case .home: homePath.append(SettingsDestination())
        case .league: leaguePath.append(SettingsDestination())
        case .club: clubPath.append(SettingsDestination())
        case .players: playersPath.append(SettingsDestination())
        }
    }

    /// Pushes the licenses onto the tab Settings was opened from.
    private func openLicenses() {
        switch selectedTab {
        case .home: homePath.append(LicensesDestination())
        case .league: leaguePath.append(LicensesDestination())
        case .club: clubPath.append(LicensesDestination())
        case .players: playersPath.append(LicensesDestination())
        }
    }

    fileprivate func settingsDetail() -> some View {
        SettingsDestinationView(appearance: $appearance, openSettingsIds: $openSettingsIds, onOpenLicenses: openLicenses)
    }

    fileprivate func licensesDetail() -> some View {
        LicensesDestinationView(openSettingsIds: $openSettingsIds)
    }

    /// Shows `destination` on the Home tab with Home under it, whatever was open before.
    private func openFromLink(_ destination: FixtureDestination) {
        openSettingsIds = []
        selectedTab = .home
        leaguePath = NavigationPath()
        clubPath = NavigationPath()
        playersPath = NavigationPath()
        var path = NavigationPath()
        path.append(destination)
        homePath = path
    }

    fileprivate func teamDetail(_ route: TeamDestination, path: Binding<NavigationPath>) -> some View {
        TeamDetailRoute(
            teamId: route.teamId,
            onOpenLeague: { path.wrappedValue.append(LeagueDestination(leagueId: $0, highlightTeamId: $1, openedFromTeamId: $1)) },
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
        LeagueDetailRoute(
            leagueId: route.leagueId,
            highlightTeamId: route.highlightTeamId,
            openedFromTeamId: route.openedFromTeamId,
            onOpenTeam: { path.wrappedValue.append(TeamDestination(teamId: $0)) },
            onOpenFixture: { path.wrappedValue.append(FixtureDestination(fixtureId: $0)) },
            onOpenPlayer: { path.wrappedValue.append(PlayerDestination(playerId: $0)) }
        )
            .navigationTitle(String(localized: "league_detail_hero_overline"))
            .navigationBarTitleDisplayMode(.inline)
            .batBackground()
    }

    fileprivate func fixtureDetail(_ route: FixtureDestination, path: Binding<NavigationPath>) -> some View {
        FixtureDetailRoute(fixtureId: route.fixtureId, onOpenTeam: { path.wrappedValue.append(TeamDestination(teamId: $0)) })
            .navigationTitle(String(localized: "fixture_detail_hero_overline"))
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
