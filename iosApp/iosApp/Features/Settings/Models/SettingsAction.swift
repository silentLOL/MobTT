enum SettingsAction {
    case choosePlayerClicked
    case chooseClubClicked
    case pickerQueryChanged(String)
    case pickerEntryClicked(id: String)
    case pickerBackClicked
    case appearanceSelected(AppearanceChoice)
    case usageConsentToggled(Bool)
    case doneClicked
}

enum SettingsEvent {
    case dismiss
}
