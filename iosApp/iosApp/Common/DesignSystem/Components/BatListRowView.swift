import SwiftUI

/// Frames `BatListRowView`s and `BatListItemView`s as one list: a single rounded, bordered block with no gaps between rows.
struct BatListView<Content: View>: View {
    @ViewBuilder let content: () -> Content

    var body: some View {
        VStack(spacing: 0) { content() }
            .frame(maxWidth: .infinity, alignment: .leading)
            .background(BatColor.surface)
            .clipShape(BatShapes.large)
            .overlay(BatShapes.large.stroke(BatColor.border, lineWidth: BatSizing.borderThin))
    }
}

/// The frame of one `BatListView` row with free content. Pass `isStriped` on every other row for the darker band; `isHighlighted` tints the row and wins over the band. The content brings its own targets and accessibility.
struct BatListItemView<Content: View>: View {
    var isStriped = false
    var isHighlighted = false
    @ViewBuilder let content: () -> Content

    var body: some View {
        HStack(spacing: BatSpacing.md) { content() }
            .padding(.horizontal, BatSpacing.md)
            .padding(.vertical, BatSpacing.sm)
            .frame(maxWidth: .infinity, minHeight: BatSizing.minTouchTarget, alignment: .leading)
            .background(background)
    }

    private var background: Color {
        if isHighlighted { BatColor.brandContainer } else if isStriped { BatColor.surfaceMuted } else { BatColor.surface }
    }
}

/// A row of a `BatListView` with a title, an optional leading text (e.g. a rank), supporting text and badge below the title, and a trailing text that lines up in a column across rows. Pass `action` to make the whole row the target; `accessibilityText` replaces what the content reads out.
struct BatListRowView: View {
    let title: String
    var leadingText: String?
    var supportingText: String?
    var trailingText: String?
    var badge: String?
    var isStriped = false
    var isHighlighted = false
    var accessibilityText: String?
    var action: (() -> Void)?

    var body: some View {
        if let action {
            Button(action: action) { row }
                .buttonStyle(.plain)
        } else {
            row
        }
    }

    private var row: some View {
        BatListItemView(isStriped: isStriped, isHighlighted: isHighlighted) {
            if let leadingText {
                Text(leadingText)
                    .batTextStyle(BatTypography.bodyStrong)
                    .monospacedDigit()
                    .foregroundStyle(BatColor.textSecondary)
                    .frame(minWidth: BatSizing.listLeadingColumn)
            }
            VStack(alignment: .leading, spacing: BatSpacing.xs) {
                Text(title)
                    .batTextStyle(BatTypography.bodyStrong)
                    .foregroundStyle(BatColor.textPrimary)
                if let supportingText {
                    Text(supportingText)
                        .batTextStyle(BatTypography.caption)
                        .foregroundStyle(BatColor.textSecondary)
                }
                if let badge {
                    BatBadgeView(text: badge)
                }
            }
            .frame(maxWidth: .infinity, alignment: .leading)
            if let trailingText {
                Text(trailingText)
                    .batTextStyle(BatTypography.caption)
                    .monospacedDigit()
                    .foregroundStyle(BatColor.textSecondary)
                    .frame(minWidth: BatSizing.listTrailingColumn, alignment: .trailing)
            }
        }
        .contentShape(Rectangle())
        .modifier(RowAccessibility(text: accessibilityText))
    }
}

private struct RowAccessibility: ViewModifier {
    let text: String?

    @ViewBuilder
    func body(content: Content) -> some View {
        if let text {
            content.accessibilityElement(children: .ignore).accessibilityLabel(text)
        } else {
            content.accessibilityElement(children: .combine)
        }
    }
}

#Preview {
    BatPreviewMatrix {
        BatListView {
            BatListRowView(title: "Katharina Huber", trailingText: "RC 1850", action: {})
            BatListRowView(title: "Max Mustermann", trailingText: "RC 1720", isStriped: true, action: {})
            BatListRowView(title: "Katharina Habsburg-Lothringen-Esterházy", trailingText: "RC 1604", action: {})
            BatListRowView(title: "Ohne RC", isStriped: true, action: {})
            BatListRowView(title: "TTC Olympic 1", leadingText: "2", supportingText: "6 played · 9 pts", badge: "This Team", isHighlighted: true)
        }
    }
}
