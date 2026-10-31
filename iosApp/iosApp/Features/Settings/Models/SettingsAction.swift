enum SettingsAction {
    case choosePlayerClicked
    case chooseClubClicked
    case pickerQueryChanged(String)
    case pickerEntryClicked(id: String)
    case pickerBackClicked
    case usageConsentToggled(Bool)
    case doneClicked
}

enum SettingsEvent {
    case dismiss
}
