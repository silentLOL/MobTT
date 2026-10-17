import Foundation

struct DirectoryEntryUi: Identifiable, Equatable {
    let id: String
    let title: String
    let subtitle: String?
    let selected: Bool
}

struct DirectorySectionUi: Equatable {
    let title: String?
    let entries: [DirectoryEntryUi]
}
