import SwiftUI

/// The collapsed Rating history: the Rating after each Rating update, with its Deviation as a band.
struct RatingHistorySectionView: View {
    let section: RatingHistorySectionUi
    let onToggle: () -> Void
    let onRetry: () -> Void

    var body: some View {
        LazySectionView(
            title: String(localized: "player_detail_rating_history_title"),
            isExpanded: section.isExpanded,
            onToggle: onToggle,
            section: section.history,
            loadingLabel: String(localized: "player_detail_rating_history_loading_cd"),
            unavailableText: String(localized: "player_detail_rating_history_unavailable_body"),
            onRetry: onRetry
        ) { history in
            RatingHistoryView(history: history)
        }
    }
}

private struct RatingHistoryView: View {
    let history: RatingHistoryUi

    var body: some View {
        if let latest = history.updates.last {
            VStack(alignment: .leading, spacing: BatSpacing.sm) {
                BatRatingChart(
                    points: history.updates.map { BatChartPoint(date: $0.date, value: Double($0.points), low: Double($0.points - $0.deviation), high: Double($0.points + $0.deviation)) },
                    accessibilityText: String(localized: "player_detail_rating_history_chart_cd \(latest.points) \(latest.deviation)")
                )
                if history.updates.count == 1 {
                    Text("player_detail_rating_history_single_body \(latest.points) \(latest.deviation) \(latest.date.formatted(Date.FormatStyle(date: .abbreviated, time: .omitted, timeZone: .gmt)))")
                        .batTextStyle(BatTypography.body)
                        .foregroundStyle(BatColor.textSecondary)
                }
            }
        } else {
            SectionMessageView(key: "player_detail_rating_history_empty_body")
        }
    }
}

#Preview {
    BatPreviewMatrix {
        VStack(spacing: BatSpacing.lg) {
            RatingHistorySectionView(section: RatingHistorySectionUi(), onToggle: {}, onRetry: {})
            RatingHistorySectionView(section: RatingHistorySectionUi(isExpanded: true, history: .loaded(PlayerDetailPreviewData.ratingHistory)), onToggle: {}, onRetry: {})
            RatingHistorySectionView(
                section: RatingHistorySectionUi(isExpanded: true, history: .loaded(RatingHistoryUi(updates: Array(PlayerDetailPreviewData.ratingHistory.updates.prefix(1))))),
                onToggle: {},
                onRetry: {}
            )
            RatingHistorySectionView(section: RatingHistorySectionUi(isExpanded: true, history: .loaded(RatingHistoryUi(updates: []))), onToggle: {}, onRetry: {})
            RatingHistorySectionView(section: RatingHistorySectionUi(isExpanded: true, history: .unavailable), onToggle: {}, onRetry: {})
        }
    }
}
