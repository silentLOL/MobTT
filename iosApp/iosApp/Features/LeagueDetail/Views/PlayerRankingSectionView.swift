import SwiftUI

/// The League's Player ranking below the Fixtures, collapsed until opened. Each row opens its Player; My Player's row is marked.
struct PlayerRankingSectionView: View {
    let ranking: SectionUiState<[PlayerRankingRowUi]>
    let isExpanded: Bool
    let onToggle: () -> Void
    let isMyPlayer: (_ playerId: String) -> Bool
    let onPlayerTap: (_ playerId: String) -> Void

    var body: some View {
        LazySectionView(
            title: String(localized: "league_detail_ranking_title"),
            isExpanded: isExpanded,
            onToggle: onToggle,
            section: ranking,
            loadingLabel: String(localized: "league_detail_ranking_loading_cd"),
            unavailableText: String(localized: "league_detail_ranking_unavailable_body")
        ) { rows in
            if rows.isEmpty {
                SectionMessageView(key: "league_detail_ranking_empty_body")
            } else {
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
}

#Preview {
    BatPreviewMatrix {
        ScrollView {
            VStack(spacing: BatSpacing.lg) {
                PlayerRankingSectionView(ranking: .loaded(LeagueDetailPreviewData.ranking), isExpanded: true, onToggle: {}, isMyPlayer: { $0 == "gruber" }, onPlayerTap: { _ in })
                PlayerRankingSectionView(ranking: .loaded(LeagueDetailPreviewData.longNamesRanking), isExpanded: true, onToggle: {}, isMyPlayer: { _ in false }, onPlayerTap: { _ in })
                PlayerRankingSectionView(ranking: .loading, isExpanded: false, onToggle: {}, isMyPlayer: { _ in false }, onPlayerTap: { _ in })
                PlayerRankingSectionView(ranking: .unavailable, isExpanded: true, onToggle: {}, isMyPlayer: { _ in false }, onPlayerTap: { _ in })
                PlayerRankingSectionView(ranking: .loaded([]), isExpanded: true, onToggle: {}, isMyPlayer: { _ in false }, onPlayerTap: { _ in })
            }
        }
    }
}
