package at.stefanirndorfer.mobtt.designsystem.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import at.stefanirndorfer.mobtt.designsystem.preview.BatPreviewSurface
import at.stefanirndorfer.mobtt.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.mobtt.designsystem.theme.BatDesign
import at.stefanirndorfer.mobtt.designsystem.theme.BatShadow
import at.stefanirndorfer.mobtt.designsystem.theme.batShadow

/** Pass [onClick] to make the whole card a single tappable target. */
@Composable
fun BatCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(BatDesign.spacing.lg),
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = BatDesign.colors
    val shape = BatDesign.shapes.large
    val cardModifier = modifier.batShadow(BatShadow.SM, shape)
    val border = BorderStroke(BatDesign.sizing.borderThin, colors.border)
    val inner: @Composable () -> Unit = {
        Column(modifier = Modifier.padding(contentPadding), content = content)
    }
    if (onClick != null) {
        Surface(
            onClick = onClick,
            modifier = cardModifier,
            shape = shape,
            color = colors.surface,
            contentColor = colors.textPrimary,
            border = border,
            content = inner,
        )
    } else {
        Surface(
            modifier = cardModifier,
            shape = shape,
            color = colors.surface,
            contentColor = colors.textPrimary,
            border = border,
            content = inner,
        )
    }
}

@BatScreenPreview
@Composable
private fun BatCardPreview() {
    BatPreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(BatDesign.spacing.md)) {
            BatCard(modifier = Modifier.fillMaxWidth()) {
                Text("Static card", style = BatDesign.typography.bodyStrong)
            }
            BatCard(modifier = Modifier.fillMaxWidth(), onClick = {}) {
                Text("Tappable card", style = BatDesign.typography.bodyStrong)
            }
        }
    }
}

@BatScreenPreview
@Composable
private fun BatCardContentPreview() {
    BatPreviewSurface {
        BatCard(
            modifier = Modifier.fillMaxWidth(),
            onClick = {},
            contentPadding = PaddingValues(BatDesign.spacing.xl),
        ) {
            Text("Match day", style = BatDesign.typography.bodyStrong)
            Text(
                "A longer description that wraps onto several lines so the padding and border can be checked at larger font scales.",
                color = BatDesign.colors.textSecondary,
                style = BatDesign.typography.body,
            )
        }
    }
}
