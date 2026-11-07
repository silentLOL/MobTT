import Foundation

enum LeagueDetailPreviewData {
    static let loaded = LeagueDetailUi(
        leagueName: "1. Landesliga Herren",
        standings: [
            StandingRowUi(id: "voest-1", position: 1, teamName: "SK VÖEST Wien 1", played: 6, points: 11, isThisTeam: false, opensTeam: true),
            StandingRowUi(id: "olympic-1", position: 2, teamName: "TTC Olympic 1", played: 6, points: 9, isThisTeam: false, opensTeam: true),
            StandingRowUi(id: "nussdorf-1", position: 3, teamName: "Nussdorf TTC 1", played: 6, points: 4, isThisTeam: false, opensTeam: true),
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
                StandingRowUi(id: $0.id, position: $0.position, teamName: $0.teamName, played: $0.played, points: $0.points, isThisTeam: $0.id == "olympic-1", opensTeam: $0.id != "olympic-1")
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
            StandingRowUi(id: "h1", position: 1, teamName: "Sportunion Hollabrunn Tischtennis 1", played: 4, points: 8, isThisTeam: false, opensTeam: true),
            StandingRowUi(id: "s1", position: 2, teamName: "Union Tischtennisclub St. Pölten Nord-West 1", played: 4, points: 7, isThisTeam: false, opensTeam: true),
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

    static let ranking = [
        PlayerRankingRowUi(playerId: "bauer", teamId: "voest-1", name: "Lukas Bauer", rank: 1, teamName: "SK VÖEST Wien 1", matchesWon: 14, matchesLost: 2, gamesPlayed: 6),
        PlayerRankingRowUi(playerId: "gruber", teamId: "olympic-1", name: "Anna Gruber", rank: 2, teamName: "TTC Olympic 1", matchesWon: 11, matchesLost: 4, gamesPlayed: 6),
        PlayerRankingRowUi(playerId: "wagner", teamId: "nussdorf-1", name: "Sophie Wagner", rank: nil, teamName: "Nussdorf TTC 1", matchesWon: 1, matchesLost: 2, gamesPlayed: 1),
    ]

    static let longNamesRanking = [
        PlayerRankingRowUi(
            playerId: "pichler",
            teamId: "s1",
            name: "Katharina Habsburg-Lothringen-Esterházy",
            rank: 1,
            teamName: "Union Tischtennisclub St. Pölten Nord-West 1",
            matchesWon: 20,
            matchesLost: 3,
            gamesPlayed: 8
        ),
        PlayerRankingRowUi(playerId: "moser", teamId: "gone", name: "Felix Moser", rank: 2, teamName: nil, matchesWon: 9, matchesLost: 9, gamesPlayed: 6),
    ]

    /// Loaded, with the ranking and My Player marked.
    static var withRanking: LeagueDetailUi {
        var detail = loaded
        detail.ranking = .loaded(ranking)
        detail.myPlayerId = "gruber"
        return detail
    }
}
