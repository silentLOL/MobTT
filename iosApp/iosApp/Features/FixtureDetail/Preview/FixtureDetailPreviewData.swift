import Foundation

enum FixtureDetailPreviewData {
    static let played = FixtureDetailUi(
        homeTeamId: "ttc-olympic-1", homeTeamName: "TTC Olympic 1",
        awayTeamId: "sk-voest-wien-1", awayTeamName: "SK VÖEST Wien 1",
        startsAt: Date(timeIntervalSince1970: 1_800_000_000),
        venue: "Sporthalle Olympic",
        address: "Hauptstraße 12, 1010 Wien",
        score: FixtureScoreUi(home: 6, away: 4),
        matches: [
            MatchRowUi(id: 1, number: 1, isDoubles: false, homePlayers: "Michael Huber", awayPlayers: "Lukas Bauer", score: FixtureScoreUi(home: 3, away: 1)),
            MatchRowUi(id: 2, number: 2, isDoubles: false, homePlayers: "Anna Gruber", awayPlayers: "Sophie Wagner", score: FixtureScoreUi(home: 1, away: 3)),
            MatchRowUi(id: 3, number: 3, isDoubles: true, homePlayers: "Michael Huber / Anna Gruber", awayPlayers: "Lukas Bauer / Sophie Wagner", score: FixtureScoreUi(home: 3, away: 2)),
            MatchRowUi(id: 4, number: 4, isDoubles: false, homePlayers: "Anna Gruber", awayPlayers: "Lukas Bauer", score: nil),
        ],
        canShare: true
    )

    /// A Fixture in the future: no score and no Matches.
    static let scheduled = FixtureDetailUi(
        homeTeamId: played.homeTeamId, homeTeamName: played.homeTeamName,
        awayTeamId: played.awayTeamId, awayTeamName: played.awayTeamName,
        startsAt: played.startsAt,
        venue: played.venue,
        address: "Wien",
        score: nil,
        matches: []
    )

    /// Played, but XTTV has no Match results for it.
    static let noMatches = FixtureDetailUi(
        homeTeamId: played.homeTeamId, homeTeamName: played.homeTeamName,
        awayTeamId: played.awayTeamId, awayTeamName: played.awayTeamName,
        startsAt: played.startsAt,
        venue: played.venue,
        address: played.address,
        score: played.score,
        matches: []
    )

    static let longNames = FixtureDetailUi(
        homeTeamId: "h", homeTeamName: "Union Tischtennisclub St. Pölten Nord-West 1",
        awayTeamId: "a", awayTeamName: "Sportunion Hollabrunn Tischtennis 1",
        startsAt: played.startsAt,
        venue: "Mehrzweckhalle der Marktgemeinde St. Pölten Nord-West",
        address: "Dr.-Karl-Renner-Promenade 123a/Stiege 4, 3100 St. Pölten",
        score: FixtureScoreUi(home: 2, away: 8),
        matches: [
            MatchRowUi(
                id: 1, number: 1, isDoubles: true,
                homePlayers: "Katharina Habsburg-Lothringen-Esterházy / Maximilian Mustermann-Musterfrau",
                awayPlayers: "Alexander von Hohenberg-Wartenstein / Christina Schwarzenberg-Lobkowicz",
                score: FixtureScoreUi(home: 2, away: 3)
            ),
        ],
        canShare: true
    )
}
