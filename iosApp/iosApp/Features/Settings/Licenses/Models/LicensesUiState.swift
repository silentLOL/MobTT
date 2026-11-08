import Foundation

/// `groups` is empty once loaded when the list is empty or could not be read. `expandedId` is the entry showing its text.
struct LicensesUiState: Equatable {
    var isLoading = true
    var groups: [LicenseGroupUi] = []
    var expandedId: String?
}

enum LicensesAction {
    case entryClicked(id: String)
}
