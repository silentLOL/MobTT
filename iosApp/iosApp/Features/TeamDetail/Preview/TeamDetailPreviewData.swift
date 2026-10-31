import Foundation

enum TeamDetailPreviewData {
    static var favorite: TeamDetailUi {
        var detail = loaded
        detail.isFavorite = true
        return detail
    }

    static let loaded = TeamDetailUi(
        teamName: "TTC Olympic 1",
        clubName: "TTC Olympic",
        league: TeamLeagueUi(
            id: "landesliga1",
            name: "1. Landesliga Herren",
            standing: StandingUi(position: 2, played: 6, points: 9),
            nextFixture: .scheduled(
                opponentName: "SK VÖEST Wien 1",
                startsAt: Date(timeIntervalSince1970: 1_800_000_000),
                venue: "Sporthalle Olympic",
                isHome: true
            )
        ),
        lineup: [
            LineupPlayerUi(id: "huber", name: "Michael Huber"),
            LineupPlayerUi(id: "gruber", name: "Anna Gruber"),
            LineupPlayerUi(id: "eder", name: "Thomas Eder"),
        ]
    )

    static let emptyLineup = TeamDetailUi(
        teamName: loaded.teamName,
        clubName: loaded.clubName,
        league: loaded.league,
        lineup: []
    )

    static let freeWeek = TeamDetailUi(
        teamName: loaded.teamName,
        clubName: loaded.clubName,
        league: TeamLeagueUi(id: "landesliga1", name: "1. Landesliga Herren", standing: nil, nextFixture: .freeWeek),
        lineup: loaded.lineup
    )

    static let noLeague = TeamDetailUi(
        teamName: loaded.teamName,
        clubName: loaded.clubName,
        league: nil,
        lineup: loaded.lineup
    )

    static let longNames = TeamDetailUi(
        teamName: "Union Tischtennisclub St. Pölten Nord-West 1",
        clubName: "Union Tischtennisclub St. Pölten Nord-West",
        league: TeamLeagueUi(
            id: "g",
            name: "Gebietsliga Nord Herren Oberes Playoff",
            standing: StandingUi(position: 2, played: 4, points: 7),
            nextFixture: .scheduled(
                opponentName: "Sportunion Hollabrunn Tischtennis 1",
                startsAt: Date(timeIntervalSince1970: 1_800_000_000),
                venue: "Mehrzweckhalle der Marktgemeinde St. Pölten Nord-West",
                isHome: false
            )
        ),
        lineup: [LineupPlayerUi(id: "pichler", name: "Katharina Habsburg-Lothringen-Esterházy")]
    )
}
