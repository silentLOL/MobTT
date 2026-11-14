import SwiftUI

struct LicensesRoute: View {
    @State private var viewModel = LicensesViewModel()

    var body: some View {
        LicensesScreen(state: viewModel.uiState, onAction: viewModel.onAction)
            .onAppear { viewModel.screenShown() }
            .task { await viewModel.load() }
    }
}

struct LicensesScreen: View {
    let state: LicensesUiState
    let onAction: (LicensesAction) -> Void

    var body: some View {
        Group {
            if state.isLoading {
                ProgressView()
                    .accessibilityLabel(Text("licenses_loading_cd"))
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
            } else if state.groups.isEmpty {
                BatEmptyStateView(
                    icon: .info,
                    title: String(localized: "licenses_empty_title"),
                    message: String(localized: "licenses_empty_body")
                )
                .frame(maxHeight: .infinity)
            } else {
                list
            }
        }
        .batBackground()
    }

    private var list: some View {
        ScrollView {
            LazyVStack(alignment: .leading, spacing: BatSpacing.sm) {
                ForEach(state.groups) { group in
                    BatSectionHeaderView(title: group.kind.title)
                    ForEach(group.entries) { entry in
                        LicenseEntryRowView(entry: entry, isExpanded: entry.id == state.expandedId) {
                            onAction(.entryClicked(id: entry.id))
                        }
                    }
                }
            }
            .padding(.horizontal, BatSpacing.lg)
            .padding(.vertical, BatSpacing.lg)
            .frame(maxWidth: BatSizing.maxContentWidth)
            .frame(maxWidth: .infinity)
        }
    }
}

private extension LicenseKind {
    var title: String {
        switch self {
        case .library: String(localized: "licenses_libraries_title")
        case .font: String(localized: "licenses_fonts_title")
        case .asset: String(localized: "licenses_assets_title")
        }
    }
}

#Preview("Loaded") {
    LicensesScreen(state: LicensesUiState(isLoading: false, groups: licensesSampleGroups), onAction: { _ in })
}

#Preview("One entry open, dark") {
    LicensesScreen(state: LicensesUiState(isLoading: false, groups: licensesSampleGroups, expandedId: "io.ktor:ktor-client-core"), onAction: { _ in })
        .preferredColorScheme(.dark)
}

#Preview("Entry without a text open") {
    LicensesScreen(state: LicensesUiState(isLoading: false, groups: licensesSampleGroups, expandedId: "x:no-text"), onAction: { _ in })
}

#Preview("Empty") {
    LicensesScreen(state: LicensesUiState(isLoading: false), onAction: { _ in })
}

#Preview("Loading") {
    LicensesScreen(state: LicensesUiState(), onAction: { _ in })
}

#Preview("Open entry, 200% text") {
    LicensesScreen(state: LicensesUiState(isLoading: false, groups: licensesSampleGroups, expandedId: "font:inter"), onAction: { _ in })
        .dynamicTypeSize(.accessibility2)
}
