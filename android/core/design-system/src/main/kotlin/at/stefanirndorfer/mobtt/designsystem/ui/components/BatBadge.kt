package at.stefanirndorfer.mobtt.designsystem.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import at.stefanirndorfer.mobtt.designsystem.preview.BatPreviewSurface
import at.stefanirndorfer.mobtt.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.mobtt.designsystem.theme.BatColors
import at.stefanirndorfer.mobtt.designsystem.theme.BatContainerColors
import at.stefanirndorfer.mobtt.designsystem.theme.BatDesign
import at.stefanirndorfer.mobtt.designsystem.theme.brandContainerColors
import at.stefanirndorfer.mobtt.designsystem.theme.liveContainerColors
import at.stefanirndorfer.mobtt.designsystem.theme.neutralContainerColors
import at.stefanirndorfer.mobtt.designsystem.theme.warningContainerColors

enum class BatBadgeStyle {
    BRAND,
    WARNING,
    LIVE,
    NEUTRAL,
}

@Composable
fun BatBadge(
    text: String,
    modifier: Modifier = Modifier,
    style: BatBadgeStyle = BatBadgeStyle.BRAND,
) {
    val (container, content) = style.colors(BatDesign.colors)
    Text(
        text = text,
        modifier = modifier
            .clip(BatDesign.shapes.full)
            .background(container)
            .padding(horizontal = BatDesign.spacing.sm, vertical = BatDesign.spacing.xxs),
        color = content,
        style = BatDesign.typography.captionStrong,
    )
}

private fun BatBadgeStyle.colors(colors: BatColors): BatContainerColors = when (this) {
    BatBadgeStyle.BRAND -> colors.brandContainerColors
    BatBadgeStyle.WARNING -> colors.warningContainerColors
    BatBadgeStyle.LIVE -> colors.liveContainerColors
    BatBadgeStyle.NEUTRAL -> colors.neutralContainerColors
}

@BatScreenPreview
@Composable
private fun BatBadgePreview() {
    BatPreviewSurface {
        Row(horizontalArrangement = Arrangement.spacedBy(BatDesign.spacing.sm)) {
            BatBadge(text = "Brand")
            BatBadge(text = "Warning", style = BatBadgeStyle.WARNING)
            BatBadge(text = "Live", style = BatBadgeStyle.LIVE)
            BatBadge(text = "Neutral", style = BatBadgeStyle.NEUTRAL)
        }
    }
}
