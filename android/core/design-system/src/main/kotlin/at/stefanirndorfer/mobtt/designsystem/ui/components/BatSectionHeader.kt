package at.stefanirndorfer.mobtt.designsystem.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import at.stefanirndorfer.mobtt.designsystem.preview.BatPreviewSurface
import at.stefanirndorfer.mobtt.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.mobtt.designsystem.theme.BatDesign

/** [actionLabel] and [onAction] must be provided together to show the trailing action. */
@Composable
fun BatSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = title.uppercase(),
            modifier = Modifier
                .weight(1f)
                .semantics { heading() },
            color = BatDesign.colors.textPrimary,
            style = BatDesign.typography.bodyStrong,
            overflow = TextOverflow.Ellipsis,
        )
        if (actionLabel != null && onAction != null) {
            Text(
                text = actionLabel,
                modifier = Modifier
                    .heightIn(min = BatDesign.sizing.minTouchTarget)
                    .clickable(role = Role.Button, onClick = onAction)
                    .padding(horizontal = BatDesign.spacing.sm)
                    .padding(top = BatDesign.spacing.md),
                color = BatDesign.colors.brand,
                style = BatDesign.typography.captionStrong,
            )
        }
    }
}

@BatScreenPreview
@Composable
private fun BatSectionHeaderPreview() {
    BatPreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(BatDesign.spacing.md)) {
            BatSectionHeader(title = "Upcoming matches")
            BatSectionHeader(title = "Favorites", actionLabel = "+ Add", onAction = {})
        }
    }
}
