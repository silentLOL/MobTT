import SwiftUI

struct NextFixtureView: View {
    let nextFixture: NextFixtureUi

    var body: some View {
        VStack(alignment: .leading, spacing: BatSpacing.xs) {
            Text("club_next_fixture_overline")
                .textCase(.uppercase)
                .batTextStyle(BatTypography.overline)
                .foregroundStyle(BatColor.textTertiary)
            switch nextFixture {
            case .freeWeek:
                Text("club_free_week_title")
                    .batTextStyle(BatTypography.bodyStrong)
                    .foregroundStyle(BatColor.textPrimary)
                Text("club_free_week_body")
                    .batTextStyle(BatTypography.body)
                    .foregroundStyle(BatColor.textSecondary)
            case .scheduled(let opponentName, let startsAt, let venue, let isHome):
                Text(isHome ? "club_fixture_home_label \(opponentName)" : "club_fixture_away_label \(opponentName)")
                    .batTextStyle(BatTypography.bodyStrong)
                    .foregroundStyle(BatColor.textPrimary)
                DetailRowView(icon: .calendar, text: startsAt.formatted(date: .abbreviated, time: .shortened))
                if let venue {
                    DetailRowView(icon: .location, text: venue)
                }
            }
        }
    }
}

private struct DetailRowView: View {
    let icon: BatIcon
    let text: String

    var body: some View {
        HStack(spacing: BatSpacing.sm) {
            BatIconView(icon: icon, size: BatSizing.iconSmall)
            Text(text)
                .batTextStyle(BatTypography.body)
                .foregroundStyle(BatColor.textSecondary)
        }
    }
}

#Preview {
    let kickoff = Date(timeIntervalSince1970: 1_800_000_000)
    BatPreviewMatrix {
        VStack(alignment: .leading, spacing: BatSpacing.lg) {
            NextFixtureView(nextFixture: .scheduled(opponentName: "SK VÖEST Wien 1", startsAt: kickoff, venue: "Sporthalle Olympic", isHome: true))
            NextFixtureView(nextFixture: .scheduled(opponentName: "SK VÖEST Wien 1", startsAt: kickoff, venue: nil, isHome: false))
            NextFixtureView(nextFixture: .freeWeek)
        }
    }
}
