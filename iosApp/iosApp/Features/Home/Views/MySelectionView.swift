import SwiftUI

struct MySelectionView: View {
    let selection: HomeSelectionUi
    let onChooseClick: () -> Void

    var body: some View {
        switch selection {
        case .none:
            ChoosePromptView(onChooseClick: onChooseClick)
        case .player(let name, let clubName, let associationName):
            HeroView(
                overline: String(localized: "home_my_player_overline"),
                name: name,
                detail: "\(clubName) • \(associationName)"
            )
        case .club(let name, let associationName):
            HeroView(overline: String(localized: "home_my_club_overline"), name: name, detail: associationName)
        }
    }
}

private struct HeroView: View {
    let overline: String
    let name: String
    let detail: String

    var body: some View {
        BatHeroCardView {
            VStack(alignment: .leading, spacing: BatSpacing.sm) {
                BatHeroPillView(text: overline)
                Text(name)
                    .batTextStyle(BatTypography.display)
                    .foregroundStyle(BatColor.onHero)
                Text(detail)
                    .batTextStyle(BatTypography.body)
                    .foregroundStyle(BatColor.onHero)
            }
        }
        .accessibilityElement(children: .combine)
    }
}

private struct ChoosePromptView: View {
    let onChooseClick: () -> Void

    var body: some View {
        BatCardView {
            VStack(alignment: .leading, spacing: BatSpacing.md) {
                Text("home_choose_title")
                    .batTextStyle(BatTypography.title)
                    .foregroundStyle(BatColor.textPrimary)
                    .accessibilityAddTraits(.isHeader)
                Text("home_choose_body")
                    .batTextStyle(BatTypography.body)
                    .foregroundStyle(BatColor.textSecondary)
                BatPrimaryButtonView(title: String(localized: "home_choose_button"), action: onChooseClick)
            }
        }
    }
}

#Preview {
    BatPreviewMatrix {
        VStack(spacing: BatSpacing.lg) {
            MySelectionView(selection: .none, onChooseClick: {})
            MySelectionView(
                selection: .player(name: "Michael Huber", clubName: "TTC Olympic", associationName: "WTTV"),
                onChooseClick: {}
            )
            MySelectionView(selection: .club(name: "Nussdorf TTC", associationName: "WTTV"), onChooseClick: {})
            MySelectionView(
                selection: .player(
                    name: "Katharina Habsburg-Lothringen-Esterházy",
                    clubName: "Union Tischtennisclub St. Pölten Nord-West",
                    associationName: "Niederösterreichischer Tischtennisverband (NÖTTV)"
                ),
                onChooseClick: {}
            )
        }
    }
}
