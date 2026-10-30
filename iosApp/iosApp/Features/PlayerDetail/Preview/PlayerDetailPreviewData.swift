import Foundation

enum PlayerDetailPreviewData {
    static let loaded = PlayerDetailUi(playerName: "Anna Gruber", clubName: "TTC Olympic", associationName: "Wiener Tischtennisverband (WTTV)")

    static var favorite: PlayerDetailUi {
        var detail = loaded
        detail.isFavorite = true
        return detail
    }

    static let noClub = PlayerDetailUi(playerName: "Anna Gruber", clubName: nil, associationName: nil)

    static let longNames = PlayerDetailUi(
        playerName: "Katharina Habsburg-Lothringen-Esterházy",
        clubName: "Union Tischtennisclub St. Pölten Nord-West",
        associationName: "Niederösterreichischer Tischtennisverband (NÖTTV)"
    )
}
