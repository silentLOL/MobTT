import Foundation

enum PlayerDetailPreviewData {
    static let loaded = PlayerDetailUi(
        playerName: "Anna Gruber",
        clubId: "olympic",
        clubName: "TTC Olympic",
        associationName: "WTTV",
        teams: [PlayerTeamUi(id: "olympic-1", name: "TTC Olympic 1"), PlayerTeamUi(id: "olympic-3", name: "TTC Olympic 3")],
        rcPoints: 1992,
        ageGroup: "U171"
    )

    static var favorite: PlayerDetailUi {
        var detail = loaded
        detail.isFavorite = true
        return detail
    }

    static var noTeams: PlayerDetailUi {
        var detail = loaded
        detail.teams = []
        return detail
    }

    static let noClub = PlayerDetailUi(playerName: "Anna Gruber", clubId: "olympic", clubName: nil, associationName: nil)

    static let longNames = PlayerDetailUi(
        playerName: "Katharina Habsburg-Lothringen-Esterházy",
        clubId: "stpoelten",
        clubName: "Union Tischtennisclub St. Pölten Nord-West",
        associationName: "NÖTTV",
        teams: [PlayerTeamUi(id: "stpoelten-1", name: "Union Tischtennisclub St. Pölten Nord-West 1")]
    )
}
