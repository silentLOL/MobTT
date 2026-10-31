enum PlayersPreviewData {
    static let associations = [
        DirectoryEntryUi(id: "191", title: "Oberösterreichischer Tischtennisverband", subtitle: nil, selected: true),
        DirectoryEntryUi(id: "196", title: "Wiener Tischtennis-Verband", subtitle: nil, selected: false),
    ]

    static let players = [
        PlayerRowUi(id: "p1", name: "Michael Huber", rcPoints: 1850),
        PlayerRowUi(id: "p2", name: "Katharina Habsburg-Lothringen-Esterházy", rcPoints: nil),
    ]

    static let chosen = PlayersUiState(associationName: "Oberösterreichischer Tischtennisverband", clubName: "LINZ AG-FROSCHBERG", list: .players(players))
}
