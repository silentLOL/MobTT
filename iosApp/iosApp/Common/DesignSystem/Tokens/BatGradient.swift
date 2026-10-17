import SwiftUI

enum BatGradient {
    static let hero = LinearGradient(
        colors: [BatColor.heroStart, BatColor.heroEnd],
        startPoint: .topLeading,
        endPoint: .bottomTrailing
    )

    static let logo = LinearGradient(
        colors: [BatColor.logoStart, BatColor.logoEnd],
        startPoint: .topLeading,
        endPoint: .bottomTrailing
    )
}
