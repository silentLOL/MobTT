import SwiftUI

enum ShellTab: Hashable, CaseIterable {
    case home
    case club
    case players

    var icon: BatIcon {
        switch self {
        case .home: .home
        case .club: .club
        case .players: .search
        }
    }

    var label: LocalizedStringResource {
        switch self {
        case .home: "core_tab_home_label"
        case .club: "core_tab_club_label"
        case .players: "core_tab_players_label"
        }
    }
}
