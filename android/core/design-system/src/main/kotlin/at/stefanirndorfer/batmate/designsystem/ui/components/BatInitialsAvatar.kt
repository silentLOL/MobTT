package at.stefanirndorfer.batmate.designsystem.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import at.stefanirndorfer.batmate.designsystem.preview.BatPreviewSurface
import at.stefanirndorfer.batmate.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.batmate.designsystem.theme.BatColors
import at.stefanirndorfer.batmate.designsystem.theme.BatContainerColors
import at.stefanirndorfer.batmate.designsystem.theme.BatDesign
import at.stefanirndorfer.batmate.designsystem.theme.brandContainerColors
import at.stefanirndorfer.batmate.designsystem.theme.liveContainerColors

enum class BatAvatarTint {
    BRAND,
    LIVE,
}

/** Decorative: the adjacent name already identifies the person, so it is hidden from accessibility services. */
@Composable
fun BatInitialsAvatar(
    initials: String,
    modifier: Modifier = Modifier,
    tint: BatAvatarTint = BatAvatarTint.BRAND,
) {
    val (container, content) = tint.colors(BatDesign.colors)
    Box(
        modifier = modifier
            .size(BatDesign.sizing.avatarSmall)
            .background(container, BatDesign.shapes.medium)
            .clearAndSetSemantics {},
        contentAlignment = Alignment.Center,
    ) {
        Text(text = initials, color = content, style = BatDesign.typography.bodyStrong)
    }
}

private fun BatAvatarTint.colors(colors: BatColors): BatContainerColors = when (this) {
    BatAvatarTint.BRAND -> colors.brandContainerColors
    BatAvatarTint.LIVE -> colors.liveContainerColors
}

@BatScreenPreview
@Composable
private fun BatInitialsAvatarPreview() {
    BatPreviewSurface {
        Row(horizontalArrangement = Arrangement.spacedBy(BatDesign.spacing.md)) {
            BatInitialsAvatar(initials = "MH")
            BatInitialsAvatar(initials = "SF", tint = BatAvatarTint.LIVE)
        }
    }
}
