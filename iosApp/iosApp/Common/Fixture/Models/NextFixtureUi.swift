import Foundation
import Shared

enum NextFixtureUi: Equatable {
    case scheduled(opponentName: String, startsAt: Date, venue: String?, isHome: Bool)
    case freeWeek
}

extension NextFixture {
    func toUi() -> NextFixtureUi {
        switch onEnum(of: self) {
        case .freeWeek:
            .freeWeek
        case .scheduled(let scheduled):
            .scheduled(
                opponentName: scheduled.opponentName,
                startsAt: Date(timeIntervalSince1970: TimeInterval(scheduled.fixture.startsAt.toEpochMilliseconds()) / 1000),
                venue: scheduled.fixture.venue,
                isHome: scheduled.isHome
            )
        }
    }
}
