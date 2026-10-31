import SwiftUI

enum BatIcon {
    case logo
    case home
    case club
    case search
    case live
    case code
    case darkMode
    case lightMode
    case settings
    case close
    case check
    case info
    case location
    case calendar
    case refresh
    case arrowForward
    case arrowBack
    case star
    case starFilled
    case chevronDown

    var image: Image {
        switch self {
        case .logo: Image("Logo")
        case .home: Image(systemName: "house")
        case .club: Image(systemName: "shield")
        case .search: Image(systemName: "magnifyingglass")
        case .live: Image(systemName: "bolt")
        case .code: Image(systemName: "chevron.left.forwardslash.chevron.right")
        case .darkMode: Image(systemName: "moon")
        case .lightMode: Image(systemName: "sun.max")
        case .settings: Image(systemName: "gearshape")
        case .close: Image(systemName: "xmark")
        case .check: Image(systemName: "checkmark")
        case .info: Image(systemName: "info.circle")
        case .location: Image(systemName: "mappin.and.ellipse")
        case .calendar: Image(systemName: "calendar")
        case .refresh: Image(systemName: "arrow.clockwise")
        case .arrowForward: Image(systemName: "arrow.right")
        case .arrowBack: Image(systemName: "arrow.left")
        case .star: Image(systemName: "star")
        case .starFilled: Image(systemName: "star.fill")
        case .chevronDown: Image(systemName: "chevron.down")
        }
    }
}
