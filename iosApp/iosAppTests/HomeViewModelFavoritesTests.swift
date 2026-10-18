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

    @Test func clickingASelectedEntryRemovesTheFavorite() async {
        var removed: [Favorite] = []
        let viewModel = makeViewModel(favorites: [FavoritePlayer(id: "p1") as AnyHashable], removeFavorite: { removed.append($0) })
        await viewModel.observe()

        viewModel.onAction(.pickerPlayerClicked(id: "p1"))
        await waitUntil { !removed.isEmpty }

        #expect(removed.compactMap { ($0 as? FavoritePlayer)?.id } == ["p1"])
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

    private func waitUntil(_ condition: () -> Bool) async {
        for _ in 0..<100 where !condition() {
            await Task.yield()
        }
    }

    private func makeViewModel(
        resolved: [DataResult] = [],
        favorites: Set<AnyHashable> = [],
        clubs: [Club] = [],
        addFavorite: @escaping (Favorite) async throws -> Void = { _ in },
        removeFavorite: @escaping (Favorite) async throws -> Void = { _ in }
    ) -> HomeViewModel {
        HomeViewModel(
            observeUserContext: finite([DataResultSuccess(value: UserContextNone.shared) as DataResult]),
            observeResolvedFavorites: finite(resolved),
            observeUpcomingFixtures: finite([UpcomingFixturesNoSelection.shared as UpcomingFixtures]),
            observeFavorites: finite([favorites]),
            addFavorite: addFavorite,
            removeFavorite: removeFavorite,
            searchPlayers: { _ in [] },
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
