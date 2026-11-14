import Foundation

extension FixtureDetailUi {
    /// The Venue's name, then its address, leaving out what XTTV does not give. Empty when it gives neither.
    var venueLines: [String] { [venue, address].compactMap { $0 } }

    /// What a maps app searches for, e.g. "Sporthalle Hagenberg, Softwarepark 101, 4232 Hagenberg". Nil without a Venue.
    var venueSearchText: String? { venueLines.isEmpty ? nil : venueLines.joined(separator: ", ") }
}

enum AppleMaps {
    /// Opens Apple Maps searching for `text`, or Apple Maps on the web when the app is deleted.
    static func search(_ text: String) -> URL {
        var components = URLComponents(string: "https://maps.apple.com/")!
        components.queryItems = [URLQueryItem(name: "q", value: text)]
        // URLQueryItem leaves "+" as is, which Apple Maps reads as a space.
        components.percentEncodedQuery = components.percentEncodedQuery?.replacingOccurrences(of: "+", with: "%2B")
        return components.url!
    }
}
