import Shared

@testable import MobTT

/// One Verband with two Leagues; the first League has two Players of two Clubs. Records every read, so tests can
/// tell that a step is read once and typing never reads again.
final class PlayerDrillDownFixtures {
    let loaders: PlayerDrillDownLoaders

    init() {
        let log = Log()
        loaders = PlayerDrillDownLoaders(
            associations: {
                log.associationReads += 1
                return [Association(id: "w", name: "Wiener Tischtennisverband"), Association(id: "n", name: "Niederösterreichischer Tischtennisverband")]
            },
            leagues: {
                log.leagueReads.append($0)
                return [League(id: "l1", name: "1. Landesliga Herren"), League(id: "l2", name: "Gebietsliga Nord")]
            },
            players: {
                log.playerReads.append($0)
                return [
                    PlayerSearchResult(player: Player(id: "p1", name: "Anna Gruber", clubId: "c1"), clubName: "TTC Olympic"),
                    PlayerSearchResult(player: Player(id: "p2", name: "Michael Huber", clubId: "c2"), clubName: "Nussdorf TTC"),
                ]
            }
        )
        self.log = log
    }

    private let log: Log

    var reads: (associations: Int, leagues: [String], players: [String]) {
        (log.associationReads, log.leagueReads, log.playerReads)
    }

    private final class Log {
        var associationReads = 0
        var leagueReads: [String] = []
        var playerReads: [String] = []
    }
}

@MainActor
func waitUntil(_ condition: () -> Bool) async {
    for _ in 0..<100 where !condition() {
        await Task.yield()
    }
}
