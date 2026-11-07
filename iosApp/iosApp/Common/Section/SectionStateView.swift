import SwiftUI

/// A section's own loading and failure; `content` draws the loaded value. With `onRetry`, an unavailable section offers a retry that reloads only this section.
struct SectionStateView<Value: Equatable, Content: View>: View {
    let section: SectionUiState<Value>
    let loadingLabel: String
    let unavailableText: String
    var onRetry: (() -> Void)?
    @ViewBuilder let content: (Value) -> Content

    var body: some View {
        switch section {
        case .loading:
            ProgressView()
                .tint(BatColor.brand)
                .accessibilityLabel(Text(loadingLabel))
                .frame(maxWidth: .infinity)
                .padding(BatSpacing.md)
        case .unavailable:
            VStack(alignment: .leading, spacing: BatSpacing.sm) {
                Text(unavailableText)
                    .batTextStyle(BatTypography.body)
                    .foregroundStyle(BatColor.textSecondary)
                    .onAppear { AccessibilityNotification.Announcement(unavailableText).post() }
                if let onRetry {
                    BatSecondaryButtonView(title: String(localized: "core_section_retry_button"), action: onRetry)
                }
            }
        case .loaded(let value):
            VStack(alignment: .leading, spacing: 0) { content(value) }
        }
    }
}

#Preview {
    BatPreviewMatrix {
        VStack(spacing: BatSpacing.md) {
            SectionStateView(section: SectionUiState<String>.loading, loadingLabel: "Loading", unavailableText: "Unavailable") { Text($0) }
            SectionStateView(section: SectionUiState<String>.unavailable, loadingLabel: "Loading", unavailableText: "Can't load right now.") { Text($0) }
            SectionStateView(section: SectionUiState<String>.unavailable, loadingLabel: "Loading", unavailableText: "Can't load right now.", onRetry: {}) { Text($0) }
            SectionStateView(section: .loaded("Loaded content"), loadingLabel: "Loading", unavailableText: "Unavailable") { Text($0) }
        }
    }
}
