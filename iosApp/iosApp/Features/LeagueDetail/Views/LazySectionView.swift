import SwiftUI

/// A collapsible League section that shows its own loading and failure while open; `content` draws the loaded value.
struct LazySectionView<Value: Equatable, Content: View>: View {
    let title: String
    let isExpanded: Bool
    let onToggle: () -> Void
    let section: SectionUiState<Value>
    let loadingLabel: String
    let unavailableText: String
    @ViewBuilder let content: (Value) -> Content

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            BatCollapsibleSectionHeaderView(title: title, isExpanded: isExpanded) {
                withAnimation(.easeInOut(duration: 0.2)) { onToggle() }
            }
            if isExpanded {
                Group {
                    switch section {
                    case .loading:
                        ProgressView()
                            .tint(BatColor.brand)
                            .accessibilityLabel(Text(loadingLabel))
                            .frame(maxWidth: .infinity)
                            .padding(BatSpacing.md)
                    case .unavailable:
                        Text(unavailableText)
                            .batTextStyle(BatTypography.body)
                            .foregroundStyle(BatColor.textSecondary)
                            .onAppear { AccessibilityNotification.Announcement(unavailableText).post() }
                    case .loaded(let value):
                        VStack(alignment: .leading, spacing: 0) { content(value) }
                    }
                }
                .transition(.opacity)
            }
        }
    }
}

struct SectionMessageView: View {
    let key: LocalizedStringKey

    var body: some View {
        Text(key)
            .batTextStyle(BatTypography.body)
            .foregroundStyle(BatColor.textSecondary)
    }
}

#Preview {
    BatPreviewMatrix {
        VStack(spacing: BatSpacing.md) {
            LazySectionView(title: "Collapsed", isExpanded: false, onToggle: {}, section: SectionUiState<String>.loading, loadingLabel: "Loading", unavailableText: "Unavailable") { Text($0) }
            LazySectionView(title: "Loading", isExpanded: true, onToggle: {}, section: SectionUiState<String>.loading, loadingLabel: "Loading", unavailableText: "Unavailable") { Text($0) }
            LazySectionView(title: "Unavailable", isExpanded: true, onToggle: {}, section: SectionUiState<String>.unavailable, loadingLabel: "Loading", unavailableText: "Can't load right now.") { Text($0) }
            LazySectionView(title: "Loaded", isExpanded: true, onToggle: {}, section: .loaded("Loaded content"), loadingLabel: "Loading", unavailableText: "Unavailable") { Text($0) }
        }
    }
}
