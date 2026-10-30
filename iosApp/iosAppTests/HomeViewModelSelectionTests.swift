import Shared
import Testing

@testable import MobTT

@MainActor
struct HomeViewModelSelectionTests {
    private let club = Club(id: "c1", name: "Nussdorf TTC", associationId: "w")
    private let association = Association(id: "w", name: "WTTV")

    @Test func chosenPlayerShowsTheirClubAndAssociation() async {
        let context = UserContextMyPlayer(player: Player(id: "p1", name: "Anna Gruber", clubId: "c1"), club: club, association: association)
        let viewModel = makeViewModel(userContext: [DataResultSuccess(value: context)])

        await viewModel.observe()

        #expect(viewModel.uiState.selection == .player(name: "Anna Gruber", clubName: "Nussdorf TTC", associationName: "WTTV", playerId: "p1"))
    }

    @Test func chosenClubShowsItsAssociation() async {
        let context = UserContextMyClub(club: club, association: association)
        let viewModel = makeViewModel(userContext: [DataResultSuccess(value: context)])

        await viewModel.observe()

        #expect(viewModel.uiState.selection == .club(name: "Nussdorf TTC", associationName: "WTTV", clubId: "c1"))
    }

    @Test func nothingChosenShowsThePromptToChoose() async {
        let viewModel = makeViewModel(userContext: [DataResultSuccess(value: UserContextNone.shared)])

        await viewModel.observe()

        #expect(viewModel.uiState.selection == .none)
    }

    @Test func unavailableFixturesShowTheErrorSection() async {
        let viewModel = makeViewModel(fixtures: [UpcomingFixturesUnavailable.shared])

        await viewModel.observe()

        #expect(viewModel.uiState.fixtures == .unavailable)
    }

    @Test func noSelectionHidesTheFixturesSection() async {
        let viewModel = makeViewModel(fixtures: [UpcomingFixturesNoSelection.shared])

        await viewModel.observe()

        #expect(viewModel.uiState.fixtures == .hidden)
    }

    @Test func retryingFixturesBumpsTheRetryCount() {
        let viewModel = makeViewModel()

        viewModel.onAction(.retryFixturesClicked)

        #expect(viewModel.retryCount == 1)
    }

    @Test func actionsEmitTheMatchingEvents() async {
        let viewModel = makeViewModel()
        var events = viewModel.events.makeAsyncIterator()

        viewModel.onAction(.chooseClicked)
        viewModel.onAction(.viewClubClicked)
        viewModel.onAction(.favoriteClubClicked(id: "c1"))
        viewModel.onAction(.favoriteTeamClicked(id: "t1"))
        viewModel.onAction(.favoritePlayerClicked(id: "p1"))
        viewModel.onAction(.heroPlayerClicked(id: "p1"))
        viewModel.onAction(.heroClubClicked(id: "c1"))

        guard case .openSettings? = await events.next() else { Issue.record("expected openSettings"); return }
        guard case .openMyClub? = await events.next() else { Issue.record("expected openMyClub"); return }
        guard case .openClub(let clubId)? = await events.next() else { Issue.record("expected openClub"); return }
        #expect(clubId == "c1")
        guard case .openTeam(let teamId)? = await events.next() else { Issue.record("expected openTeam"); return }
        #expect(teamId == "t1")
        guard case .openPlayer(let playerId)? = await events.next() else { Issue.record("expected openPlayer"); return }
        #expect(playerId == "p1")
        guard case .openPlayer(let heroPlayerId)? = await events.next() else { Issue.record("expected openPlayer for the hero"); return }
        #expect(heroPlayerId == "p1")
        guard case .openClub(let heroClubId)? = await events.next() else { Issue.record("expected openClub for the hero"); return }
        #expect(heroClubId == "c1")
    }

    private func makeViewModel(
        userContext: [DataResult] = [DataResultSuccess(value: UserContextNone.shared)],
        fixtures: [UpcomingFixtures] = [UpcomingFixturesNoSelection.shared]
    ) -> HomeViewModel {
        HomeViewModel(
            observeUserContext: finite(userContext),
            observeResolvedFavorites: finite([DataResult]()),
            observeUpcomingFixtures: finite(fixtures),
            observeFavorites: finite([Set<AnyHashable>()]),
            addFavorite: { _ in },
            removeFavorite: { _ in },
            playerLoaders: PlayerDrillDownFixtures().loaders,
            searchClubs: { _ in [] }
        )
    }

    private func finite<T>(_ values: [T]) -> AsyncStream<T> {
        AsyncStream { continuation in
            values.forEach { continuation.yield($0) }
            continuation.finish()
        }
    }
}
