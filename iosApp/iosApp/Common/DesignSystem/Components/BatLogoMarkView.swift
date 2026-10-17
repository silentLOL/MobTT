import SwiftUI

struct BatLogoMarkView: View {
    @ScaledMetric private var size = BatSizing.logo

    var body: some View {
        BatIconView(icon: .logo, tint: BatColor.onHero, size: BatSizing.iconLarge)
            .frame(width: size, height: size)
            .background(BatGradient.logo, in: BatShapes.medium)
            .batShadow(.md, tint: BatColor.logoStart)
            .accessibilityHidden(true)
    }
}

#Preview {
    BatPreviewMatrix {
        BatLogoMarkView()
    }
}
