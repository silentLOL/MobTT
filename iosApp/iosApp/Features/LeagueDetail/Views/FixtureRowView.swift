import SwiftUI

/// One Fixture of a League: the pairing, when and where it is played, and the score once finished. The info button is the row's one action and opens the Fixture; Teams are opened from the standings.
struct FixtureRowView: View {
    let row: FixtureRowUi
    var isStriped = false
    /// Off when only the Team's own Fixtures are listed: marking every row would mark none.
    var showsThisTeam = true
    var highlightBadge = String(localized: "team_detail_this_team_badge")
    let onInfoTap: () -> Void

    var body: some View {
        BatListItemView(isStriped: isStriped, isHighlighted: showsThisTeam && row.isThisTeam) {
            HStack(spacing: BatSpacing.sm) {
                VStack(alignment: .leading, spacing: BatSpacing.xs) {
                    HStack(spacing: BatSpacing.sm) {
                        teamName(row.homeTeamName)
                        Text("league_detail_fixture_versus")
                            .batTextStyle(BatTypography.caption)
                            .foregroundStyle(BatColor.textSecondary)
                            .accessibilityHidden(true)
                        teamName(row.awayTeamName)
                        Spacer(minLength: 0)
                    }
                    if let score = row.score {
                        Text("league_detail_fixture_score \(score.home) \(score.away)")
                            .batTextStyle(BatTypography.bodyStrong)
                            .monospacedDigit()
                            .foregroundStyle(BatColor.textPrimary)
                    }
                    Text(detailText)
                        .batTextStyle(BatTypography.caption)
                        .foregroundStyle(BatColor.textSecondary)
                    if showsThisTeam && row.isThisTeam {
                        BatBadgeView(text: highlightBadge)
                    }
                }
                .frame(maxWidth: .infinity, alignment: .leading)
                BatIconButtonView(
                    icon: .info,
                    accessibilityText: String(localized: "league_detail_fixture_info_cd \(row.homeTeamName) \(row.awayTeamName)"),
                    action: onInfoTap
                )
            }
        }
    }

    private func teamName(_ name: String) -> some View {
        Text(name)
            .batTextStyle(BatTypography.bodyStrong)
            .foregroundStyle(BatColor.textPrimary)
    }

    private var kickoff: String { row.startsAt.formatted(date: .abbreviated, time: .shortened) }

    private var detailText: String {
        [kickoff, row.venue].compactMap { $0 }.joined(separator: " · ")
    }
}

#Preview {
    BatPreviewMatrix {
        BatListView {
            ForEach(Array(LeagueDetailPreviewData.highlighted.fixtures.enumerated()), id: \.element.id) { index, row in
                FixtureRowView(row: row, isStriped: !index.isMultiple(of: 2), onInfoTap: {})
            }
            ForEach(Array(LeagueDetailPreviewData.longNames.fixtures.enumerated()), id: \.element.id) { index, row in
                FixtureRowView(row: row, isStriped: !index.isMultiple(of: 2), onInfoTap: {})
            }
        }
    }
}
