package at.stefanirndorfer.mobtt.designsystem.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import at.stefanirndorfer.mobtt.designsystem.icon.BatIcon
import at.stefanirndorfer.mobtt.designsystem.preview.BatPreviewSurface
import at.stefanirndorfer.mobtt.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.mobtt.designsystem.theme.BatDesign

/** A link at the bottom of a [BatInfoSheet]: [text] on the button, [onClick] to follow it. */
class BatInfoSheetLink(val text: String, val onClick: () -> Unit)

/**
 * A bottom sheet that explains something: [title], [body] and an optional [link]. It closes with a swipe or its close
 * button, labelled [closeLabel], and scrolls when the text does not fit.
 */
@Composable
fun BatInfoSheet(
    title: String,
    body: String,
    closeLabel: String,
    onDismiss: () -> Unit,
    link: BatInfoSheetLink? = null,
) {
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = BatDesign.colors.background) {
        BatInfoSheetContent(title = title, body = body, closeLabel = closeLabel, onDismiss = onDismiss, link = link)
    }
}

@Composable
private fun BatInfoSheetContent(
    title: String,
    body: String,
    closeLabel: String,
    onDismiss: () -> Unit,
    link: BatInfoSheetLink?,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = BatDesign.spacing.lg)
            .padding(bottom = BatDesign.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(BatDesign.spacing.lg),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(BatDesign.spacing.sm)) {
            Text(
                text = title,
                modifier = Modifier
                    .weight(1f)
                    .semantics { heading() },
                color = BatDesign.colors.textPrimary,
                style = BatDesign.typography.headline,
            )
            BatIconButton(icon = BatIcon.Close, contentDescription = closeLabel, onClick = onDismiss)
        }
        Text(text = body, color = BatDesign.colors.textSecondary, style = BatDesign.typography.body)
        link?.let { BatSecondaryButton(text = it.text, onClick = it.onClick, modifier = Modifier.fillMaxWidth()) }
    }
}

@BatScreenPreview
@Composable
private fun BatInfoSheetContentPreview() {
    BatPreviewSurface {
        BatInfoSheetContent(
            title = "About the Rating history",
            body = "The line is the estimate of the Player\'s playing strength.\n\nThe ± number is how certain that estimate is.",
            closeLabel = "Close",
            onDismiss = {},
            link = BatInfoSheetLink("Learn more at Ratings Central") {},
        )
    }
}
