import SwiftUI

struct FavoriteRowView: View {
    let favorite: HomeFavoriteUi
    var onClick: (() -> Void)?

    var body: some View {
        BatCardView(padding: BatSpacing.md) {
            HStack(spacing: BatSpacing.md) {
                BatInitialsAvatarView(initials: initialsOf(favorite.name), tint: favorite.kind == .club ? .live : .brand)
                if let onClick {
                    Button(action: onClick) { labels }
                        .buttonStyle(.plain)
                } else {
                    labels
                }
            }
        }
    }
}

private extension FavoriteRowView {
    var labels: some View {
        VStack(alignment: .leading, spacing: 0) {
            Text(favorite.name)
                .batTextStyle(BatTypography.bodyStrong)
                .foregroundStyle(BatColor.textPrimary)
            if let detail = favorite.detail {
                Text(detail)
                    .batTextStyle(BatTypography.caption)
                    .foregroundStyle(BatColor.textSecondary)
            }
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .frame(minHeight: BatSizing.minTouchTarget)
        .contentShape(Rectangle())
    }
}

#Preview {
    BatPreviewMatrix {
        VStack(spacing: BatSpacing.sm) {
            FavoriteRowView(
                favorite: HomeFavoriteUi(kind: .player, entityId: "p", name: "Anna Gruber", detail: "TTC Olympic")
            )
            FavoriteRowView(
                favorite: HomeFavoriteUi(kind: .club, entityId: "c", name: "Nussdorf TTC", detail: "WTTV"),
                onClick: {}
            )
            FavoriteRowView(
                favorite: HomeFavoriteUi(kind: .team, entityId: "t", name: "TTC Olympic 1", detail: "TTC Olympic"),
                onClick: {}
            )
            FavoriteRowView(
                favorite: HomeFavoriteUi(
                    kind: .team,
                    entityId: "lt",
                    name: "Union Tischtennisclub St. Pölten Nord-West 1",
                    detail: "Union Tischtennisclub St. Pölten Nord-West"
                ),
                onClick: {}
            )
            FavoriteRowView(
                favorite: HomeFavoriteUi(
                    kind: .player,
                    entityId: "l",
                    name: "Katharina Habsburg-Lothringen-Esterházy",
                    detail: "Union Tischtennisclub St. Pölten Nord-West"
                )
            )
        }
    }
}
