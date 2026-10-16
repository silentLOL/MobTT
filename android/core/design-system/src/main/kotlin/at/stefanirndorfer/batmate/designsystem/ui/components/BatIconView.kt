package at.stefanirndorfer.batmate.designsystem.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import at.stefanirndorfer.batmate.designsystem.icon.BatIcon
import at.stefanirndorfer.batmate.designsystem.preview.BatPreviewSurface
import at.stefanirndorfer.batmate.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.batmate.designsystem.theme.BatDesign

/** A null [contentDescription] marks the icon as decorative. */
@Composable
fun BatIconView(
    @DrawableRes icon: Int,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = BatDesign.colors.textSecondary,
    size: Dp = BatDesign.sizing.icon,
) {
    Icon(
        painter = painterResource(icon),
        contentDescription = contentDescription,
        modifier = modifier.size(size),
        tint = tint,
    )
}

@BatScreenPreview
@Composable
private fun BatIconViewPreview() {
    BatPreviewSurface {
        Row(horizontalArrangement = Arrangement.spacedBy(BatDesign.spacing.md)) {
            BatIconView(icon = BatIcon.Home, contentDescription = null)
            BatIconView(icon = BatIcon.Live, contentDescription = null, tint = BatDesign.colors.brand)
            BatIconView(icon = BatIcon.Club, contentDescription = null, size = BatDesign.sizing.iconLarge)
        }
    }
}
