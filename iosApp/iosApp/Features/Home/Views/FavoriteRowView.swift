import SwiftUI

struct FavoriteRowView: View {
    let favorite: HomeFavoriteUi
    let onRemoveClick: () -> Void

    var body: some View {
        BatCardView(padding: BatSpacing.md) {
            HStack(spacing: BatSpacing.md) {
                BatInitialsAvatarView(initials: initialsOf(favorite.name), tint: favorite.kind == .club ? .live : .brand)
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
                BatIconButtonView(
                    icon: .close,
                    accessibilityText: String(localized: "home_favorite_remove_cd \(favorite.name)"),
                    action: onRemoveClick
                )
            }
        }
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
