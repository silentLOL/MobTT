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
                homeTeamName: "Nussdorf TTC 1",
                awayTeamName: "TTC Olympic 1",
                startsAt: Date(timeIntervalSince1970: 1_799_000_000),
                venue: nil,
                score: FixtureScoreUi(home: 4, away: 6)
            ),
            FixtureRowUi(
                id: "f1",
                homeTeamName: "TTC Olympic 1",
                awayTeamName: "SK VÖEST Wien 1",
                startsAt: Date(timeIntervalSince1970: 1_800_000_000),
                venue: "Sporthalle Olympic",
                score: nil
            ),
        ]
    )

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
                homeTeamName: "Union Tischtennisclub St. Pölten Nord-West 1",
                awayTeamName: "Sportunion Hollabrunn Tischtennis 1",
                startsAt: Date(timeIntervalSince1970: 1_800_000_000),
                venue: "Mehrzweckhalle der Marktgemeinde St. Pölten Nord-West",
                score: nil
            ),
        ]
    )
}
