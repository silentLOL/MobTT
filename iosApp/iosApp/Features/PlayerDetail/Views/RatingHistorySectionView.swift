import SwiftUI

/// The collapsed Rating history: the Rating after each Rating update, with its Deviation as a band.
struct RatingHistorySectionView: View {
    let section: RatingHistorySectionUi
    let onToggle: () -> Void
    let onRetry: () -> Void
    let onSpanChosen: (RatingHistorySpan) -> Void
    let onInfo: () -> Void

    var body: some View {
        LazySectionView(
            title: String(localized: "player_detail_rating_history_title"),
            isExpanded: section.isExpanded,
            onToggle: onToggle,
            section: section.history,
            loadingLabel: String(localized: "player_detail_rating_history_loading_cd"),
            unavailableText: String(localized: "player_detail_rating_history_unavailable_body"),
            onRetry: onRetry,
            headerAction: {
                BatIconButtonView(icon: .info, accessibilityText: String(localized: "player_detail_rating_info_cd"), action: onInfo)
            }
        ) { history in
            RatingHistoryView(history: history, section: section, onSpanChosen: onSpanChosen)
        }
    }
}

private struct RatingHistoryView: View {
    let history: RatingHistoryUi
    let section: RatingHistorySectionUi
    let onSpanChosen: (RatingHistorySpan) -> Void

    /// A touched Rating update; a new span starts without one.
    @State private var selected: Int?

    var body: some View {
        if history.updates.isEmpty {
            SectionMessageView(key: "player_detail_rating_history_empty_body")
        } else {
            VStack(alignment: .leading, spacing: BatSpacing.md) {
                BatSegmentedControlView(
                    options: RatingHistorySpan.allCases.map(\.label),
                    selectedIndex: RatingHistorySpan.allCases.firstIndex(of: section.span) ?? 0,
                    onSelect: { index in
                        selected = nil
                        onSpanChosen(RatingHistorySpan.allCases[index])
                    }
                )
                let updates = section.visibleUpdates
                if let latest = updates.last {
                    BatRatingChart(
                        points: updates.map { BatChartPoint(date: $0.date, value: Double($0.points), low: Double($0.points - $0.deviation), high: Double($0.points + $0.deviation)) },
                        accessibilityText: String(
                            localized: "player_detail_rating_history_chart_cd \(latest.points) \(latest.deviation) \(signed(latest.pointChange)) \(updates.count) \(updates[0].date.dayLabel)"
                        ),
                        showDots: section.showsDots,
                        selectedIndex: selected,
                        onSelect: { selected = $0 },
                        selectedDescription: selected.flatMap { updates.indices.contains($0) ? updates[$0].spoken : nil },
                        audioGraphTitle: String(localized: "player_detail_rating_history_title"),
                        valueName: String(localized: "player_detail_rating_history_value_name")
                    )
                    if let selected, updates.indices.contains(selected) {
                        RatingUpdateCalloutView(update: updates[selected])
                    } else if history.updates.count == 1 {
                        Text("player_detail_rating_history_single_body \(latest.points) \(latest.deviation) \(latest.date.dayLabel)")
                            .batTextStyle(BatTypography.body)
                            .foregroundStyle(BatColor.textSecondary)
                    }
                } else {
                    SectionMessageView(key: "player_detail_rating_history_no_recent_body")
                }
            }
        }
    }
}

/// The touched Rating update: when, which, and where it left the Rating.
private struct RatingUpdateCalloutView: View {
    let update: RatingUpdateUi

    var body: some View {
        VStack(alignment: .leading, spacing: BatSpacing.xxs) {
            Text(update.date.dayLabel)
                .batTextStyle(BatTypography.caption)
                .foregroundStyle(BatColor.textTertiary)
            Text(update.name)
                .batTextStyle(BatTypography.bodyStrong)
                .foregroundStyle(BatColor.textPrimary)
            Text("player_detail_rating_history_callout_value \(update.points) \(update.deviation) \(signed(update.pointChange))")
                .batTextStyle(BatTypography.body)
                .foregroundStyle(BatColor.textSecondary)
        }
        .accessibilityElement(children: .combine)
    }

}

/// A point change with its sign, e.g. +72 or −1119.
private func signed(_ change: Int) -> String {
    change > 0 ? "+\(change)" : change < 0 ? "\u{2212}\(-change)" : "0"
}

private extension RatingUpdateUi {
    /// What VoiceOver reads for a selected Rating update: the same values as the callout.
    var spoken: String {
        "\(date.dayLabel), \(name), " + String(localized: "player_detail_rating_history_callout_value \(points) \(deviation) \(signed(pointChange))")
    }
}

private extension RatingHistorySpan {
    var label: String {
        switch self {
        case .lastYear: String(localized: "player_detail_rating_history_span_year")
        case .all: String(localized: "player_detail_rating_history_span_all")
        }
    }
}

private extension Date {
    /// Rating update dates are midnight UTC.
    var dayLabel: String { formatted(Date.FormatStyle(date: .abbreviated, time: .omitted, timeZone: .gmt)) }
}

#Preview {
    BatPreviewMatrix {
        VStack(spacing: BatSpacing.lg) {
            RatingHistorySectionView(section: RatingHistorySectionUi(), onToggle: {}, onRetry: {}, onSpanChosen: { _ in }, onInfo: {})
            RatingHistorySectionView(section: RatingHistorySectionUi(isExpanded: true, history: .loaded(PlayerDetailPreviewData.ratingHistory)), onToggle: {}, onRetry: {}, onSpanChosen: { _ in }, onInfo: {})
            RatingHistorySectionView(
                section: RatingHistorySectionUi(isExpanded: true, history: .loaded(RatingHistoryUi(updates: Array(PlayerDetailPreviewData.ratingHistory.updates.prefix(1))))),
                onToggle: {},
                onRetry: {},
                onSpanChosen: { _ in },
                onInfo: {}
            )
            RatingHistorySectionView(section: RatingHistorySectionUi(isExpanded: true, history: .loaded(RatingHistoryUi(updates: []))), onToggle: {}, onRetry: {}, onSpanChosen: { _ in }, onInfo: {})
            RatingHistorySectionView(section: RatingHistorySectionUi(isExpanded: true, history: .unavailable), onToggle: {}, onRetry: {}, onSpanChosen: { _ in }, onInfo: {})
        }
    }
}
