import Foundation

enum PlayerDetailPreviewData {
    static let loaded = PlayerDetailUi(
        playerName: "Anna Gruber",
        clubId: "olympic",
        club: .loaded(PlayerClubUi(clubName: "TTC Olympic", associationName: "WTTV")),
        teams: .loaded(PlayerTeamsUi(
            teams: [PlayerTeamUi(id: "olympic-1", name: "TTC Olympic 1"), PlayerTeamUi(id: "olympic-3", name: "TTC Olympic 3")],
            rcPoints: 1992,
            ageGroup: "U171"
        ))
    )

    static var favorite: PlayerDetailUi {
        var detail = loaded
        detail.isFavorite = true
        return detail
    }

    static let sectionsLoading = PlayerDetailUi(playerName: "Anna Gruber", clubId: "olympic")

    static let sectionsUnavailable = PlayerDetailUi(playerName: "Anna Gruber", clubId: "olympic", club: .unavailable, teams: .unavailable)

    static var noTeams: PlayerDetailUi {
        var detail = loaded
        detail.teams = .loaded(PlayerTeamsUi())
        return detail
    }

    static let noClub = PlayerDetailUi(playerName: "Anna Gruber", clubId: "olympic", club: .loaded(nil), teams: .loaded(PlayerTeamsUi()))

    static let longNames = PlayerDetailUi(
        playerName: "Katharina Habsburg-Lothringen-Esterházy",
        clubId: "stpoelten",
        club: .loaded(PlayerClubUi(clubName: "Union Tischtennisclub St. Pölten Nord-West", associationName: "NÖTTV")),
        teams: .loaded(PlayerTeamsUi(teams: [PlayerTeamUi(id: "stpoelten-1", name: "Union Tischtennisclub St. Pölten Nord-West 1")]))
    )
}
