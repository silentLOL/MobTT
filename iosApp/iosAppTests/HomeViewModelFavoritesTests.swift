import Foundation
import Shared
import Testing

@testable import BatMate

@MainActor
struct HomeViewModelFavoritesTests {
    private let club = Club(id: "c1", name: "TTC Olympic", associationId: "w")

    @Test func resolvedFavoritesAppearInTheList() async {
        let resolved = ResolvedFavorite(favorite: FavoriteClub(id: "c1"), name: "TTC Olympic", detail: "WTTV")
        let viewModel = makeViewModel(resolved: [success([resolved])])

        await viewModel.observe()

        #expect(viewModel.uiState.favorites == [HomeFavoriteUi(kind: .club, entityId: "c1", name: "TTC Olympic", detail: "WTTV")])
    }

    @Test func teamFavoritesAreLeftOutOfTheList() async {
        let resolved = ResolvedFavorite(favorite: FavoriteTeam(id: "t1"), name: "Olympic 1", detail: nil)
        let viewModel = makeViewModel(resolved: [success([resolved])])

        await viewModel.observe()

        #expect(viewModel.uiState.favorites.isEmpty)
    }

    @Test func unavailableFavoritesShowNoneRatherThanStaleOnes() async {
        let resolved = ResolvedFavorite(favorite: FavoriteClub(id: "c1"), name: "TTC Olympic", detail: nil)
        let viewModel = makeViewModel(resolved: [
            success([resolved]),
            DataResultUnavailable(error: DataUnavailable(message: "down", cause: nil)),
        ])

        await viewModel.observe()

        #expect(viewModel.uiState.favorites.isEmpty)
    }

    @Test func anUnavailableUserContextIsNotThePromptToChoose() async {
        let viewModel = makeViewModel(
            userContext: [DataResultUnavailable(error: DataUnavailable(message: "down", cause: nil))]
        )

        await viewModel.observe()

        #expect(viewModel.uiState.selection == .unavailable)
    }

    @Test func pickerMarksAnExistingFavoriteSelected() async {
        let viewModel = makeViewModel(favorites: [FavoriteClub(id: "c1") as AnyHashable], clubs: [club])
        await viewModel.observe()

        viewModel.onAction(.addFavoriteClicked)
        await waitUntil { !(viewModel.uiState.picker?.clubs.isEmpty ?? true) }

        #expect(viewModel.uiState.picker?.clubs.map(\.selected) == [true])
    }

    @Test func clickingAnUnselectedEntryAddsTheFavorite() async {
        var added: [Favorite] = []
        let viewModel = makeViewModel(clubs: [club], addFavorite: { added.append($0) })
        await viewModel.observe()

        viewModel.onAction(.pickerClubClicked(id: "c1"))
        await waitUntil { !added.isEmpty }

        #expect(added.compactMap { ($0 as? FavoriteClub)?.id } == ["c1"])
    }

    @Test func browsingPlayersStartsTheDrillDownAndBackReturnsToTheSearch() async {
        let viewModel = makeViewModel(clubs: [club])
        viewModel.onAction(.addFavoriteClicked)
        #expect(viewModel.uiState.picker?.playerDrillDown == nil)

        viewModel.onAction(.browsePlayersClicked)
        await waitUntil { !(viewModel.uiState.picker?.playerDrillDown?.entries.isEmpty ?? true) }
        #expect(viewModel.uiState.picker?.playerDrillDown?.step == .association)

        viewModel.onAction(.pickerDrillDownBackClicked)
        #expect(viewModel.uiState.picker != nil)
        #expect(viewModel.uiState.picker?.playerDrillDown == nil)
    }

    @Test func pickingAPlayerFromTheDrillDownAddsTheFavorite() async {
        var added: [Favorite] = []
        let viewModel = await playerStep(addFavorite: { added.append($0) })

        viewModel.onAction(.pickerDrillDownEntryClicked(id: "p1"))
        await waitUntil { !added.isEmpty }

        #expect(added.compactMap { ($0 as? FavoritePlayer)?.id } == ["p1"])
    }

    @Test func pickingAFavoritePlayerFromTheDrillDownRemovesIt() async {
        var removed: [Favorite] = []
        let viewModel = await playerStep(favorites: [FavoritePlayer(id: "p1") as AnyHashable], removeFavorite: { removed.append($0) })

        #expect(viewModel.uiState.picker?.playerDrillDown?.entries.map(\.selected) == [true, false])
        viewModel.onAction(.pickerDrillDownEntryClicked(id: "p1"))
        await waitUntil { !removed.isEmpty }

        #expect(removed.compactMap { ($0 as? FavoritePlayer)?.id } == ["p1"])
    }

    @Test func theDrillDownFiltersLocallyAndReadsEachStepOnce() async {
        let fixtures = PlayerDrillDownFixtures()
        let viewModel = await playerStep(fixtures: fixtures)

        viewModel.onAction(.pickerQueryChanged("gru"))

        #expect(viewModel.uiState.picker?.playerDrillDown?.entries.map(\.id) == ["p1"])
        #expect(fixtures.reads.players == ["l1"])
    }

    @Test func removingFromHomeRemovesThatFavorite() async {
        var removed: [Favorite] = []
        let viewModel = makeViewModel(removeFavorite: { removed.append($0) })

        viewModel.onAction(.removeFavoriteClicked(HomeFavoriteUi(kind: .club, entityId: "c1", name: "TTC Olympic", detail: nil)))
        await waitUntil { !removed.isEmpty }

        #expect(removed.compactMap { ($0 as? FavoriteClub)?.id } == ["c1"])
    }

    @Test func dismissingThePickerClosesIt() {
        let viewModel = makeViewModel()

        viewModel.onAction(.addFavoriteClicked)
        #expect(viewModel.uiState.picker?.query == "")

        viewModel.onAction(.pickerDismissed)
        #expect(viewModel.uiState.picker == nil)
    }

    /// The picker open at the Player step of the first Verband's first League.
    private func playerStep(
        fixtures: PlayerDrillDownFixtures = PlayerDrillDownFixtures(),
        favorites: Set<AnyHashable> = [],
        addFavorite: @escaping (Favorite) async throws -> Void = { _ in },
        removeFavorite: @escaping (Favorite) async throws -> Void = { _ in }
    ) async -> HomeViewModel {
        let viewModel = makeViewModel(
            favorites: favorites,
            playerLoaders: fixtures.loaders,
            addFavorite: addFavorite,
            removeFavorite: removeFavorite
        )
        await viewModel.observe()
        viewModel.onAction(.addFavoriteClicked)
        viewModel.onAction(.browsePlayersClicked)
        await waitUntil { !(viewModel.uiState.picker?.playerDrillDown?.entries.isEmpty ?? true) }
        viewModel.onAction(.pickerDrillDownEntryClicked(id: "w"))
        await waitUntil { viewModel.uiState.picker?.playerDrillDown?.step == .league && !(viewModel.uiState.picker?.playerDrillDown?.entries.isEmpty ?? true) }
        viewModel.onAction(.pickerDrillDownEntryClicked(id: "l1"))
        await waitUntil { viewModel.uiState.picker?.playerDrillDown?.step == .player && !(viewModel.uiState.picker?.playerDrillDown?.entries.isEmpty ?? true) }
        return viewModel
    }

    private func makeViewModel(
        userContext: [DataResult] = [DataResultSuccess(value: UserContextNone.shared)],
        resolved: [DataResult] = [],
        favorites: Set<AnyHashable> = [],
        clubs: [Club] = [],
        playerLoaders: PlayerDrillDownLoaders = PlayerDrillDownFixtures().loaders,
        addFavorite: @escaping (Favorite) async throws -> Void = { _ in },
        removeFavorite: @escaping (Favorite) async throws -> Void = { _ in }
    ) -> HomeViewModel {
        HomeViewModel(
            observeUserContext: finite(userContext),
            observeResolvedFavorites: finite(resolved),
            observeUpcomingFixtures: finite([UpcomingFixturesNoSelection.shared as UpcomingFixtures]),
            observeFavorites: finite([favorites]),
            addFavorite: addFavorite,
            removeFavorite: removeFavorite,
            playerLoaders: playerLoaders,
            searchClubs: { _ in clubs }
        )
    }

    private func success(_ favorites: [ResolvedFavorite]) -> DataResult {
        DataResultSuccess(value: favorites as NSArray)
    }

    private func finite<T>(_ values: [T]) -> AsyncStream<T> {
        AsyncStream { continuation in
            values.forEach { continuation.yield($0) }
            continuation.finish()
        }
    }
}
