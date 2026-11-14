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
    case licensesClicked
}

enum SettingsEvent: Equatable {
    case openSupportLink(URL)
    case openLicenses
}
