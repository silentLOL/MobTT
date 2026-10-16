package at.stefanirndorfer.batmate.designsystem.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import at.stefanirndorfer.batmate.designsystem.preview.BatPreviewSurface
import at.stefanirndorfer.batmate.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.batmate.designsystem.theme.BatDesign

/** The pulse only decorates a live pill; the text carries the state for assistive technology. */
@Composable
fun BatLiveStatusPill(
    text: String,
    isLive: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = BatDesign.colors
    val container = if (isLive) {
        colors.liveContainer
    } else {
        colors.surfaceMuted
    }
    val content = if (isLive) {
        colors.onLiveContainer
    } else {
        colors.textSecondary
    }
    Row(
        modifier = modifier
            .clip(BatDesign.shapes.full)
            .background(container)
            .padding(horizontal = BatDesign.spacing.sm, vertical = BatDesign.spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(BatDesign.spacing.xs),
    ) {
        if (isLive) {
            PulsingDot()
        }
        Text(text = text, color = content, style = BatDesign.typography.overline)
    }
}

@Composable
internal fun PulsingDot(modifier: Modifier = Modifier) {
    val alpha by rememberInfiniteTransition(label = "live-pulse").animateFloat(
        initialValue = 1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(tween(durationMillis = 900), RepeatMode.Reverse),
        label = "live-pulse-alpha",
    )
    Box(
        modifier = modifier
            .size(BatDesign.sizing.liveIndicator)
            .alpha(alpha)
            .background(BatDesign.colors.liveIndicator, CircleShape),
    )
}

@BatScreenPreview
@Composable
private fun BatLiveStatusPillPreview() {
    BatPreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(BatDesign.spacing.sm)) {
            BatLiveStatusPill(text = "LIVE • Table 2", isLive = true)
            BatLiveStatusPill(text = "Finished • Table 1", isLive = false)
        }
    }
}
