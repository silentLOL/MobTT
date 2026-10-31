package at.stefanirndorfer.mobtt.designsystem.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import at.stefanirndorfer.mobtt.designsystem.preview.BatPreviewSurface
import at.stefanirndorfer.mobtt.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.mobtt.designsystem.theme.BatDesign

enum class BatFormResult {
    WIN,
    LOSS,
}

/** [label] is the short visible letter; [contentDescription] spells the outcome out for screen readers. */
@Composable
fun BatFormResultView(
    result: BatFormResult,
    label: String,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    val colors = BatDesign.colors
    val container = when (result) {
        BatFormResult.WIN -> colors.success
        BatFormResult.LOSS -> colors.danger
    }
    Box(
        modifier = modifier
            .size(BatDesign.sizing.formResult)
            .background(container, BatDesign.shapes.small)
            .semantics { this.contentDescription = contentDescription },
        contentAlignment = Alignment.Center,
    ) {
        Text(text = label, color = colors.onSuccess, style = BatDesign.typography.bodyStrong)
    }
}

@BatScreenPreview
@Composable
private fun BatFormResultViewPreview() {
    BatPreviewSurface {
        Row(horizontalArrangement = Arrangement.spacedBy(BatDesign.spacing.sm)) {
            BatFormResultView(BatFormResult.WIN, "W", "Win")
            BatFormResultView(BatFormResult.WIN, "W", "Win")
            BatFormResultView(BatFormResult.LOSS, "L", "Loss")
            BatFormResultView(BatFormResult.WIN, "W", "Win")
        }
    }
}
