import SwiftUI

/// The Team's League at a glance; opens the League with its full table and Fixtures. `teamName` adds a line naming the Team, for lists where two cards can show the same League.
struct TeamLeagueCardView: View {
    let league: TeamLeagueUi
    var teamName: String?
    let onTap: () -> Void

    var body: some View {
        BatCardView(onTap: onTap) {
            VStack(alignment: .leading, spacing: BatSpacing.md) {
                VStack(alignment: .leading, spacing: 0) {
                    if let teamName {
                        Text(teamName)
                            .batTextStyle(BatTypography.caption)
                            .foregroundStyle(BatColor.textSecondary)
                    }
                    Text(league.name)
                        .batTextStyle(BatTypography.title)
                        .foregroundStyle(BatColor.textPrimary)
                    if let standing = league.standing {
                        Text("club_team_standing_label \(standing.position) \(standing.played) \(standing.points)")
                            .batTextStyle(BatTypography.body)
                            .foregroundStyle(BatColor.textSecondary)
                    } else {
                        Text("team_detail_league_no_rank_label")
                            .batTextStyle(BatTypography.body)
                            .foregroundStyle(BatColor.textSecondary)
                    }
                }
                Divider().overlay(BatColor.border)
                NextFixtureView(nextFixture: league.nextFixture)
            }
        }
    }
}

#Preview {
    BatPreviewMatrix {
        VStack(spacing: BatSpacing.md) {
            TeamLeagueCardView(league: TeamDetailPreviewData.loaded.league!, onTap: {})
            TeamLeagueCardView(league: TeamDetailPreviewData.longNames.league!, onTap: {})
            TeamLeagueCardView(league: TeamDetailPreviewData.freeWeek.league!, onTap: {})
            TeamLeagueCardView(league: TeamDetailPreviewData.longNames.league!, teamName: "Union Tischtennisclub St. Pölten Nord-West 1", onTap: {})
        }
    }
}
