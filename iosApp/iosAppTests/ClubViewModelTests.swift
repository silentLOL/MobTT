import Shared
import Testing

@testable import MobTT

@MainActor
struct ClubViewModelTests {
    private let club = Club(id: "1", name: "Nussdorf TTC", associationId: "w")
    private let association = Association(id: "w", name: "WTTV")

    @Test func dataUnavailableShowsTheErrorState() async {
        let viewModel = makeSpecificViewModel { _ in unavailable }

        await viewModel.observe()

        #expect(viewModel.uiState == .error)
    }

    @Test func clubWithoutTeamsIsContentNotAnError() async {
        let overview = ClubOverview(club: club, association: association, teams: [], players: [])
        let viewModel = makeSpecificViewModel { _ in DataResultSuccess(value: overview) }

        await viewModel.observe()

        #expect(viewModel.uiState == .content(ClubOverviewUi(clubName: "Nussdorf TTC", associationName: "WTTV", teams: [])))
    }

    @Test func unknownClubShowsTheErrorState() async {
        let viewModel = makeSpecificViewModel { _ in DataResultSuccess<ClubOverview>(value: nil) }

        await viewModel.observe()

        #expect(viewModel.uiState == .error)
    }

    @Test func retryRestartsObservationAndSuccessClearsTheError() async {
        var failures = 1
        let overview = ClubOverview(club: club, association: association, teams: [], players: [])
        let viewModel = makeSpecificViewModel { _ in
            if failures > 0 {
                failures -= 1
                return unavailable
            }
            return DataResultSuccess(value: overview)
        }

        await viewModel.observe()
        #expect(viewModel.uiState == .error)

        viewModel.onAction(.retryClicked)
        #expect(viewModel.retryCount == 1)
        await viewModel.observe()

        #expect(viewModel.uiState == .content(ClubOverviewUi(clubName: "Nussdorf TTC", associationName: "WTTV", teams: [])))
    }

    @Test func myClubLoadsTheClubOfTheChosenPlayer() async {
        let context = UserContextMyPlayer(player: Player(id: "p1", name: "Anna Gruber", clubId: "1"), club: club, association: association)
        var requested: [String] = []
        let viewModel = makeMineViewModel(contexts: [DataResultSuccess(value: context)]) { id in
            requested.append(id)
            return DataResultSuccess(value: ClubOverview(club: club, association: association, teams: [], players: []))
        }

        await viewModel.observe()
        await waitUntil { viewModel.uiState != .loading }

        #expect(requested == ["1"])
        #expect(viewModel.uiState == .content(ClubOverviewUi(clubName: "Nussdorf TTC", associationName: "WTTV", teams: [])))
    }

    @Test func myClubWithNothingChosenShowsThePromptToChoose() async {
        let viewModel = makeMineViewModel(contexts: [DataResultSuccess(value: UserContextNone.shared)])

        await viewModel.observe()

        #expect(viewModel.uiState == .noClub)
    }

    @Test func myClubWithUnavailableUserContextShowsTheErrorNotThePrompt() async {
        let viewModel = makeMineViewModel(contexts: [unavailable])

        await viewModel.observe()

        #expect(viewModel.uiState == .error)
    }

    @Test func myClubFollowsAChangedSelection() async {
        let context = UserContextMyClub(club: club, association: association)
        let viewModel = makeMineViewModel(contexts: [DataResultSuccess(value: context), DataResultSuccess(value: UserContextNone.shared)])

        await viewModel.observe()

        #expect(viewModel.uiState == .noClub)
    }

    @Test func aClubThatIsNotFavoriteShowsAsNotFavorite() async {
        let viewModel = makeSpecificViewModel(favorites: [[FavoriteTeam(id: "1") as AnyHashable]]) { _ in success }

        await viewModel.observe()

        #expect(viewModel.uiState == .content(ClubOverviewUi(clubName: "Nussdorf TTC", associationName: "WTTV", teams: [])))
    }

    @Test func aFavoriteClubShowsAsFavorite() async {
        let viewModel = makeSpecificViewModel(favorites: [[FavoriteClub(id: "1") as AnyHashable]]) { _ in success }

        await viewModel.observe()

        #expect(viewModel.uiState == .content(ClubOverviewUi(clubName: "Nussdorf TTC", associationName: "WTTV", teams: [], isFavorite: true)))
    }

    @Test func theStarFollowsChangesToTheFavorites() async {
        let club = FavoriteClub(id: "1") as AnyHashable
        let viewModel = makeSpecificViewModel(favorites: [[], [club]]) { _ in success }
        await viewModel.observe()
        #expect(viewModel.uiState == .content(ClubOverviewUi(clubName: "Nussdorf TTC", associationName: "WTTV", teams: [], isFavorite: true)))

        let removed = makeSpecificViewModel(favorites: [[club], []]) { _ in success }
        await removed.observe()
        #expect(removed.uiState == .content(ClubOverviewUi(clubName: "Nussdorf TTC", associationName: "WTTV", teams: [])))
    }

    @Test func tappingTheStarOfAClubThatIsNotFavoriteAddsIt() async {
        var added: [Favorite] = []
        let viewModel = makeSpecificViewModel(addFavorite: { added.append($0) }) { _ in success }
        await viewModel.observe()

        viewModel.onAction(.favoriteClicked)
        await waitUntil { !added.isEmpty }

        #expect(added.compactMap { ($0 as? FavoriteClub)?.id } == ["1"])
    }

    @Test func tappingTheStarOfAFavoriteClubRemovesIt() async {
        var removed: [Favorite] = []
        let viewModel = makeSpecificViewModel(
            favorites: [[FavoriteClub(id: "1") as AnyHashable]],
            removeFavorite: { removed.append($0) }
        ) { _ in success }
        await viewModel.observe()

        viewModel.onAction(.favoriteClicked)
        await waitUntil { !removed.isEmpty }

        #expect(removed.compactMap { ($0 as? FavoriteClub)?.id } == ["1"])
    }

    @Test func tappingTheStarWithoutContentChangesNothing() async {
        var changes = 0
        let viewModel = makeSpecificViewModel(addFavorite: { _ in changes += 1 }, removeFavorite: { _ in changes += 1 }) { _ in unavailable }
        await viewModel.observe()

        viewModel.onAction(.favoriteClicked)
        for _ in 0..<20 { await Task.yield() }

        #expect(changes == 0)
    }

    private func makeMineViewModel(
        contexts: [DataResult],
        loadOverview: @escaping (String) async throws -> DataResult = { _ in DataResultUnavailable(error: DataUnavailable(message: "down", cause: nil)) }
    ) -> ClubViewModel {
        ClubViewModel(
            source: .mine,
            observeUserContext: AsyncStream { continuation in
                contexts.forEach { continuation.yield($0) }
                continuation.finish()
            },
            loadOverview: loadOverview,
            observeFavorites: finite([]),
            addFavorite: { _ in },
            removeFavorite: { _ in }
        )
    }

    private func makeSpecificViewModel(
        favorites: [Set<AnyHashable>] = [],
        addFavorite: @escaping (Favorite) async throws -> Void = { _ in },
        removeFavorite: @escaping (Favorite) async throws -> Void = { _ in },
        loadOverview: @escaping (String) async throws -> DataResult
    ) -> ClubViewModel {
        ClubViewModel(
            source: .specific(clubId: "1"),
            loadOverview: loadOverview,
            observeFavorites: finite(favorites),
            addFavorite: addFavorite,
            removeFavorite: removeFavorite
        )
    }

    private func finite<T>(_ values: [T]) -> AsyncStream<T> {
        AsyncStream { continuation in
            values.forEach { continuation.yield($0) }
            continuation.finish()
        }
    }

    private func waitUntil(_ condition: () -> Bool) async {
        for _ in 0..<100 where !condition() {
            await Task.yield()
        }
    }

    private var success: DataResult {
        DataResultSuccess(value: ClubOverview(club: club, association: association, teams: [], players: []))
    }

    private var unavailable: DataResult { DataResultUnavailable(error: DataUnavailable(message: "down", cause: nil)) }
}
