package at.stefanirndorfer.batmate.designsystem.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import at.stefanirndorfer.batmate.designsystem.icon.BatIcon
import at.stefanirndorfer.batmate.designsystem.preview.BatPreviewSurface
import at.stefanirndorfer.batmate.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.batmate.designsystem.theme.BatDesign
import at.stefanirndorfer.batmate.designsystem.theme.BatShadow
import at.stefanirndorfer.batmate.designsystem.theme.batShadow

@Composable
fun BatPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val colors = BatDesign.colors
    Button(
        onClick = onClick,
        modifier = modifier
            .heightIn(min = BatDesign.sizing.minTouchTarget)
            .batShadow(BatShadow.LG, BatDesign.shapes.medium, tint = colors.brand),
        enabled = enabled,
        shape = BatDesign.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = colors.brand,
            contentColor = colors.onBrand,
            disabledContainerColor = colors.surfaceMuted,
            disabledContentColor = colors.textTertiary,
        ),
        contentPadding = PaddingValues(horizontal = BatDesign.spacing.lg, vertical = BatDesign.spacing.md),
    ) {
        Text(text = text, style = BatDesign.typography.bodyStrong)
    }
}

@Composable
fun BatSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    @DrawableRes leadingIcon: Int? = null,
    enabled: Boolean = true,
) {
    val colors = BatDesign.colors
    Button(
        onClick = onClick,
        modifier = modifier.heightIn(min = BatDesign.sizing.minTouchTarget),
        enabled = enabled,
        shape = BatDesign.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = colors.surfaceMuted,
            contentColor = colors.textPrimary,
            disabledContainerColor = colors.surfaceMuted,
            disabledContentColor = colors.textTertiary,
        ),
        contentPadding = PaddingValues(horizontal = BatDesign.spacing.lg, vertical = BatDesign.spacing.md),
    ) {
        if (leadingIcon != null) {
            BatIconView(icon = leadingIcon, contentDescription = null, tint = colors.textSecondary, size = BatDesign.sizing.iconSmall)
            Spacer(Modifier.width(BatDesign.spacing.sm))
        }
        Text(text = text, style = BatDesign.typography.bodyStrong)
    }
}

@BatScreenPreview
@Composable
private fun BatButtonsPreview() {
    BatPreviewSurface {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(BatDesign.spacing.md),
        ) {
            BatPrimaryButton(text = "Save settings", onClick = {}, modifier = Modifier.fillMaxWidth())
            BatPrimaryButton(text = "Disabled", onClick = {}, enabled = false, modifier = Modifier.fillMaxWidth())
            BatSecondaryButton(text = "Test connection", onClick = {}, modifier = Modifier.fillMaxWidth())
            BatSecondaryButton(text = "Sync", onClick = {}, leadingIcon = BatIcon.Refresh)
        }
    }
}
