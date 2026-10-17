import SwiftUI

/// Content is capped at `BatSizing.maxContentWidth` and centred on wide screens, while both bars span the full width.
struct BatScreenScaffoldView<TopBar: View, BottomBar: View, Content: View>: View {
    @ViewBuilder let topBar: () -> TopBar
    @ViewBuilder let bottomBar: () -> BottomBar
    @ViewBuilder let content: () -> Content

    var body: some View {
        VStack(spacing: 0) {
            topBar()
            content()
                .frame(maxWidth: BatSizing.maxContentWidth)
                .frame(maxWidth: .infinity, maxHeight: .infinity)
            bottomBar()
        }
        .batBackground()
    }
}

#Preview {
    BatScreenScaffoldView {
        BatTopBarView(title: "Bat Mate")
    } bottomBar: {
        BatTabBarView {
            BatTabBarItemView(icon: .home, label: "Home", isSelected: true) {}
            BatTabBarItemView(icon: .live, label: "Live", isSelected: false) {}
        }
    } content: {
        BatEmptyStateView(icon: .home, title: "Welcome to Bat Mate", message: "Your results will show up here.")
    }
}

#Preview("Dark") {
    BatScreenScaffoldView {
        BatTopBarView(title: "Bat Mate")
    } bottomBar: {
        BatTabBarView {
            BatTabBarItemView(icon: .home, label: "Home", isSelected: true) {}
        }
    } content: {
        BatEmptyStateView(icon: .home, title: "Welcome to Bat Mate", message: "Your results will show up here.")
    }
    .preferredColorScheme(.dark)
}
