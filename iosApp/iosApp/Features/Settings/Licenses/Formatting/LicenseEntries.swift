import Foundation

/// Reads `Resources/licenses.json`, which `scripts/generate-ios-licenses.py` writes (docs/LICENSES.md).
private struct LicensesFile: Decodable {
    struct SharedLicense: Decodable {
        let text: String?
    }

    struct Entry: Decodable {
        let id: String
        let name: String
        let version: String?
        let authors: String?
        let kind: LicenseKind
        let licenseName: String
        let licenseUrl: String?
        let licenseKey: String?
        let text: String?
    }

    let licenses: [String: SharedLicense]
    let entries: [Entry]
}

/// Libraries first, then fonts, then assets; each group sorted by name. An entry's own text wins over its license's.
nonisolated func parseLicenseEntries(_ data: Data) throws -> [LicenseEntry] {
    let file = try JSONDecoder().decode(LicensesFile.self, from: data)
    return file.entries
        .map { entry in
            let shared = entry.licenseKey.flatMap { file.licenses[$0]?.text }
            return LicenseEntry(
                id: entry.id,
                name: entry.name,
                version: entry.version.nonEmpty,
                authors: entry.authors.nonEmpty,
                licenseName: entry.licenseName,
                licenseText: (entry.text ?? shared).nonEmpty,
                licenseUrl: entry.licenseUrl,
                kind: entry.kind
            )
        }
        .sorted { ($0.kind, $0.name.lowercased(), $0.id) < ($1.kind, $1.name.lowercased(), $1.id) }
}

private extension String? {
    var nonEmpty: String? { self.flatMap { $0.isEmpty ? nil : $0 } }
}
