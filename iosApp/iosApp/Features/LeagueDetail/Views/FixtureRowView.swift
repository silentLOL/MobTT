import SwiftUI

/// One Fixture of a League: the pairing, when and where it is played, and the score once finished. Each Team opens its Team, except `highlightTeamId`, the Team the user came from.
struct FixtureRowView: View {
    let row: FixtureRowUi
    var highlightTeamId: String?
    var isStriped = false
    let onTeamTap: (_ teamId: String) -> Void

    var body: some View {
        BatListItemView(isStriped: isStriped, isHighlighted: row.isThisTeam) {
            VStack(alignment: .leading, spacing: BatSpacing.xs) {
                teamLine(id: row.homeTeamId, name: row.homeTeamName)
                teamLine(id: row.awayTeamId, name: row.awayTeamName)
                Text(detailText)
                    .batTextStyle(BatTypography.caption)
                    .foregroundStyle(BatColor.textSecondary)
                if row.isThisTeam {
                    BatBadgeView(text: String(localized: "team_detail_this_team_badge"))
                }
            }
            .frame(maxWidth: .infinity, alignment: .leading)
            Text(scoreText)
                .batTextStyle(BatTypography.title)
                .foregroundStyle(BatColor.textPrimary)
                .accessibilityLabel(scoreAccessibilityText)
        }
    }

    /// Underlined when it opens the Team, so a link is never told apart by colour alone.
    @ViewBuilder
    private func teamLine(id: String, name: String) -> some View {
        if id == highlightTeamId {
            Text(name)
                .batTextStyle(BatTypography.bodyStrong)
                .foregroundStyle(BatColor.textPrimary)
                .frame(maxWidth: .infinity, minHeight: BatSizing.minTouchTarget, alignment: .leading)
        } else {
            Button { onTeamTap(id) } label: {
                Text(name)
                    .batTextStyle(BatTypography.bodyStrong)
                    .underline()
                    .foregroundStyle(BatColor.textPrimary)
                    .frame(maxWidth: .infinity, minHeight: BatSizing.minTouchTarget, alignment: .leading)
                    .contentShape(Rectangle())
            }
            .buttonStyle(.plain)
        }
    }

    private var kickoff: String { row.startsAt.formatted(date: .abbreviated, time: .shortened) }

    private var detailText: String {
        [kickoff, row.venue].compactMap { $0 }.joined(separator: " · ")
    }

    /// A Fixture not yet played has no result to show.
    private var scoreText: String {
        if let score = row.score {
            String(localized: "league_detail_fixture_score \(score.home) \(score.away)")
        } else {
            String(localized: "league_detail_fixture_no_score")
        }
    }

    private var scoreAccessibilityText: String {
        if let score = row.score {
            "\(score.home) : \(score.away)"
        } else {
            String(localized: "league_detail_fixture_no_score")
        }
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
