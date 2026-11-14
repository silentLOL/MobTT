package at.stefanirndorfer.mobtt.designsystem.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import at.stefanirndorfer.mobtt.designsystem.icon.BatIcon
import at.stefanirndorfer.mobtt.designsystem.preview.BatPreviewSurface
import at.stefanirndorfer.mobtt.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.mobtt.designsystem.theme.BatDesign

@Composable
fun BatEmptyState(
    @DrawableRes icon: Int,
    title: String,
    body: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(BatDesign.spacing.xxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(BatDesign.spacing.md),
    ) {
        BatIconView(
            icon = icon,
            contentDescription = null,
            tint = BatDesign.colors.textTertiary,
            size = BatDesign.sizing.iconLarge,
        )
        Text(
            text = title,
            modifier = Modifier.semantics { heading() },
            color = BatDesign.colors.textPrimary,
            style = BatDesign.typography.title,
            textAlign = TextAlign.Center,
        )
        Text(
            text = body,
            color = BatDesign.colors.textSecondary,
            style = BatDesign.typography.body,
            textAlign = TextAlign.Center,
        )
    }
}

@BatScreenPreview
@Composable
private fun BatEmptyStatePreview() {
    BatPreviewSurface {
        BatEmptyState(
            icon = BatIcon.Live,
            title = "Nothing live right now",
            body = "Fixtures in progress will show up here as they are played.",
        )
    }
}
