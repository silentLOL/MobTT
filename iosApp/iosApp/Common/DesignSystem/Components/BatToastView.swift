import SwiftUI

struct BatToastView: View {
    let message: String

    var body: some View {
        HStack(spacing: BatSpacing.sm) {
            BatIconView(icon: .check, tint: BatColor.toastIcon, size: BatSizing.iconSmall)
            Text(message)
                .batTextStyle(BatTypography.captionStrong)
                .foregroundStyle(BatColor.onToast)
        }
        .padding(.horizontal, BatSpacing.lg)
        .padding(.vertical, BatSpacing.md)
        .background(BatColor.toast, in: BatShapes.large)
        .batShadow(.xl)
        .accessibilityElement(children: .combine)
        .onAppear { AccessibilityNotification.Announcement(message).post() }
    }
}

#Preview {
    BatPreviewMatrix {
        BatToastView(message: "Settings saved")
    }
}

#Preview("Long message") {
    BatPreviewMatrix {
        BatToastView(message: "Your favourite players were saved and will be synced the next time you are online")
    }
}
