import SafariServices
import SwiftUI

/// A web page in the in-app browser, presented as a sheet.
struct SafariView: UIViewControllerRepresentable {
    let url: URL

    func makeUIViewController(context: Context) -> SFSafariViewController {
        SFSafariViewController(url: url)
    }

    func updateUIViewController(_ controller: SFSafariViewController, context: Context) {}
}

/// A web page to open in `SafariView`, as a sheet's item.
struct WebPage: Identifiable, Equatable {
    let url: URL
    var id: String { url.absoluteString }
}
