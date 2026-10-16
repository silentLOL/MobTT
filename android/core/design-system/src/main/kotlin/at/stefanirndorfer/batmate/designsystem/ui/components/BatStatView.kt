package at.stefanirndorfer.batmate.designsystem.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import at.stefanirndorfer.batmate.designsystem.preview.BatPreviewSurface
import at.stefanirndorfer.batmate.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.batmate.designsystem.theme.BatDesign

/** Label and value are announced as one element, e.g. "QTTR points, 1842". */
@Composable
fun BatStatView(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    onHero: Boolean = false,
) {
    val colors = BatDesign.colors
    val labelColor = if (onHero) {
        colors.onHeroMuted
    } else {
        colors.textTertiary
    }
    val valueColor = if (onHero) {
        colors.onHero
    } else {
        colors.textPrimary
    }
    Column(modifier = modifier.semantics(mergeDescendants = true) { contentDescription = "$label, $value" }) {
        Text(text = label.uppercase(), color = labelColor, style = BatDesign.typography.overline)
        Text(text = value, color = valueColor, style = BatDesign.typography.headline)
    }
}

@BatScreenPreview
@Composable
private fun BatStatViewPreview() {
    BatPreviewSurface {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(BatDesign.spacing.lg),
        ) {
            BatStatView(label = "QTTR", value = "1842", modifier = Modifier.weight(1f))
            BatStatView(label = "Rank", value = "#45", modifier = Modifier.weight(1f))
            BatStatView(label = "Record", value = "34:12", modifier = Modifier.weight(1f))
        }
    }
}
