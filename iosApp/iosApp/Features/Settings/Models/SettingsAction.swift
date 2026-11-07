import Foundation

enum SettingsAction {
    case choosePlayerClicked
    case chooseClubClicked
    case pickerQueryChanged(String)
    case pickerEntryClicked(id: String)
    case pickerBackClicked
    case appearanceSelected(AppearanceChoice)
    case usageConsentToggled(Bool)
    case supportClicked
}

enum SettingsEvent: Equatable {
    case openSupportLink(URL)
}
