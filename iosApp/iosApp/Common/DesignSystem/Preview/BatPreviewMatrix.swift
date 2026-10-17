import SwiftUI

/// Renders `content` in light, dark and 200% text so one preview checks contrast and wrapping together.
struct BatPreviewMatrix<Content: View>: View {
    @ViewBuilder let content: () -> Content

    var body: some View {
        ScrollView {
            VStack(spacing: 0) {
                variant(scheme: .light, size: .large)
                variant(scheme: .dark, size: .large)
                variant(scheme: .light, size: .accessibility2)
            }
        }
    }

    private func variant(scheme: ColorScheme, size: DynamicTypeSize) -> some View {
        content()
            .padding(BatSpacing.lg)
            .frame(maxWidth: .infinity, alignment: .leading)
            .batBackground()
            .environment(\.colorScheme, scheme)
            .dynamicTypeSize(size)
    }
}
