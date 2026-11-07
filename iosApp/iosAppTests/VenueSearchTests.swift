import Foundation
import Testing

@testable import MobTT

struct VenueSearchTests {
    @Test func namesTheVenueThenEveryAddressPart() {
        #expect(detail(venue: "Sporthalle Hagenberg", address: "Softwarepark 101, 4232 Hagenberg").venueSearchText == "Sporthalle Hagenberg, Softwarepark 101, 4232 Hagenberg")
    }

    @Test func aNameAloneIsSearchedForOnItsOwn() {
        #expect(detail(venue: "Sporthalle Olympic", address: nil).venueSearchText == "Sporthalle Olympic")
    }

    @Test func aCityAloneFollowsTheName() {
        #expect(detail(venue: "Sporthalle Olympic", address: "Wien").venueSearchText == "Sporthalle Olympic, Wien")
    }

    @Test func anAddressWithoutANameIsSearchedForOnItsOwn() {
        #expect(detail(venue: nil, address: "Softwarepark 101, 4232 Hagenberg").venueSearchText == "Softwarepark 101, 4232 Hagenberg")
    }

    @Test func withoutANameOrAddressThereIsNothingToSearchFor() {
        #expect(detail(venue: nil, address: nil).venueSearchText == nil)
    }

    @Test func appleMapsSearchesForTheTextEncoded() {
        let url = AppleMaps.search("Sporthalle Olympic, Wien & Umgebung")

        #expect(url.absoluteString == "https://maps.apple.com/?q=Sporthalle%20Olympic,%20Wien%20%26%20Umgebung")
    }

    @Test func aPlusInTheVenueStaysAPlus() {
        #expect(AppleMaps.search("Halle 1+2").absoluteString == "https://maps.apple.com/?q=Halle%201%2B2")
    }

    private func detail(venue: String?, address: String?) -> FixtureDetailUi {
        FixtureDetailUi(
            homeTeamId: "t1",
            homeTeamName: "Hagenberg 1",
            awayTeamId: "t2",
            awayTeamName: "Olympic 1",
            startsAt: Date(timeIntervalSince1970: 1_800_000_000),
            venue: venue,
            address: address,
            score: nil,
            matches: []
        )
    }
}
