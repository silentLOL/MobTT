import SwiftUI

enum BatBackgroundStyle {
    case `default`
}

extension View {
    func batBackground(_ style: BatBackgroundStyle = .default) -> some View {
        background(BatColor.background.ignoresSafeArea())
    }
}
