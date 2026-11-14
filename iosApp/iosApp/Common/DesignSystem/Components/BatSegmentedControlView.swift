import SwiftUI

/// One choice out of a few `options`, shown side by side; `selectedIndex` is the chosen one.
struct BatSegmentedControlView: View {
    let options: [String]
    let selectedIndex: Int
    let onSelect: (Int) -> Void

    var body: some View {
        Picker(selection: Binding(get: { selectedIndex }, set: onSelect)) {
            ForEach(Array(options.enumerated()), id: \.offset) { index, option in
                Text(option).tag(index)
            }
        } label: {
            EmptyView()
        }
        .pickerStyle(.segmented)
        .tint(BatColor.brand)
    }
}

#Preview {
    BatPreviewMatrix {
        BatSegmentedControlView(options: ["12 months", "All"], selectedIndex: 0, onSelect: { _ in })
    }
}
