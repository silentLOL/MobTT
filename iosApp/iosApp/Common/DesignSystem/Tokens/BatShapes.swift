import SwiftUI

enum BatShapes {
    static let small = RoundedRectangle(cornerRadius: 8, style: .continuous)
    static let medium = RoundedRectangle(cornerRadius: 12, style: .continuous)
    static let large = RoundedRectangle(cornerRadius: 16, style: .continuous)
    static let extraLarge = RoundedRectangle(cornerRadius: 24, style: .continuous)
    static let sheet = UnevenRoundedRectangle(topLeadingRadius: 24, topTrailingRadius: 24, style: .continuous)
    static let full = Capsule()
}
