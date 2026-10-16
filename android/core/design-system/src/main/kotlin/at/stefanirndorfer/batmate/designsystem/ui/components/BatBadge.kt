package at.stefanirndorfer.batmate.designsystem.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import at.stefanirndorfer.batmate.designsystem.preview.BatPreviewSurface
import at.stefanirndorfer.batmate.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.batmate.designsystem.theme.BatColors
import at.stefanirndorfer.batmate.designsystem.theme.BatDesign

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

private fun BatBadgeStyle.colors(colors: BatColors): Pair<Color, Color> = when (this) {
    BatBadgeStyle.BRAND -> colors.brandContainer to colors.onBrandContainer
    BatBadgeStyle.WARNING -> colors.warningContainer to colors.onWarningContainer
    BatBadgeStyle.LIVE -> colors.liveContainer to colors.onLiveContainer
    BatBadgeStyle.NEUTRAL -> colors.surfaceMuted to colors.textSecondary
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
