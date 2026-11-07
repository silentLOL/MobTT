import Foundation
import Observation
import Shared

@MainActor
@Observable
final class PlayerDetailViewModel {
    private(set) var uiState: PlayerDetailUiState = .loading

    private let playerId: String
    private var loadTask: Task<Void, Never>?
    @ObservationIgnored private var player: Player?
    @ObservationIgnored private var clubTask: Task<Void, Never>?
    @ObservationIgnored private var teamsTask: Task<Void, Never>?
    @ObservationIgnored private var ratingsCentralId: String?
    @ObservationIgnored private var ratingsCentralIdTask: Task<Void, Never>?
    @ObservationIgnored private var ratingHistoryTask: Task<Void, Never>?
    @ObservationIgnored private var favorites: Set<AnyHashable> = []

    @ObservationIgnored private let eventStream = AsyncStream<PlayerDetailEvent>.makeStream()
    @ObservationIgnored private let loadPlayer: (String) async throws -> DataResult
    @ObservationIgnored private let loadClub: (Player) async throws -> DataResult
    @ObservationIgnored private let loadTeams: (Player) async throws -> DataResult
    @ObservationIgnored private let ratingHistoryEnabled: Bool
    @ObservationIgnored private let loadRatingsCentralId: (String, [String]) async throws -> DataResult
    @ObservationIgnored private let loadRatingHistory: (String) async throws -> DataResult
    @ObservationIgnored private let now: () -> Date
    @ObservationIgnored private let ratingExplanationUrl: URL?
    @ObservationIgnored private let observeFavorites: any AsyncSequence<Set<AnyHashable>, Never>
    @ObservationIgnored private let addFavorite: (Favorite) async throws -> Void
    @ObservationIgnored private let removeFavorite: (Favorite) async throws -> Void
    @ObservationIgnored private let analytics: any AnalyticsTracker
    @ObservationIgnored private let screenViews: ScreenViewReporter

    init(
        playerId: String,
        loadPlayer: @escaping (String) async throws -> DataResult = { try await KoinHelper.shared.loadPlayer(playerId: $0) },
        loadClub: @escaping (Player) async throws -> DataResult = { try await KoinHelper.shared.loadPlayerClub(player: $0) },
        loadTeams: @escaping (Player) async throws -> DataResult = { try await KoinHelper.shared.loadPlayerTeams(player: $0) },
        ratingHistoryEnabled: Bool = KoinHelper.shared.isRatingHistoryEnabled(),
        loadRatingsCentralId: @escaping (String, [String]) async throws -> DataResult = { try await KoinHelper.shared.loadRatingsCentralId(playerId: $0, leagueIds: $1) },
        loadRatingHistory: @escaping (String) async throws -> DataResult = { try await KoinHelper.shared.loadRatingHistory(ratingsCentralId: $0) },
        now: @escaping () -> Date = Date.init,
        ratingExplanationUrl: URL? = URL(string: KoinHelper.shared.ratingHistoryExplanationUrl()),
        observeFavorites: any AsyncSequence<Set<AnyHashable>, Never> = KoinHelper.shared.getObserveFavoritesUseCase().invoke(),
        addFavorite: @escaping (Favorite) async throws -> Void = { try await KoinHelper.shared.getAddFavoriteUseCase().invoke(favorite: $0) },
        removeFavorite: @escaping (Favorite) async throws -> Void = { try await KoinHelper.shared.getRemoveFavoriteUseCase().invoke(favorite: $0) },
        analytics: any AnalyticsTracker = AppAnalytics.tracker
    ) {
        self.playerId = playerId
        self.loadPlayer = loadPlayer
        self.loadClub = loadClub
        self.loadTeams = loadTeams
        self.ratingHistoryEnabled = ratingHistoryEnabled
        self.loadRatingsCentralId = loadRatingsCentralId
        self.loadRatingHistory = loadRatingHistory
        self.now = now
        self.ratingExplanationUrl = ratingExplanationUrl
        self.observeFavorites = observeFavorites
        self.addFavorite = addFavorite
        self.removeFavorite = removeFavorite
        self.analytics = analytics
        self.screenViews = ScreenViewReporter(tracker: analytics, screen: AnalyticsScreen.playerDetail)
    }

    /// Reports the screen view, once per screen.
    func screenShown() {
        screenViews.report()
    }

    var events: AsyncStream<PlayerDetailEvent> { eventStream.stream }

    /// Follows the Favorites until the calling task is cancelled. The load itself runs from `loadIfNeeded()`.
    func observe() async {
        for await set in observeFavorites {
            favorites = set
            refreshFavorite()
        }
    }

    /// Starts the first load once: the Player, then the Club and the Teams side by side. It runs outside the view's task, so leaving and returning to the screen neither reloads nor strands it on `.loading`.
    func loadIfNeeded() {
        guard loadTask == nil else { return }
        loadTask = Task { await load() }
    }

    func onAction(_ action: PlayerDetailAction) {
        switch action {
        case .favoriteClicked:
            toggleFavorite()
        case .clubClicked:
            guard case .content(let detail) = uiState else { return }
            analytics.detailOpened(kind: AnalyticsKind.club, id: detail.clubId, from: AnalyticsScreen.playerDetail)
            eventStream.continuation.yield(.openClub(clubId: detail.clubId))
        case .teamClicked(let id):
            analytics.detailOpened(kind: AnalyticsKind.team, id: id, from: AnalyticsScreen.playerDetail)
            eventStream.continuation.yield(.openTeam(teamId: id))
        case .clubRetryClicked:
            startClub()
        case .teamsRetryClicked:
            startTeams()
        case .ratingHistoryToggled:
            toggleRatingHistory()
        case .ratingHistoryRetryClicked:
            startRatingHistory()
        case .ratingInfoClicked:
            updateContent { $0.ratingInfoShown = true }
            analytics.ratingInfoOpened()
        case .ratingInfoDismissed:
            updateContent { $0.ratingInfoShown = false }
        case .ratingInfoLinkClicked:
            guard let ratingExplanationUrl else { return }
            eventStream.continuation.yield(.openWebPage(url: ratingExplanationUrl))
        case .ratingHistorySpanChosen(let span):
            updateContent {
                $0.ratingHistory?.span = span
                $0.ratingHistory?.since = firstDay(of: span)
            }
        }
    }

    private func refreshFavorite() {
        guard case .content(var detail) = uiState else { return }
        let isFavorite = isFavoritePlayer
        guard detail.isFavorite != isFavorite else { return }
        detail.isFavorite = isFavorite
        uiState = .content(detail)
    }

    private var isFavoritePlayer: Bool {
        favorites.contains { ($0 as? FavoritePlayer)?.id == playerId }
    }

    private func toggleFavorite() {
        guard case .content(let detail) = uiState else { return }
        let favorite = FavoritePlayer(id: playerId)
        Task {
            let saved = detail.isFavorite ? (try? await removeFavorite(favorite)) != nil : (try? await addFavorite(favorite)) != nil
            if saved {
                analytics.favoriteToggled(kind: AnalyticsKind.player, id: playerId, enabled: !detail.isFavorite)
            }
        }
    }

    private func load() async {
        uiState = .loading
        do {
            let result = try await loadPlayer(playerId)
            guard !Task.isCancelled else { return }
            guard case .success(let success) = onEnum(of: result), let player = success.value as? Player else {
                uiState = .unavailable
                return
            }
            self.player = player
            uiState = .content(PlayerDetailUi(playerName: player.name, clubId: player.clubId, isFavorite: isFavoritePlayer))
            startClub()
            startTeams()
        } catch {
            // Only cancellation can throw now, and the load that replaced this one owns the state.
        }
    }

    private func startClub() {
        guard let player else { return }
        clubTask?.cancel()
        clubTask = Task {
            updateContent { $0.club = .loading }
            guard let section = await loadSection({ try await self.loadClub(player) }, map: { (club: PlayerClub?) in club?.toUi() }) else { return }
            updateContent { $0.club = section }
        }
    }

    private func startTeams() {
        guard let player else { return }
        teamsTask?.cancel()
        teamsTask = Task {
            updateContent { $0.teams = .loading }
            let result = try? await loadTeams(player)
            guard !Task.isCancelled else { return }
            guard let result, case .success(let success) = onEnum(of: result), let teams = success.value as? PlayerTeams else {
                updateContent { $0.teams = .unavailable }
                return
            }
            updateContent { $0.teams = .loaded(teams.toUi()) }
            findRatingsCentralId(player: player, leagueIds: teams.teams.map(\.leagueId))
        }
    }

    /// The Rating history is a bonus: a ranking XTTV cannot answer only hides it. The section starts open, so Ratings Central is asked once the entry is known.
    private func findRatingsCentralId(player: Player, leagueIds: [String]) {
        guard ratingHistoryEnabled, ratingsCentralId == nil, !leagueIds.isEmpty else { return }
        ratingsCentralIdTask?.cancel()
        ratingsCentralIdTask = Task {
            guard let result = try? await loadRatingsCentralId(player.id, leagueIds), !Task.isCancelled else { return }
            guard case .success(let success) = onEnum(of: result), let id = success.value as? String else { return }
            ratingsCentralId = id
            // The section starts open, so its Rating history loads straight away.
            updateContent { $0.ratingHistory = RatingHistorySectionUi(since: firstDay(of: .lastYear)) }
            startRatingHistory()
        }
    }

    /// The last 12 months run from this day a year ago, so a Rating update on that day still counts. Rating update dates are midnight UTC.
    private func firstDay(of span: RatingHistorySpan) -> Date? {
        switch span {
        case .all:
            return nil
        case .lastYear:
            // Calendar arithmetic, like Android's, so 29 February steps back to 28 February.
            let calendar = Calendar.current
            guard let yearAgo = calendar.date(byAdding: .year, value: -1, to: now()) else { return nil }
            var utc = Calendar(identifier: .gregorian)
            utc.timeZone = TimeZone(identifier: "UTC") ?? .gmt
            return utc.date(from: calendar.dateComponents([.year, .month, .day], from: yearAgo))
        }
    }

    private func toggleRatingHistory() {
        guard case .content(let detail) = uiState, var section = detail.ratingHistory else { return }
        section.isExpanded.toggle()
        updateContent { $0.ratingHistory = section }
        guard section.isExpanded else { return }
        analytics.ratingHistoryOpened()
        if ratingHistoryTask == nil {
            startRatingHistory()
        }
    }

    private func startRatingHistory() {
        guard let ratingsCentralId else { return }
        ratingHistoryTask?.cancel()
        ratingHistoryTask = Task {
            updateContent { $0.ratingHistory?.history = .loading }
            guard let section = await loadSection({ try await self.loadRatingHistory(ratingsCentralId) }, map: { (history: RatingHistory?) in
                history?.toUi() ?? RatingHistoryUi(updates: [])
            }) else { return }
            updateContent { $0.ratingHistory?.history = section }
        }
    }

    private func updateContent(_ change: (inout PlayerDetailUi) -> Void) {
        guard case .content(var detail) = uiState else { return }
        change(&detail)
        uiState = .content(detail)
    }

    /// Nil when the load was cancelled, so the load that replaced it owns the section.
    private func loadSection<Item, Value: Equatable>(
        _ load: () async throws -> DataResult,
        map: (Item?) -> Value
    ) async -> SectionUiState<Value>? {
        do {
            let result = try await load()
            guard !Task.isCancelled else { return nil }
            switch onEnum(of: result) {
            case .success(let success): return .loaded(map(success.value as? Item))
            case .unavailable: return .unavailable
            }
        } catch {
            // Only cancellation can throw now.
            return nil
        }
    }
}

private extension PlayerClub {
    func toUi() -> PlayerClubUi {
        PlayerClubUi(clubName: clubName, associationName: associationCode)
    }
}

private extension RatingHistory {
    func toUi() -> RatingHistoryUi {
        RatingHistoryUi(updates: updates.map { update in
            RatingUpdateUi(
                date: Date(timeIntervalSince1970: TimeInterval(update.date.toEpochDays()) * 86_400),
                name: update.name,
                points: Int(update.after.points),
                deviation: Int(update.after.deviation),
                pointChange: Int(update.pointChange)
            )
        })
    }
}

private extension PlayerTeams {
    func toUi() -> PlayerTeamsUi {
        PlayerTeamsUi(teams: teams.map { PlayerTeamUi(id: $0.id, name: $0.name) }, rcPoints: rcPoints?.intValue, ageGroup: ageGroup)
    }
}
