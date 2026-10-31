package at.stefanirndorfer.batmate.designsystem.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import at.stefanirndorfer.batmate.designsystem.preview.BatPreviewSurface
import at.stefanirndorfer.batmate.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.batmate.designsystem.theme.BatDesign

/** The whole row is the target and the switch is decorative: state is announced through the toggleable semantics. */
@Composable
fun BatSwitchRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    supportingText: String? = null,
) {
    val colors = BatDesign.colors
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = BatDesign.sizing.minTouchTarget)
            .toggleable(value = checked, role = Role.Switch, onValueChange = onCheckedChange)
            .padding(vertical = BatDesign.spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(BatDesign.spacing.md),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, color = colors.textPrimary, style = BatDesign.typography.bodyStrong)
            if (supportingText != null) {
                Text(text = supportingText, color = colors.textSecondary, style = BatDesign.typography.caption)
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = null,
            colors = SwitchDefaults.colors(
                checkedThumbColor = colors.onBrand,
                checkedTrackColor = colors.brand,
                uncheckedThumbColor = colors.textSecondary,
                uncheckedTrackColor = colors.surface,
                uncheckedBorderColor = colors.borderStrong,
            ),
        )
    }
}

@BatScreenPreview
@Composable
private fun BatSwitchRowPreview() {
    BatPreviewSurface {
        Column {
            BatSwitchRow(label = "Usage statistics", supportingText = "Anonymous", checked = true, onCheckedChange = {})
            BatSwitchRow(label = "Usage statistics", checked = false, onCheckedChange = {})
            BatSwitchRow(
                label = "Nutzungsstatistiken und Absturzberichte für eine sehr lange Beschriftung",
                supportingText = "Anonym. Enthält nie Namen oder Eingaben, auch nicht bei sehr großer Schrift.",
                checked = false,
                onCheckedChange = {},
            )
        }
    }
}
