import SwiftUI

struct BatHeroCardView<Content: View>: View {
    @ViewBuilder let content: () -> Content

    var body: some View {
        VStack(alignment: .leading, spacing: 0) { content() }
            .padding(BatSpacing.xxl)
            .frame(maxWidth: .infinity, alignment: .leading)
            .background(BatGradient.hero, in: BatShapes.extraLarge)
            .batShadow(.xl, tint: BatColor.heroStart)
    }
}

struct BatHeroPillView: View {
    let text: String

    var body: some View {
        Text(text)
            .textCase(.uppercase)
            .batTextStyle(BatTypography.overline)
            .foregroundStyle(BatColor.onHero)
            .padding(.horizontal, BatSpacing.md)
            .padding(.vertical, BatSpacing.xs)
            .background(BatColor.onHero.opacity(0.2), in: BatShapes.full)
    }
}

struct BatHeroStatRowView<Content: View>: View {
    @ViewBuilder let content: () -> Content

    var body: some View {
        VStack(spacing: BatSpacing.lg) {
            Divider().overlay(BatColor.onHero.opacity(0.15))
            HStack(alignment: .top, spacing: BatSpacing.md) { content() }
        }
        .padding(.top, BatSpacing.lg)
    }
}

#Preview {
    BatPreviewMatrix {
        BatHeroCardView {
            BatHeroPillView(text: "Active player")
            Text("Michael Huber")
                .batTextStyle(BatTypography.display)
                .foregroundStyle(BatColor.onHero)
                .padding(.top, BatSpacing.sm)
            Text("TTC Olympic • WTTV")
                .batTextStyle(BatTypography.body)
                .foregroundStyle(BatColor.onHeroMuted)
            BatHeroStatRowView {
                BatStatView(label: "QTTR points", value: "1842", onHero: true)
                BatStatView(label: "ÖTTV rank", value: "#45", onHero: true)
                BatStatView(label: "Singles", value: "34:12", onHero: true)
            }
        }
    }
}
