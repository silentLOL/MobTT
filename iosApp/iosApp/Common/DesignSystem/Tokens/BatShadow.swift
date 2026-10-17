import SwiftUI

enum BatShadow {
    case sm
    case md
    case lg
    case xl

    fileprivate var radius: CGFloat {
        switch self {
        case .sm: 2
        case .md: 6
        case .lg: 12
        case .xl: 24
        }
    }

    fileprivate var offsetY: CGFloat {
        switch self {
        case .sm: 1
        case .md: 4
        case .lg: 8
        case .xl: 16
        }
    }

    fileprivate var opacity: Double {
        switch self {
        case .sm: 0.08
        case .md: 0.10
        case .lg: 0.15
        case .xl: 0.20
        }
    }
}

extension View {
    /// `tint` colours the shadow, e.g. the brand colour under hero cards.
    func batShadow(_ shadow: BatShadow, tint: Color = .black) -> some View {
        self.shadow(color: tint.opacity(shadow.opacity), radius: shadow.radius, x: 0, y: shadow.offsetY)
    }
}
