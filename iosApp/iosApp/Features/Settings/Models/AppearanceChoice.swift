import SwiftUI

enum AppearanceChoice: String, CaseIterable {
    case system
    case light
    case dark

    static let storageKey = "appearance"

    var label: LocalizedStringResource {
        switch self {
        case .system: "settings_appearance_system_option"
        case .light: "settings_appearance_light_option"
        case .dark: "settings_appearance_dark_option"
        }
    }

    var colorScheme: ColorScheme? {
        switch self {
        case .system: nil
        case .light: .light
        case .dark: .dark
        }
    }
}
