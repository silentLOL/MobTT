package at.stefanirndorfer.batmate.designsystem.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import at.stefanirndorfer.batmate.designsystem.icon.BatIcon
import at.stefanirndorfer.batmate.designsystem.preview.BatPreviewSurface
import at.stefanirndorfer.batmate.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.batmate.designsystem.theme.BatDesign

/** A radio-style row: the whole row is the target and the check mark is decorative, selection is announced through semantics. */
@Composable
fun BatSelectableRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    supportingText: String? = null,
) {
    val colors = BatDesign.colors
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = BatDesign.sizing.minTouchTarget)
            .selectable(selected = selected, role = Role.RadioButton, onClick = onClick)
            .padding(vertical = BatDesign.spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(BatDesign.spacing.md),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                color = colors.textPrimary,
                style = BatDesign.typography.bodyStrong,
                overflow = TextOverflow.Ellipsis,
            )
            if (supportingText != null) {
                Text(
                    text = supportingText,
                    color = colors.textSecondary,
                    style = BatDesign.typography.caption,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        if (selected) {
            BatIconView(
                icon = BatIcon.Check,
                contentDescription = null,
                tint = colors.brand,
                modifier = Modifier.size(BatDesign.sizing.icon),
            )
        }
    }
}

@BatScreenPreview
@Composable
private fun BatSelectableRowPreview() {
    BatPreviewSurface {
        Column {
            BatSelectableRow(label = "Follow system", selected = true, onClick = {})
            BatSelectableRow(label = "Light", selected = false, onClick = {})
            BatSelectableRow(
                label = "Katharina Habsburg-Lothringen-Esterházy",
                supportingText = "Union Tischtennisclub Wien Nord-West",
                selected = false,
                onClick = {},
            )
        }
    }
}
