import Foundation
import Observation
import Shared

@MainActor
@Observable
final class ClubViewModel {
    private(set) var uiState: ClubUiState = .loading

    private let source: ClubSource
    private var clubId: String?
    private var loadTask: Task<Void, Never>?

    @ObservationIgnored private let observeUserContext = KoinHelper.shared.getObserveUserContextUseCase()
    @ObservationIgnored private let getClubOverview = KoinHelper.shared.getGetClubOverviewUseCase()
    @ObservationIgnored private let eventStream = AsyncStream<ClubEvent>.makeStream()

    var events: AsyncStream<ClubEvent> { eventStream.stream }

    init(source: ClubSource) {
        self.source = source
    }

    /// Runs until the calling task is cancelled.
    func observe() async {
        switch source {
        case .specific(let clubId):
            self.clubId = clubId
            await load(clubId: clubId)
        case .mine:
            for await context in observeUserContext.invoke() {
                clubId = context.club?.id
                loadTask?.cancel()
                if let clubId {
                    loadTask = Task { await load(clubId: clubId) }
                } else {
                    uiState = .noClub
                }
            }
        }
    }

    func onAction(_ action: ClubAction) {
        switch action {
        case .chooseClicked:
            eventStream.continuation.yield(.openSettings)
        case .teamClicked(let teamId):
            eventStream.continuation.yield(.openTeam(teamId: teamId))
        case .retryClicked:
            guard let clubId else { return }
            loadTask?.cancel()
            loadTask = Task { await load(clubId: clubId) }
        }
    }

    private func load(clubId: String) async {
        uiState = .loading
        do {
            let overview = try await getClubOverview.invoke(clubId: clubId)
            guard !Task.isCancelled else { return }
            // TODO: map to domain errors (offline vs. XTTV unreachable) once the real repositories exist.
            uiState = overview.map { .content($0.toUi()) } ?? .error
        } catch {
            guard !Task.isCancelled else { return }
            uiState = .error
        }
    }
}

private extension ClubOverview {
    func toUi() -> ClubOverviewUi {
        ClubOverviewUi(clubName: club.name, associationName: association.name, teams: teams.map { $0.toUi() })
    }
}

private extension TeamOverview {
    func toUi() -> TeamUi {
        TeamUi(
            id: team.id,
            name: team.name,
            leagueName: league?.name,
            standing: standing.map { StandingUi(position: Int($0.position), played: Int($0.played), points: Int($0.points)) },
            nextFixture: nextFixture.toUi()
        )
    }
}
