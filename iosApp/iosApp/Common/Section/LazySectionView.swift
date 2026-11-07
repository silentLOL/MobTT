import SwiftUI

/// A collapsible section that shows its own loading and failure while open; `content` draws the loaded value. A `headerAction`, such as an info button, sits in the header.
struct LazySectionView<Value: Equatable, Content: View, HeaderAction: View>: View {
    let title: String
    let isExpanded: Bool
    let onToggle: () -> Void
    let section: SectionUiState<Value>
    let loadingLabel: String
    let unavailableText: String
    var onRetry: (() -> Void)?
    var headerAction: () -> HeaderAction
    @ViewBuilder let content: (Value) -> Content

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            BatCollapsibleSectionHeaderView(title: title, isExpanded: isExpanded, onToggle: {
                withAnimation(.easeInOut(duration: 0.2)) { onToggle() }
            }, action: headerAction)
            if isExpanded {
                SectionStateView(section: section, loadingLabel: loadingLabel, unavailableText: unavailableText, onRetry: onRetry, content: content)
                    .transition(.opacity)
            }
        }
    }
}

extension LazySectionView where HeaderAction == EmptyView {
    init(
        title: String,
        isExpanded: Bool,
        onToggle: @escaping () -> Void,
        section: SectionUiState<Value>,
        loadingLabel: String,
        unavailableText: String,
        onRetry: (() -> Void)? = nil,
        @ViewBuilder content: @escaping (Value) -> Content
    ) {
        self.init(
            title: title,
            isExpanded: isExpanded,
            onToggle: onToggle,
            section: section,
            loadingLabel: loadingLabel,
            unavailableText: unavailableText,
            onRetry: onRetry,
            headerAction: { EmptyView() },
            content: content
        )
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
