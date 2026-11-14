import Foundation
import Testing

@testable import MobTT

struct LicenseEntriesTests {
    private func json(licenses: String = "{}", entries: String) -> Data {
        Data(#"{"licenses": \#(licenses), "entries": \#(entries)}"#.utf8)
    }

    private let apache = #"{"Apache-2.0": {"name": "Apache License 2.0", "url": "https://spdx.org/licenses/Apache-2.0.html", "text": "Apache text"}}"#

    @Test func librariesFontsAndAssetsComeInThatOrderEachSortedByName() throws {
        let entries = try parseLicenseEntries(json(entries: """
            [
              {"id": "asset:logo", "name": "Logo", "kind": "asset", "licenseName": "CC0"},
              {"id": "font:inter", "name": "Inter", "kind": "font", "licenseName": "OFL"},
              {"id": "x:zeta", "name": "Zeta", "kind": "library", "licenseName": "MIT"},
              {"id": "x:alpha", "name": "alpha", "kind": "library", "licenseName": "MIT"},
              {"id": "x:Beta", "name": "Beta", "kind": "library", "licenseName": "MIT"}
            ]
            """))

        #expect(entries.map(\.name) == ["alpha", "Beta", "Zeta", "Inter", "Logo"])
        #expect(entries.map(\.kind) == [.library, .library, .library, .font, .asset])
    }

    @Test func anEntryTakesTheSharedTextOfItsLicense() throws {
        let entries = try parseLicenseEntries(json(licenses: apache, entries: """
            [{"id": "x:a", "name": "A", "version": "1.2", "authors": "Jane", "kind": "library",
              "licenseName": "Apache License 2.0", "licenseUrl": "https://spdx.org/licenses/Apache-2.0.html",
              "licenseKey": "Apache-2.0"}]
            """))

        let entry = try #require(entries.first)
        #expect(entry.licenseText == "Apache text")
        #expect(entry.version == "1.2")
        #expect(entry.authors == "Jane")
        #expect(entry.licenseName == "Apache License 2.0")
    }

    @Test func anEntryOwnTextWinsOverTheSharedOne() throws {
        let entries = try parseLicenseEntries(json(licenses: apache, entries: """
            [{"id": "x:a", "name": "A", "kind": "library", "licenseName": "Apache License 2.0",
              "licenseKey": "Apache-2.0", "text": "Copyright Jane. Apache text"}]
            """))

        #expect(entries.first?.licenseText == "Copyright Jane. Apache text")
    }

    @Test func aMissingLicenseTextStaysNilWithTheLicenseNameAndUrl() throws {
        let entries = try parseLicenseEntries(json(entries: """
            [{"id": "x:a", "name": "A", "kind": "library", "licenseName": "Android SDK License",
              "licenseUrl": "https://developer.android.com/studio/terms.html"}]
            """))

        let entry = try #require(entries.first)
        #expect(entry.licenseText == nil)
        #expect(entry.licenseName == "Android SDK License")
        #expect(entry.licenseUrl == "https://developer.android.com/studio/terms.html")
    }

    @Test func anEmptyListGivesNoEntries() throws {
        #expect(try parseLicenseEntries(json(entries: "[]")).isEmpty)
    }

    @Test func aListThatIsNotJsonThrows() {
        #expect(throws: (any Error).self) { try parseLicenseEntries(Data("not json".utf8)) }
    }

    @Test func theBundledListReadsAndCoversSwiftPackagesKotlinLibrariesAndInter() async throws {
        let entries = try parseLicenseEntries(try await LicensesViewModel.bundledList())

        #expect(entries.contains { $0.id == "font:inter" && $0.licenseText?.isEmpty == false })
        #expect(entries.contains { $0.id == "swift:firebase-ios-sdk" })
        #expect(entries.contains { $0.id.hasPrefix("io.ktor:") })
        #expect(entries.allSatisfy { $0.licenseText?.isEmpty == false || $0.licenseUrl != nil })
    }
}
