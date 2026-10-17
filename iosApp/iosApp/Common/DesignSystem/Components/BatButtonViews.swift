import SwiftUI

private struct BatButtonStyle: ButtonStyle {
    let container: Color
    let pressedContainer: Color
    let content: Color

    @Environment(\.isEnabled) private var isEnabled

    func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .batTextStyle(BatTypography.bodyStrong)
            .foregroundStyle(isEnabled ? content : BatColor.textTertiary)
            .padding(.horizontal, BatSpacing.lg)
            .padding(.vertical, BatSpacing.md)
            .frame(minHeight: BatSizing.minTouchTarget)
            .background(
                isEnabled ? (configuration.isPressed ? pressedContainer : container) : BatColor.surfaceMuted,
                in: BatShapes.medium
            )
            .contentShape(BatShapes.medium)
    }
}

struct BatPrimaryButtonView: View {
    let title: String
    var isEnabled: Bool = true
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Text(title).frame(maxWidth: .infinity)
        }
        .buttonStyle(BatButtonStyle(container: BatColor.brand, pressedContainer: BatColor.brandPressed, content: BatColor.onBrand))
        .disabled(!isEnabled)
        .batShadow(.lg, tint: isEnabled ? BatColor.brand : .clear)
    }
}

struct BatSecondaryButtonView: View {
    let title: String
    var icon: BatIcon?
    var isEnabled: Bool = true
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            HStack(spacing: BatSpacing.sm) {
                if let icon {
                    BatIconView(icon: icon, size: BatSizing.iconSmall)
                }
                Text(title)
            }
            .frame(maxWidth: .infinity)
        }
        .buttonStyle(BatButtonStyle(container: BatColor.surfaceMuted, pressedContainer: BatColor.border, content: BatColor.textPrimary))
        .disabled(!isEnabled)
    }
}

#Preview {
    BatPreviewMatrix {
        VStack(spacing: BatSpacing.md) {
            BatPrimaryButtonView(title: "Save settings") {}
            BatPrimaryButtonView(title: "Disabled", isEnabled: false) {}
            BatSecondaryButtonView(title: "Test connection") {}
            BatSecondaryButtonView(title: "Sync", icon: .refresh) {}
        }
    }
}
