import Charts
import SwiftUI

/// One point of a `BatRatingChart`: `value` drawn as the line, `low` to `high` as the band around it.
struct BatChartPoint: Equatable {
    let date: Date
    let value: Double
    let low: Double
    let high: Double
}

/// Most points that still get a dot each; above it, only the line and band are drawn.
let batChartMaxDots = 40

/// A line through `points` with a shaded band from each point's low to high, ordered by date. The vertical scale fits the band with some room above and below. With `showDots`, each point gets a dot. Tapping or dragging reports the nearest point's index to `onSelect`, and `selectedIndex` is marked with a rule. The chart is read as one element, `accessibilityText`.
struct BatRatingChart: View {
    let points: [BatChartPoint]
    let accessibilityText: String
    var showDots: Bool?
    var selectedIndex: Int?
    var onSelect: ((Int) -> Void)?

    @State private var touchedDate: Date?

    var body: some View {
        Chart {
            ForEach(Array(points.enumerated()), id: \.offset) { _, point in
                if points.count == 1 {
                    RuleMark(x: .value("Date", point.date), yStart: .value("Low", point.low), yEnd: .value("High", point.high))
                        .foregroundStyle(BatColor.brandContainer)
                        .lineStyle(StrokeStyle(lineWidth: BatSizing.chartDot, lineCap: .round))
                } else {
                    AreaMark(x: .value("Date", point.date), yStart: .value("Low", point.low), yEnd: .value("High", point.high))
                        .foregroundStyle(BatColor.brandContainer)
                    LineMark(x: .value("Date", point.date), y: .value("Rating", point.value))
                        .foregroundStyle(BatColor.brand)
                        .lineStyle(StrokeStyle(lineWidth: BatSizing.chartLine, lineCap: .round, lineJoin: .round))
                }
                if showDots ?? (points.count <= batChartMaxDots) {
                    PointMark(x: .value("Date", point.date), y: .value("Rating", point.value))
                        .foregroundStyle(BatColor.brand)
                        .symbolSize(BatSizing.chartDot * BatSizing.chartDot)
                }
            }
            if let selectedIndex, points.indices.contains(selectedIndex) {
                let point = points[selectedIndex]
                RuleMark(x: .value("Date", point.date))
                    .foregroundStyle(BatColor.borderStrong)
                    .lineStyle(StrokeStyle(lineWidth: BatSizing.borderThin))
                PointMark(x: .value("Date", point.date), y: .value("Rating", point.value))
                    .foregroundStyle(BatColor.brand)
                    .symbolSize(BatSizing.chartDot * BatSizing.chartDot * 2)
            }
        }
        .chartXSelection(value: $touchedDate)
        .onChange(of: touchedDate) { _, date in
            guard let date, let index = nearestIndex(to: date) else { return }
            onSelect?(index)
        }
        .chartYScale(domain: yDomain)
        .chartYAxis {
            AxisMarks(position: .leading, values: .automatic(desiredCount: 4)) { value in
                AxisGridLine().foregroundStyle(BatColor.border)
                AxisValueLabel {
                    if let points = value.as(Double.self) {
                        Text(points, format: .number.precision(.fractionLength(0)))
                            .batTextStyle(BatTypography.caption)
                            .foregroundStyle(BatColor.textTertiary)
                    }
                }
            }
        }
        .chartXAxis {
            AxisMarks(values: .automatic(desiredCount: 3)) { value in
                AxisValueLabel {
                    if let date = value.as(Date.self) {
                        Text(date, format: .dateTime.month(.abbreviated).year())
                            .batTextStyle(BatTypography.caption)
                            .foregroundStyle(BatColor.textTertiary)
                    }
                }
            }
        }
        .frame(height: BatSizing.chartHeight)
        .accessibilityElement(children: .ignore)
        .accessibilityLabel(Text(accessibilityText))
    }

    private func nearestIndex(to date: Date) -> Int? {
        points.indices.min { abs(points[$0].date.timeIntervalSince(date)) < abs(points[$1].date.timeIntervalSince(date)) }
    }

    /// The band's range with a tenth of it, at least 10 points, above and below.
    private var yDomain: ClosedRange<Double> {
        let low = points.map(\.low).min() ?? 0
        let high = points.map(\.high).max() ?? 1
        let padding = max((high - low) * 0.1, 10)
        return (low - padding)...(high + padding)
    }
}

#Preview {
    let start = Date(timeIntervalSince1970: 1_757_116_800)
    let points = (0..<12).map { index in
        let value = 900.0 + Double(index) * 12 + Double(index % 5) * 20
        let deviation = max(160.0 - Double(index) * 4, 60)
        return BatChartPoint(date: start.addingTimeInterval(Double(index) * 7 * 86_400), value: value, low: value - deviation, high: value + deviation)
    }
    BatPreviewMatrix {
        VStack(spacing: BatSpacing.lg) {
            BatRatingChart(points: points, accessibilityText: "Rating history", selectedIndex: 4)
            BatRatingChart(points: Array(points.prefix(1)), accessibilityText: "Rating history")
        }
    }
}
