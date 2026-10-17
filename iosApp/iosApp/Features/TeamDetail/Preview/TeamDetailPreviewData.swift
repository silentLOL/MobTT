import Foundation

enum TeamDetailPreviewData {
    static let loaded = TeamDetailUi(
        teamName: "TTC Olympic 1",
        clubName: "TTC Olympic",
        leagueName: "1. Landesliga Herren",
        standings: [
            StandingRowUi(id: "voest-1", position: 1, teamName: "SK VÖEST Wien 1", played: 6, points: 11, isThisTeam: false),
            StandingRowUi(id: "olympic-1", position: 2, teamName: "TTC Olympic 1", played: 6, points: 9, isThisTeam: true),
            StandingRowUi(id: "nussdorf-1", position: 3, teamName: "Nussdorf TTC 1", played: 6, points: 4, isThisTeam: false),
        ],
        lineup: [
            LineupPlayerUi(id: "huber", name: "Michael Huber"),
            LineupPlayerUi(id: "gruber", name: "Anna Gruber"),
            LineupPlayerUi(id: "eder", name: "Thomas Eder"),
        ]
    )

    static let emptyLineup = TeamDetailUi(
        teamName: loaded.teamName,
        clubName: loaded.clubName,
        leagueName: loaded.leagueName,
        standings: loaded.standings,
        lineup: []
    )

    static let noLeague = TeamDetailUi(
        teamName: loaded.teamName,
        clubName: loaded.clubName,
        leagueName: nil,
        standings: [],
        lineup: loaded.lineup
    )

    static let longNames = TeamDetailUi(
        teamName: "Union Tischtennisclub St. Pölten Nord-West 1",
        clubName: "Union Tischtennisclub St. Pölten Nord-West",
        leagueName: "Gebietsliga Nord Herren Oberes Playoff",
        standings: [
            StandingRowUi(id: "h1", position: 1, teamName: "Sportunion Hollabrunn Tischtennis 1", played: 4, points: 8, isThisTeam: false),
            StandingRowUi(id: "s1", position: 2, teamName: "Union Tischtennisclub St. Pölten Nord-West 1", played: 4, points: 7, isThisTeam: true),
        ],
        lineup: [LineupPlayerUi(id: "pichler", name: "Katharina Habsburg-Lothringen-Esterházy")]
    )
}
