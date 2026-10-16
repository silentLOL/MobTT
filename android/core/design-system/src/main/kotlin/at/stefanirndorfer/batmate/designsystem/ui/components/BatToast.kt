package at.stefanirndorfer.batmate.designsystem.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import at.stefanirndorfer.batmate.designsystem.icon.BatIcon
import at.stefanirndorfer.batmate.designsystem.preview.BatPreviewSurface
import at.stefanirndorfer.batmate.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.batmate.designsystem.theme.BatDesign
import at.stefanirndorfer.batmate.designsystem.theme.BatShadow
import at.stefanirndorfer.batmate.designsystem.theme.batShadow

@Composable
fun BatToast(
    message: String,
    modifier: Modifier = Modifier,
) {
    val colors = BatDesign.colors
    val shape = BatDesign.shapes.large
    Row(
        modifier = modifier
            .batShadow(BatShadow.XL, shape)
            .background(colors.toast, shape)
            .padding(horizontal = BatDesign.spacing.lg, vertical = BatDesign.spacing.md)
            .semantics { liveRegion = LiveRegionMode.Polite },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(BatDesign.spacing.sm),
    ) {
        BatIconView(icon = BatIcon.Check, contentDescription = null, tint = colors.toastIcon, size = BatDesign.sizing.iconSmall)
        Text(text = message, color = colors.onToast, style = BatDesign.typography.captionStrong)
    }
}

@BatScreenPreview
@Composable
private fun BatToastPreview() {
    BatPreviewSurface {
        BatToast(message = "Settings saved")
    }
}
