import SwiftUI

/// The Team is marked by a badge and a tinted row, never by colour alone.
struct StandingRowView: View {
    let row: StandingRowUi

    var body: some View {
        HStack(spacing: BatSpacing.md) {
            Text("\(row.position)")
                .batTextStyle(BatTypography.bodyStrong)
                .foregroundStyle(BatColor.textSecondary)
            VStack(alignment: .leading, spacing: BatSpacing.xs) {
                Text(row.teamName)
                    .batTextStyle(row.isThisTeam ? BatTypography.bodyStrong : BatTypography.body)
                    .foregroundStyle(BatColor.textPrimary)
                Text("team_detail_standing_stats_label \(row.played) \(row.points)")
                    .batTextStyle(BatTypography.caption)
                    .foregroundStyle(BatColor.textSecondary)
                if row.isThisTeam {
                    BatBadgeView(text: String(localized: "team_detail_this_team_badge"))
                }
            }
            .frame(maxWidth: .infinity, alignment: .leading)
        }
        .padding(.horizontal, BatSpacing.md)
        .padding(.vertical, BatSpacing.sm)
        .background(row.isThisTeam ? BatColor.brandContainer : Color.clear, in: BatShapes.large)
        .accessibilityElement(children: .ignore)
        .accessibilityLabel(accessibilityText)
    }

    private var accessibilityText: String {
        if row.isThisTeam {
            String(localized: "team_detail_standing_row_this_team_cd \(row.position) \(row.teamName) \(row.played) \(row.points)")
        } else {
            String(localized: "team_detail_standing_row_cd \(row.position) \(row.teamName) \(row.played) \(row.points)")
        }
    }
}

#Preview {
    BatPreviewMatrix {
        VStack(spacing: 0) {
            ForEach(LeagueDetailPreviewData.loaded.standings) { StandingRowView(row: $0) }
            ForEach(LeagueDetailPreviewData.longNames.standings) { StandingRowView(row: $0) }
        }
    }
}
