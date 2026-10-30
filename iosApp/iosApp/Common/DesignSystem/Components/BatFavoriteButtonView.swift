import SwiftUI

/// `accessibilityText` names the action a tap performs; `accessibilityState` names what the star shows now.
struct BatFavoriteButtonView: View {
    let isFavorite: Bool
    let accessibilityText: String
    let accessibilityState: String
    let action: () -> Void

    var body: some View {
        BatIconButtonView(
            icon: isFavorite ? .starFilled : .star,
            accessibilityText: accessibilityText,
            tint: isFavorite ? BatColor.brand : BatColor.textSecondary,
            action: action
        )
        .accessibilityValue(accessibilityState)
    }
}

#Preview {
    BatPreviewMatrix {
        HStack {
            BatFavoriteButtonView(
                isFavorite: false,
                accessibilityText: "Add to Favorites",
                accessibilityState: "Not a Favorite",
                action: {}
            )
            BatFavoriteButtonView(
                isFavorite: true,
                accessibilityText: "Remove from Favorites",
                accessibilityState: "Favorite",
                action: {}
            )
        }
    }
}
