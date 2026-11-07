import Foundation
import Testing

@testable import MobTT

struct FixtureShareTests {
    private let link = "https://links.mobtt.example/f/464821"
    private let vienna = TimeZone(identifier: "Europe/Vienna")!
    // 2027-01-15 08:00 UTC, 09:00 in Vienna.
    private let startsAt = Date(timeIntervalSince1970: 1_800_000_000)

    private var scheduled: FixtureDetailUi {
        FixtureDetailUi(
            homeTeamId: "t1",
            homeTeamName: "Hagenberg 3",
            awayTeamId: "t2",
            awayTeamName: "Freistadt 6",
            startsAt: startsAt,
            venue: "Sporthalle Hagenberg",
            address: "Hauptstraße 1, 4232 Hagenberg",
            score: nil,
            matches: []
        )
    }

    private var finished: FixtureDetailUi {
        FixtureDetailUi(
            homeTeamId: "t1",
            homeTeamName: "Hagenberg 3",
            awayTeamId: "t2",
            awayTeamName: "Freistadt 6",
            startsAt: startsAt,
            venue: "Sporthalle Hagenberg",
            address: "Hauptstraße 1, 4232 Hagenberg",
            score: FixtureScoreUi(home: 6, away: 4),
            matches: [
                MatchRowUi(id: 1, number: 1, isDoubles: true, homePlayers: "Huber / Bauer", awayPlayers: "Gruber / Wolf", score: FixtureScoreUi(home: 3, away: 1)),
                MatchRowUi(id: 2, number: 2, isDoubles: false, homePlayers: "Huber", awayPlayers: "Gruber", score: FixtureScoreUi(home: 2, away: 3)),
                MatchRowUi(id: 3, number: 3, isDoubles: false, homePlayers: "Bauer", awayPlayers: "Wolf", score: nil),
            ]
        )
    }

    /// The kickoff as the Fixture card shows it.
    private func kickoff(_ locale: Locale) -> String {
        startsAt.formatted(Date.FormatStyle(date: .abbreviated, time: .shortened, locale: locale, timeZone: vienna))
    }

    @Test func aFinishedFixtureSharesEveryMatchInGerman() {
        let german = Locale(identifier: "de")

        let share = FixtureShare.content(detail: finished, link: link, locale: german, timeZone: vienna)

        #expect(share.subject == "Hagenberg 3 – Freistadt 6")
        #expect(share.text == """
            Hagenberg 3 – Freistadt 6  6 : 4
            \(kickoff(german))
            Sporthalle Hagenberg
            Hauptstraße 1, 4232 Hagenberg

            Spiele
            1 Doppel: Huber / Bauer – Gruber / Wolf  3 : 1
            2 Einzel: Huber – Gruber  2 : 3
            3 Einzel: Bauer – Wolf

            https://links.mobtt.example/f/464821
            """)
    }

    @Test func aScheduledFixtureSaysItIsNotPlayedYetAndHasNoMatches() {
        let english = Locale(identifier: "en")

        let share = FixtureShare.content(detail: scheduled, link: link, locale: english, timeZone: vienna)

        #expect(share.text == """
            Hagenberg 3 – Freistadt 6  Not played yet
            \(kickoff(english))
            Sporthalle Hagenberg
            Hauptstraße 1, 4232 Hagenberg

            https://links.mobtt.example/f/464821
            """)
    }

    @Test func aFinishedFixtureWithoutMatchesSaysSoAndLeavesOutAMissingPlace() {
        let english = Locale(identifier: "en")
        let base = finished
        let noPlace = FixtureDetailUi(
            homeTeamId: base.homeTeamId,
            homeTeamName: base.homeTeamName,
            awayTeamId: base.awayTeamId,
            awayTeamName: base.awayTeamName,
            startsAt: base.startsAt,
            venue: nil,
            address: nil,
            score: base.score,
            matches: []
        )

        let share = FixtureShare.content(detail: noPlace, link: link, locale: english, timeZone: vienna)

        #expect(share.text == """
            Hagenberg 3 – Freistadt 6  6 : 4
            \(kickoff(english))

            Matches
            No Match results available yet.

            https://links.mobtt.example/f/464821
            """)
    }
}
