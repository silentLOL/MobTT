import SwiftUI

struct TeamCardView: View {
    let team: TeamUi

    var body: some View {
        BatCardView {
            VStack(alignment: .leading, spacing: BatSpacing.md) {
                VStack(alignment: .leading, spacing: 0) {
                    Text(team.name)
                        .batTextStyle(BatTypography.title)
                        .foregroundStyle(BatColor.textPrimary)
                    if let leagueName = team.leagueName {
                        Text(leagueName)
                            .batTextStyle(BatTypography.body)
                            .foregroundStyle(BatColor.textSecondary)
                    }
                    if let standing = team.standing {
                        Text("club_team_standing_label \(standing.position) \(standing.played) \(standing.points)")
                            .batTextStyle(BatTypography.caption)
                            .foregroundStyle(BatColor.textSecondary)
                    }
                }
                Divider().overlay(BatColor.border)
                NextFixtureView(nextFixture: team.nextFixture)
            }
        }
    }
}

#Preview {
    BatPreviewMatrix {
        VStack(spacing: BatSpacing.md) {
            ForEach(ClubPreviewData.overview.teams) { TeamCardView(team: $0) }
            TeamCardView(team: ClubPreviewData.noLeague.teams[0])
            TeamCardView(team: ClubPreviewData.longNames.teams[0])
        }
    }
}
