enum SettingsAction {
    case choosePlayerClicked
    case chooseClubClicked
    case pickerQueryChanged(String)
    case pickerEntryClicked(id: String)
    case pickerBackClicked
    case doneClicked
}

enum SettingsEvent {
    case dismiss
}
