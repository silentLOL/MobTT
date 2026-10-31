import Foundation

enum ClubPreviewData {
    private static let kickoff = Date(timeIntervalSince1970: 1_800_000_000)

    static var favorite: ClubOverviewUi {
        var overview = overview
        overview.isFavorite = true
        return overview
    }

    static let overview = ClubOverviewUi(
        clubName: "TTC Olympic",
        associationName: "Wiener Tischtennisverband (WTTV)",
        teams: [
            TeamUi(
                id: "olympic-1",
                name: "TTC Olympic 1",
                leagueName: "1. Landesliga Herren",
                standing: StandingUi(position: 2, played: 6, points: 9),
                fixtures: [
                    TeamFixtureUi(leagueName: "1. Landesliga Herren", nextFixture: .scheduled(opponentName: "SK VÖEST Wien 1", startsAt: kickoff, venue: "Sporthalle Olympic", isHome: true)),
                    TeamFixtureUi(leagueName: "Herren-Cup", nextFixture: .scheduled(opponentName: "Nussdorf TTC 1", startsAt: kickoff, venue: nil, isHome: false)),
                ]
            ),
            TeamUi(
                id: "olympic-2",
                name: "TTC Olympic 2",
                leagueName: "2. Landesliga Herren",
                standing: StandingUi(position: 2, played: 5, points: 6),
                fixtures: [TeamFixtureUi(leagueName: nil, nextFixture: .scheduled(opponentName: "Nussdorf TTC 2", startsAt: kickoff, venue: nil, isHome: false))]
            ),
            TeamUi(
                id: "olympic-3",
                name: "TTC Olympic 3",
                leagueName: "2. Landesliga Herren",
                standing: StandingUi(position: 3, played: 5, points: 3),
                fixtures: [TeamFixtureUi(leagueName: nil, nextFixture: .freeWeek)]
            ),
        ],
        players: [
            LineupPlayerUi(id: "huber", name: "Michael Huber", rcPoints: 2114),
            LineupPlayerUi(id: "gruber", name: "Anna Gruber", rcPoints: 1992),
            LineupPlayerUi(id: "eder", name: "Thomas Eder", rcPoints: nil),
        ]
    )

    static let longNames = ClubOverviewUi(
        clubName: "Union Tischtennisclub St. Pölten Nord-West",
        associationName: "Niederösterreichischer Tischtennisverband (NÖTTV)",
        teams: [
            TeamUi(
                id: "s1",
                name: "Union Tischtennisclub St. Pölten Nord-West 1",
                leagueName: "Gebietsliga Nord Herren Oberes Playoff",
                standing: StandingUi(position: 1, played: 4, points: 8),
                fixtures: [TeamFixtureUi(leagueName: nil, nextFixture: .scheduled(
                    opponentName: "Sportunion Hollabrunn Tischtennis 1",
                    startsAt: kickoff,
                    venue: "Sporthalle Hollabrunn Mitte",
                    isHome: false
                ))]
            ),
        ]
    )

    static let noTeams = ClubOverviewUi(clubName: "TTC Olympic", associationName: "Wiener Tischtennisverband (WTTV)", teams: [])

    static let noLeague = ClubOverviewUi(
        clubName: "TTC Olympic",
        associationName: "Wiener Tischtennisverband (WTTV)",
        teams: [TeamUi(id: "olympic-1", name: "TTC Olympic 1", leagueName: nil, standing: nil, fixtures: [TeamFixtureUi(leagueName: nil, nextFixture: .freeWeek)])]
    )
}
