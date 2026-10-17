import SwiftUI

// Not tappable yet: the Player profile does not exist, so there is nothing to open.
struct LineupRowView: View {
    let player: LineupPlayerUi

    var body: some View {
        Text(player.name)
            .batTextStyle(BatTypography.body)
            .foregroundStyle(BatColor.textPrimary)
            .frame(maxWidth: .infinity, alignment: .leading)
            .padding(.horizontal, BatSpacing.md)
            .padding(.vertical, BatSpacing.sm)
    }
}

#Preview {
    BatPreviewMatrix {
        VStack(spacing: 0) {
            ForEach(TeamDetailPreviewData.loaded.lineup) { LineupRowView(player: $0) }
            ForEach(TeamDetailPreviewData.longNames.lineup) { LineupRowView(player: $0) }
        }
    }
}
