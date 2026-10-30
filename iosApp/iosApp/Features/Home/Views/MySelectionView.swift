import SwiftUI

struct MySelectionView: View {
    let selection: HomeSelectionUi
    let onChooseClick: () -> Void
    let onPlayerClick: (String) -> Void
    let onClubClick: (String) -> Void

    var body: some View {
        switch selection {
        case .none:
            ChoosePromptView(onChooseClick: onChooseClick)
        case .unavailable:
            EmptyView()
        case .player(let name, let clubName, let associationName, let playerId):
            HeroView(
                overline: String(localized: "home_my_player_overline"),
                name: name,
                detail: "\(clubName) • \(associationName)",
                kind: String(localized: "home_favorite_kind_player"),
                onClick: { onPlayerClick(playerId) }
            )
        case .club(let name, let associationName, let clubId):
            HeroView(
                overline: String(localized: "home_my_club_overline"),
                name: name,
                detail: associationName,
                kind: String(localized: "home_favorite_kind_club"),
                onClick: { onClubClick(clubId) }
            )
        }
    }
}

private struct HeroView: View {
    let overline: String
    let name: String
    let detail: String
    let kind: String
    let onClick: () -> Void

    var body: some View {
        Button(action: onClick) {
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
            .frame(minHeight: BatSizing.minTouchTarget)
            .contentShape(BatShapes.extraLarge)
        }
        .buttonStyle(.plain)
        .accessibilityElement(children: .ignore)
        .accessibilityLabel(Text("\(kind), \(name), \(detail)"))
        .accessibilityAddTraits(.isButton)
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
            MySelectionView(selection: .none, onChooseClick: {}, onPlayerClick: { _ in }, onClubClick: { _ in })
            MySelectionView(
                selection: .player(name: "Michael Huber", clubName: "TTC Olympic", associationName: "WTTV", playerId: "p1"),
                onChooseClick: {},
                onPlayerClick: { _ in },
                onClubClick: { _ in }
            )
            MySelectionView(
                selection: .club(name: "Nussdorf TTC", associationName: "WTTV", clubId: "c1"),
                onChooseClick: {},
                onPlayerClick: { _ in },
                onClubClick: { _ in }
            )
            MySelectionView(
                selection: .player(
                    name: "Katharina Habsburg-Lothringen-Esterházy",
                    clubName: "Union Tischtennisclub St. Pölten Nord-West",
                    associationName: "Niederösterreichischer Tischtennisverband (NÖTTV)",
                    playerId: "p2"
                ),
                onChooseClick: {},
                onPlayerClick: { _ in },
                onClubClick: { _ in }
            )
        }
    }
}
