import Foundation

enum PlayerDrillDownStep: Equatable {
    case association
    case league
    case player
}

/// [entries] are already filtered by [query]. [contextName] is the chosen Verband (League step) or League (Player step).
struct PlayerDrillDownUi: Equatable {
    let step: PlayerDrillDownStep
    let contextName: String?
    let query: String
    let entries: [DirectoryEntryUi]
}
