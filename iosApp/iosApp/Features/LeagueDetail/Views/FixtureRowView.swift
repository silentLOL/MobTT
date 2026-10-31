import SwiftUI

/// One Fixture of a League: the pairing, when and where it is played, and the score once finished. Each Team opens its Team, except `highlightTeamId`, the Team the user came from.
struct FixtureRowView: View {
    let row: FixtureRowUi
    var highlightTeamId: String?
    var isStriped = false
    /// Off when only the Team's own Fixtures are listed: marking every row would mark none.
    var showsThisTeam = true
    let onTeamTap: (_ teamId: String) -> Void

    var body: some View {
        BatListItemView(isStriped: isStriped, isHighlighted: showsThisTeam && row.isThisTeam) {
            VStack(spacing: BatSpacing.xs) {
                HStack(spacing: BatSpacing.sm) {
                    teamLine(id: row.homeTeamId, name: row.homeTeamName)
                    Text("league_detail_fixture_versus")
                        .batTextStyle(BatTypography.caption)
                        .foregroundStyle(BatColor.textSecondary)
                        .accessibilityHidden(true)
                    teamLine(id: row.awayTeamId, name: row.awayTeamName)
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
                    .multilineTextAlignment(.center)
                if showsThisTeam && row.isThisTeam {
                    BatBadgeView(text: String(localized: "team_detail_this_team_badge"))
                }
            }
            .frame(maxWidth: .infinity)
        }
    }

    /// Underlined when it opens the Team, so a link is never told apart by colour alone.
    @ViewBuilder
    private func teamLine(id: String, name: String) -> some View {
        if id == highlightTeamId {
            Text(name)
                .batTextStyle(BatTypography.bodyStrong)
                .foregroundStyle(BatColor.textPrimary)
                .multilineTextAlignment(.center)
                .frame(maxWidth: .infinity, minHeight: BatSizing.minTouchTarget)
        } else {
            Button { onTeamTap(id) } label: {
                Text(name)
                    .batTextStyle(BatTypography.bodyStrong)
                    .underline()
                    .foregroundStyle(BatColor.textPrimary)
                    .multilineTextAlignment(.center)
                    .frame(maxWidth: .infinity, minHeight: BatSizing.minTouchTarget)
                    .contentShape(Rectangle())
            }
            .buttonStyle(.plain)
        }
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
                FixtureRowView(row: row, isStriped: !index.isMultiple(of: 2), onTeamTap: { _ in })
            }
            ForEach(Array(LeagueDetailPreviewData.longNames.fixtures.enumerated()), id: \.element.id) { index, row in
                FixtureRowView(row: row, isStriped: !index.isMultiple(of: 2), onTeamTap: { _ in })
            }
        }
    }
}
