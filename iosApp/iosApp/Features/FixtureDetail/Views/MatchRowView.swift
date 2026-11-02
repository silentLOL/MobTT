import SwiftUI

/// One Match of a Fixture: its number and kind, the players of both sides (the winner in bold) and the Sets won.
struct MatchRowView: View {
    let row: MatchRowUi
    var isStriped = false

    var body: some View {
        BatListItemView(isStriped: isStriped) {
            HStack(spacing: BatSpacing.md) {
                Text("\(row.number)")
                    .batTextStyle(BatTypography.caption)
                    .foregroundStyle(BatColor.textSecondary)
                VStack(alignment: .leading, spacing: BatSpacing.xs) {
                    Text(row.isDoubles ? "fixture_detail_match_doubles" : "fixture_detail_match_singles")
                        .batTextStyle(BatTypography.caption)
                        .foregroundStyle(BatColor.textSecondary)
                    Text(row.homePlayers)
                        .batTextStyle(isWinner(home: true) ? BatTypography.bodyStrong : BatTypography.body)
                        .foregroundStyle(BatColor.textPrimary)
                    Text(row.awayPlayers)
                        .batTextStyle(isWinner(home: false) ? BatTypography.bodyStrong : BatTypography.body)
                        .foregroundStyle(BatColor.textPrimary)
                }
                .frame(maxWidth: .infinity, alignment: .leading)
                if let score = row.score {
                    Text("fixture_detail_score \(score.home) \(score.away)")
                        .batTextStyle(BatTypography.bodyStrong)
                        .monospacedDigit()
                        .foregroundStyle(BatColor.textPrimary)
                }
            }
        }
        .accessibilityElement(children: .ignore)
        .accessibilityLabel(accessibilityText)
    }

    private func isWinner(home: Bool) -> Bool {
        guard let score = row.score else { return false }
        return home ? score.home > score.away : score.away > score.home
    }

    private var accessibilityText: String {
        let kind = String(localized: row.isDoubles ? "fixture_detail_match_doubles" : "fixture_detail_match_singles")
        guard let score = row.score else {
            return String(localized: "fixture_detail_match_row_no_result_cd \(kind) \(row.number) \(row.homePlayers) \(row.awayPlayers)")
        }
        return String(localized: "fixture_detail_match_row_cd \(kind) \(row.number) \(row.homePlayers) \(row.awayPlayers) \(score.home) \(score.away)")
    }
}

#Preview {
    BatPreviewMatrix {
        BatListView {
            ForEach(Array((FixtureDetailPreviewData.played.matches + FixtureDetailPreviewData.longNames.matches).enumerated()), id: \.offset) { index, row in
                MatchRowView(row: row, isStriped: !index.isMultiple(of: 2))
            }
        }
    }
}
