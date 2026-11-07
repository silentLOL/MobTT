import SwiftUI

/// The League's Player ranking below the Fixtures. Each row opens its Player; My Player's row is marked.
struct PlayerRankingSectionView: View {
    let ranking: PlayerRankingUiState
    let isMyPlayer: (_ playerId: String) -> Bool
    let onPlayerTap: (_ playerId: String) -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            BatSectionHeaderView(title: String(localized: "league_detail_ranking_title"))
            switch ranking {
            case .loading:
                ProgressView()
                    .tint(BatColor.brand)
                    .accessibilityLabel(Text("league_detail_ranking_loading_cd"))
                    .frame(maxWidth: .infinity)
                    .padding(BatSpacing.md)
            case .unavailable:
                message("league_detail_ranking_unavailable_body")
            case .loaded(let rows) where rows.isEmpty:
                message("league_detail_ranking_empty_body")
            case .loaded(let rows):
                BatListView {
                    ForEach(Array(rows.enumerated()), id: \.element.id) { index, row in
                        rankingRow(row, isMine: isMyPlayer(row.playerId), isStriped: !index.isMultiple(of: 2))
                    }
                }
            }
        }
    }

    private func rankingRow(_ row: PlayerRankingRowUi, isMine: Bool, isStriped: Bool) -> some View {
        let stats = String(localized: "league_detail_ranking_stats_label \(row.matchesWon) \(row.matchesLost) \(row.gamesPlayed)")
        let badge = String(localized: "league_detail_my_player_badge")
        let rankText = row.rank.map { String(localized: "league_detail_ranking_rank_cd \($0)") } ?? String(localized: "league_detail_ranking_unranked_cd")
        let description = [
            rankText,
            row.name,
            isMine ? badge : nil,
            row.teamName,
            String(localized: "league_detail_ranking_stats_cd \(row.matchesWon) \(row.matchesLost) \(row.gamesPlayed)"),
        ].compactMap { $0 }.joined(separator: ", ")
        return BatListRowView(
            title: row.name,
            leadingText: row.rank.map { "\($0)" } ?? String(localized: "league_detail_ranking_unranked_label"),
            supportingText: [row.teamName, stats].compactMap { $0 }.joined(separator: " · "),
            badge: isMine ? badge : nil,
            isStriped: isStriped,
            isHighlighted: isMine,
            accessibilityText: description,
            action: { onPlayerTap(row.playerId) }
        )
    }

    private func message(_ key: LocalizedStringKey) -> some View {
        Text(key)
            .batTextStyle(BatTypography.body)
            .foregroundStyle(BatColor.textSecondary)
    }
}

#Preview {
    BatPreviewMatrix {
        ScrollView {
            VStack(spacing: BatSpacing.lg) {
                PlayerRankingSectionView(ranking: .loaded(LeagueDetailPreviewData.ranking), isMyPlayer: { $0 == "gruber" }, onPlayerTap: { _ in })
                PlayerRankingSectionView(ranking: .loaded(LeagueDetailPreviewData.longNamesRanking), isMyPlayer: { _ in false }, onPlayerTap: { _ in })
                PlayerRankingSectionView(ranking: .loading, isMyPlayer: { _ in false }, onPlayerTap: { _ in })
                PlayerRankingSectionView(ranking: .unavailable, isMyPlayer: { _ in false }, onPlayerTap: { _ in })
                PlayerRankingSectionView(ranking: .loaded([]), isMyPlayer: { _ in false }, onPlayerTap: { _ in })
            }
        }
    }
}
