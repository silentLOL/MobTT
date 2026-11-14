import Foundation

/// The order the groups appear in on the screen.
enum LicenseKind: Int, Comparable, Equatable, Decodable {
    case library
    case font
    case asset

    init(from decoder: any Decoder) throws {
        switch try decoder.singleValueContainer().decode(String.self) {
        case "library": self = .library
        case "font": self = .font
        case "asset": self = .asset
        case let other:
            throw DecodingError.dataCorrupted(.init(codingPath: decoder.codingPath, debugDescription: "Unknown kind \(other)"))
        }
    }

    static func < (lhs: LicenseKind, rhs: LicenseKind) -> Bool { lhs.rawValue < rhs.rawValue }
}

/// `licenseText` is nil when the license ships no text; `licenseName` and `licenseUrl` still say which license it is.
struct LicenseEntry: Identifiable, Equatable {
    let id: String
    let name: String
    let version: String?
    let authors: String?
    let licenseName: String
    let licenseText: String?
    let licenseUrl: String?
    let kind: LicenseKind
}

struct LicenseGroupUi: Identifiable, Equatable {
    let kind: LicenseKind
    let entries: [LicenseEntry]

    var id: LicenseKind { kind }
}
