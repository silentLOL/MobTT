package at.stefanirndorfer.mobtt.designsystem.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import at.stefanirndorfer.mobtt.designsystem.preview.BatPreviewSurface
import at.stefanirndorfer.mobtt.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.mobtt.designsystem.theme.BatDesign

/** One choice out of a few [options], shown side by side; [selectedIndex] is the chosen one. */
@Composable
fun BatSegmentedControl(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = BatDesign.colors
    SingleChoiceSegmentedButtonRow(modifier = modifier.fillMaxWidth()) {
        options.forEachIndexed { index, option ->
            SegmentedButton(
                selected = index == selectedIndex,
                onClick = { onSelect(index) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = colors.brandContainer,
                    activeContentColor = colors.onBrandContainer,
                    activeBorderColor = colors.brandBorder,
                    inactiveContainerColor = colors.surface,
                    inactiveContentColor = colors.textSecondary,
                    inactiveBorderColor = colors.border,
                ),
                icon = {},
            ) {
                Text(text = option, style = BatDesign.typography.bodyStrong)
            }
        }
    }
}

@BatScreenPreview
@Composable
private fun BatSegmentedControlPreview() {
    BatPreviewSurface {
        BatSegmentedControl(options = listOf("12 months", "All"), selectedIndex = 0, onSelect = {})
    }
}
