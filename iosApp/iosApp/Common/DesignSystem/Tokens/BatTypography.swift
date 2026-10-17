import SwiftUI

struct BatTextStyle {
    fileprivate let fontName: String
    fileprivate let size: CGFloat
    fileprivate let lineHeight: CGFloat
    fileprivate let tracking: CGFloat
    fileprivate let relativeTo: Font.TextStyle
}

enum BatTypography {
    static let display = style("Inter-Bold", 24, 32, relativeTo: .title)
    static let headline = style("Inter-Bold", 20, 28, relativeTo: .title2)
    static let title = style("Inter-Bold", 18, 28, relativeTo: .title3)
    static let subtitle = style("Inter-Bold", 16, 24, tracking: -0.2, relativeTo: .headline)
    static let body = style("Inter-Regular", 14, 20, relativeTo: .body)
    static let bodyStrong = style("Inter-SemiBold", 14, 20, relativeTo: .body)
    static let caption = style("Inter-Regular", 12, 16, relativeTo: .caption)
    static let captionStrong = style("Inter-SemiBold", 12, 16, relativeTo: .caption)
    static let overline = style("Inter-Bold", 11, 16, tracking: 0.6, relativeTo: .caption2)
    static let score = style("Inter-ExtraBold", 24, 32, tracking: 1, relativeTo: .title)

    private static func style(
        _ fontName: String,
        _ size: CGFloat,
        _ lineHeight: CGFloat,
        tracking: CGFloat = 0,
        relativeTo: Font.TextStyle
    ) -> BatTextStyle {
        BatTextStyle(fontName: fontName, size: size, lineHeight: lineHeight, tracking: tracking, relativeTo: relativeTo)
    }
}

/// Inter's natural line height is about 1.21 times its size, so only the excess becomes line spacing.
private struct BatTextStyleModifier: ViewModifier {
    let style: BatTextStyle
    @ScaledMetric private var size: CGFloat
    @ScaledMetric private var lineHeight: CGFloat
    @ScaledMetric private var tracking: CGFloat

    init(style: BatTextStyle) {
        self.style = style
        _size = ScaledMetric(wrappedValue: style.size, relativeTo: style.relativeTo)
        _lineHeight = ScaledMetric(wrappedValue: style.lineHeight, relativeTo: style.relativeTo)
        _tracking = ScaledMetric(wrappedValue: style.tracking, relativeTo: style.relativeTo)
    }

    func body(content: Content) -> some View {
        content
            .font(.custom(style.fontName, size: size))
            .tracking(tracking)
            .lineSpacing(max(0, lineHeight - size * 1.21))
    }
}

extension View {
    func batTextStyle(_ style: BatTextStyle) -> some View {
        modifier(BatTextStyleModifier(style: style))
    }
}
