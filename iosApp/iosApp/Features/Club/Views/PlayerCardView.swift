import SwiftUI

/// The whole card opens the Player; the RC values line up in a column.
struct PlayerCardView: View {
    let player: LineupPlayerUi
    let onTap: () -> Void

    var body: some View {
        BatCardView(padding: BatSpacing.md, onTap: onTap) {
            HStack(spacing: BatSpacing.md) {
                Text(player.name)
                    .batTextStyle(BatTypography.bodyStrong)
                    .foregroundStyle(BatColor.textPrimary)
                    .frame(maxWidth: .infinity, alignment: .leading)
                if let rcPoints = player.rcPoints {
                    Text("team_detail_lineup_rc_label \(rcPoints)")
                        .batTextStyle(BatTypography.caption)
                        .monospacedDigit()
                        .foregroundStyle(BatColor.textSecondary)
                        .frame(minWidth: 64, alignment: .trailing)
                }
            }
            .frame(minHeight: BatSizing.minTouchTarget)
        }
    }
}

#Preview {
    BatPreviewMatrix {
        VStack(spacing: BatSpacing.sm) {
            ForEach(ClubPreviewData.overview.players) { PlayerCardView(player: $0, onTap: {}) }
        }
    }
}
