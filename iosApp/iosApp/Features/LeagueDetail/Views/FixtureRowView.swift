import SwiftUI

/// One Fixture of a League: the pairing, when and where it is played, and the score once finished.
struct FixtureRowView: View {
    let row: FixtureRowUi

    var body: some View {
        HStack(spacing: BatSpacing.md) {
            VStack(alignment: .leading, spacing: BatSpacing.xs) {
                Text(row.homeTeamName)
                    .batTextStyle(BatTypography.bodyStrong)
                    .foregroundStyle(BatColor.textPrimary)
                Text(row.awayTeamName)
                    .batTextStyle(BatTypography.bodyStrong)
                    .foregroundStyle(BatColor.textPrimary)
                Text(detailText)
                    .batTextStyle(BatTypography.caption)
                    .foregroundStyle(BatColor.textSecondary)
            }
            .frame(maxWidth: .infinity, alignment: .leading)
            if let score = row.score {
                Text("league_detail_fixture_score \(score.home) \(score.away)")
                    .batTextStyle(BatTypography.title)
                    .foregroundStyle(BatColor.textPrimary)
            }
        }
        .padding(.horizontal, BatSpacing.md)
        .padding(.vertical, BatSpacing.sm)
        .accessibilityElement(children: .ignore)
        .accessibilityLabel(accessibilityText)
    }

    private var kickoff: String { row.startsAt.formatted(date: .abbreviated, time: .shortened) }

    private var detailText: String {
        [kickoff, row.venue].compactMap { $0 }.joined(separator: " · ")
    }

    private var accessibilityText: String {
        if let score = row.score {
            String(localized: "league_detail_fixture_result_row_cd \(row.homeTeamName) \(row.awayTeamName) \(kickoff) \(score.home) \(score.away)")
        } else {
            String(localized: "league_detail_fixture_row_cd \(row.homeTeamName) \(row.awayTeamName) \(kickoff)")
        }
    }
}

#Preview {
    BatPreviewMatrix {
        VStack(spacing: 0) {
            ForEach(LeagueDetailPreviewData.loaded.fixtures) { FixtureRowView(row: $0) }
            ForEach(LeagueDetailPreviewData.longNames.fixtures) { FixtureRowView(row: $0) }
        }
    }
}
