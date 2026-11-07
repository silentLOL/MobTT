import SwiftUI
import UIKit

/// The system share sheet for a text and an optional picture, presented as a sheet. Mail and other apps that take a subject get `subject`.
struct ShareSheetView: UIViewControllerRepresentable {
    let content: ShareContent

    func makeUIViewController(context: Context) -> UIActivityViewController {
        let items: [Any] = (content.image.map { [$0] } ?? []) + [ShareTextItem(content: content)]
        return UIActivityViewController(activityItems: items, applicationActivities: nil)
    }

    func updateUIViewController(_ controller: UIActivityViewController, context: Context) {}
}

/// A text to share, with the subject for apps that use one and an optional picture, as a sheet's item.
struct ShareContent: Identifiable, Equatable {
    let subject: String
    let text: String
    var image: UIImage?
    var id: String { subject + "\n" + text }
}

private final class ShareTextItem: NSObject, UIActivityItemSource {
    private let content: ShareContent

    init(content: ShareContent) {
        self.content = content
    }

    func activityViewControllerPlaceholderItem(_ controller: UIActivityViewController) -> Any {
        content.text
    }

    func activityViewController(_ controller: UIActivityViewController, itemForActivityType activityType: UIActivity.ActivityType?) -> Any? {
        content.text
    }

    func activityViewController(_ controller: UIActivityViewController, subjectForActivityType activityType: UIActivity.ActivityType?) -> String {
        content.subject
    }
}

#Preview {
    ShareSheetView(content: ShareContent(subject: "Hagenberg 3 – Freistadt 6", text: "Hagenberg 3 – Freistadt 6  6 : 4"))
}
