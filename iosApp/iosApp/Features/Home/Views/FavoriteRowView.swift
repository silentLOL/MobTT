import SwiftUI

struct FavoriteRowView: View {
    let favorite: HomeFavoriteUi
    var onClick: (() -> Void)?
    let onRemoveClick: () -> Void

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
                BatIconButtonView(
                    icon: .close,
                    accessibilityText: String(localized: "home_favorite_remove_cd \(favorite.name)"),
                    action: onRemoveClick
                )
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
                favorite: HomeFavoriteUi(kind: .player, entityId: "p", name: "Anna Gruber", detail: "TTC Olympic"),
                onRemoveClick: {}
            )
            FavoriteRowView(
                favorite: HomeFavoriteUi(kind: .club, entityId: "c", name: "Nussdorf TTC", detail: "WTTV"),
                onClick: {},
                onRemoveClick: {}
            )
            FavoriteRowView(
                favorite: HomeFavoriteUi(
                    kind: .player,
                    entityId: "l",
                    name: "Katharina Habsburg-Lothringen-Esterházy",
                    detail: "Union Tischtennisclub St. Pölten Nord-West"
                ),
                onRemoveClick: {}
            )
        }
    }
}
