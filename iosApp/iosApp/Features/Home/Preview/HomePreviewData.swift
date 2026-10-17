import Foundation

enum HomePreviewData {
    private static let kickoff = Date(timeIntervalSince1970: 1_800_000_000)

    static let fixtures = HomeFixturesUi.loaded([
        HomeFixtureUi(
            teamId: "olympic-1",
            teamName: "TTC Olympic 1",
            nextFixture: .scheduled(opponentName: "SK VÖEST Wien 1", startsAt: kickoff, venue: "Sporthalle Olympic", isHome: true)
        ),
        HomeFixtureUi(
            teamId: "olympic-2",
            teamName: "TTC Olympic 2",
            nextFixture: .scheduled(opponentName: "Nussdorf TTC 2", startsAt: kickoff, venue: nil, isHome: false)
        ),
        HomeFixtureUi(teamId: "olympic-3", teamName: "TTC Olympic 3", nextFixture: .freeWeek),
    ])
}
