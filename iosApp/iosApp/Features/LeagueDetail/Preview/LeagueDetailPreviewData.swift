import Foundation

enum LeagueDetailPreviewData {
    static let loaded = LeagueDetailUi(
        leagueName: "1. Landesliga Herren",
        standings: [
            StandingRowUi(id: "voest-1", position: 1, teamName: "SK VÖEST Wien 1", played: 6, points: 11, isThisTeam: false),
            StandingRowUi(id: "olympic-1", position: 2, teamName: "TTC Olympic 1", played: 6, points: 9, isThisTeam: false),
            StandingRowUi(id: "nussdorf-1", position: 3, teamName: "Nussdorf TTC 1", played: 6, points: 4, isThisTeam: false),
        ],
        fixtures: [
            FixtureRowUi(
                id: "f3",
                homeTeamId: "nussdorf-ttc-1", homeTeamName: "Nussdorf TTC 1",
                awayTeamId: "ttc-olympic-1", awayTeamName: "TTC Olympic 1",
                startsAt: Date(timeIntervalSince1970: 1_799_000_000),
                venue: nil,
                score: FixtureScoreUi(home: 4, away: 6)
            ),
            FixtureRowUi(
                id: "f1",
                homeTeamId: "ttc-olympic-1", homeTeamName: "TTC Olympic 1",
                awayTeamId: "sk-voest-wien-1", awayTeamName: "SK VÖEST Wien 1",
                startsAt: Date(timeIntervalSince1970: 1_800_000_000),
                venue: "Sporthalle Olympic",
                score: nil
            ),
        ]
    )

    /// Opened from TTC Olympic 1: its table row and Fixtures are marked and the filter is offered.
    static var highlighted: LeagueDetailUi {
        LeagueDetailUi(
            leagueName: loaded.leagueName,
            standings: loaded.standings.map {
                StandingRowUi(id: $0.id, position: $0.position, teamName: $0.teamName, played: $0.played, points: $0.points, isThisTeam: $0.id == "olympic-1")
            },
            fixtures: loaded.fixtures.map {
                FixtureRowUi(
                    id: $0.id,
                    homeTeamId: $0.homeTeamId,
                    homeTeamName: $0.homeTeamName,
                    awayTeamId: $0.awayTeamId,
                    awayTeamName: $0.awayTeamName,
                    startsAt: $0.startsAt,
                    venue: $0.venue,
                    score: $0.score,
                    isThisTeam: $0.homeTeamName == "TTC Olympic 1" || $0.awayTeamName == "TTC Olympic 1"
                )
            }
            + [FixtureRowUi(id: "f4", homeTeamId: "nussdorf-ttc-1", homeTeamName: "Nussdorf TTC 1", awayTeamId: "sk-voest-wien-1", awayTeamName: "SK VÖEST Wien 1", startsAt: Date(timeIntervalSince1970: 1_800_500_000), venue: nil, score: nil)],
            canFilterToTeam: true
        )
    }

    static var onlyTeamFixtures: LeagueDetailUi {
        var detail = highlighted
        detail.onlyTeamFixtures = true
        return detail
    }

    static let empty = LeagueDetailUi(leagueName: loaded.leagueName, standings: [], fixtures: [])

    static let longNames = LeagueDetailUi(
        leagueName: "Gebietsliga Nord Herren Oberes Playoff",
        standings: [
            StandingRowUi(id: "h1", position: 1, teamName: "Sportunion Hollabrunn Tischtennis 1", played: 4, points: 8, isThisTeam: false),
            StandingRowUi(id: "s1", position: 2, teamName: "Union Tischtennisclub St. Pölten Nord-West 1", played: 4, points: 7, isThisTeam: false),
        ],
        fixtures: [
            FixtureRowUi(
                id: "f1",
                homeTeamId: "union-tischtennisclub-st.-polten-nord-west-1", homeTeamName: "Union Tischtennisclub St. Pölten Nord-West 1",
                awayTeamId: "sportunion-hollabrunn-tischtennis-1", awayTeamName: "Sportunion Hollabrunn Tischtennis 1",
                startsAt: Date(timeIntervalSince1970: 1_800_000_000),
                venue: "Mehrzweckhalle der Marktgemeinde St. Pölten Nord-West",
                score: nil
            ),
        ]
    )
}
