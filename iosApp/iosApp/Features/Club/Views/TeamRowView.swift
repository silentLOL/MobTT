import SwiftUI

/// Tapping the row opens the Team; the chevron reveals the next Fixture without leaving the Club.
struct TeamRowView: View {
    let team: TeamUi
    let onTap: () -> Void

    @State private var isExpanded = false

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            HStack(spacing: 0) {
                Button(action: onTap) {
                    VStack(alignment: .leading, spacing: 0) {
                        Text(team.name)
                            .batTextStyle(BatTypography.title)
                            .foregroundStyle(BatColor.textPrimary)
                        if let leagueName = team.leagueName {
                            Text(leagueName)
                                .batTextStyle(BatTypography.body)
                                .foregroundStyle(BatColor.textSecondary)
                        }
                    }
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .padding(.leading, BatSpacing.lg)
                    .padding(.vertical, BatSpacing.md)
                    .contentShape(Rectangle())
                }
                .buttonStyle(.plain)
                Button {
                    withAnimation { isExpanded.toggle() }
                } label: {
                    Image(systemName: "chevron.down")
                        .foregroundStyle(BatColor.textSecondary)
                        .rotationEffect(.degrees(isExpanded ? 180 : 0))
                        .frame(width: 44, height: 44)
                }
                .buttonStyle(.plain)
                .padding(.horizontal, BatSpacing.sm)
                .accessibilityLabel(
                    isExpanded
                        ? String(localized: "club_team_collapse_cd \(team.name)")
                        : String(localized: "club_team_expand_cd \(team.name)")
                )
            }
            if isExpanded {
                VStack(alignment: .leading, spacing: BatSpacing.md) {
                    Divider().overlay(BatColor.border)
                    ForEach(Array(team.fixtures.enumerated()), id: \.offset) { _, entry in
                        VStack(alignment: .leading, spacing: BatSpacing.xs) {
                            if team.fixtures.count > 1, let leagueName = entry.leagueName {
                                Text(leagueName)
                                    .batTextStyle(BatTypography.caption)
                                    .foregroundStyle(BatColor.textSecondary)
                            }
                            NextFixtureView(nextFixture: entry.nextFixture)
                        }
                        .accessibilityElement(children: .combine)
                    }
                }
                .padding(.horizontal, BatSpacing.lg)
                .padding(.bottom, BatSpacing.lg)
            }
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(BatColor.surface, in: BatShapes.large)
        .overlay(BatShapes.large.stroke(BatColor.border, lineWidth: BatSizing.borderThin))
        .batShadow(.sm)
    }
}

#Preview {
    BatPreviewMatrix {
        VStack(spacing: BatSpacing.md) {
            ForEach(ClubPreviewData.overview.teams) { TeamRowView(team: $0, onTap: {}) }
            TeamRowView(team: ClubPreviewData.noLeague.teams[0], onTap: {})
            TeamRowView(team: ClubPreviewData.longNames.teams[0], onTap: {})
        }
    }
}
