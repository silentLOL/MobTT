enum PickerKind {
    case player
    case club
}

enum DerivedFrom {
    case none
    case player
    case club
}

/// [playerDrillDown] is set for the Player picker, which is browsed Verband, League, Player instead of searched.
struct SettingsPickerUi: Equatable {
    let kind: PickerKind
    let query: String
    let entries: [DirectoryEntryUi]
    var playerDrillDown: PlayerDrillDownUi?
}

/// `myPlayerName` and `myClubName` are the user's choice; the Club and Association are derived and never edited.
struct SettingsUiState: Equatable {
    var myPlayerName: String?
    var myClubName: String?
    var derivedFrom: DerivedFrom = .none
    var derivedClubName: String?
    var derivedAssociationName: String?
    var picker: SettingsPickerUi?
}
