package at.stefanirndorfer.batmate.designsystem.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import at.stefanirndorfer.batmate.designsystem.icon.BatIcon
import at.stefanirndorfer.batmate.designsystem.preview.BatPreviewSurface
import at.stefanirndorfer.batmate.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.batmate.designsystem.theme.BatDesign

/** The round surface is [at.stefanirndorfer.batmate.designsystem.theme.BatSizing.iconButton]; the tap area is padded up to the minimum touch target. */
@Composable
fun BatIconButton(
    @DrawableRes icon: Int,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = BatDesign.colors.textSecondary,
) {
    val sizing = BatDesign.sizing
    Box(
        modifier = modifier
            .sizeIn(minWidth = sizing.minTouchTarget, minHeight = sizing.minTouchTarget)
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(sizing.iconButton)
                .clip(CircleShape)
                .background(BatDesign.colors.surfaceMuted),
            contentAlignment = Alignment.Center,
        ) {
            BatIconView(icon = icon, contentDescription = contentDescription, tint = tint)
        }
    }
}

@BatScreenPreview
@Composable
private fun BatIconButtonPreview() {
    BatPreviewSurface {
        BatIconButton(
            icon = BatIcon.Settings,
            contentDescription = "Settings",
            onClick = {},
        )
    }
}
